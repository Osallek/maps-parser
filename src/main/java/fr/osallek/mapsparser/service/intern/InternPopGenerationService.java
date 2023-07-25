package fr.osallek.mapsparser.service.intern;

import fr.osallek.mapsparser.common.CollecUtils;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.CommunePopulation;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataJenks;
import fr.osallek.mapsparser.service.object.DataNode;
import fr.osallek.mapsparser.service.object.DataPopulation;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.DepartementPopulation;
import fr.osallek.mapsparser.service.object.Region;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class InternPopGenerationService extends GenerationService {

    public InternPopGenerationService(Environment environment) {
        super("Osallek", "https://maps.osallek.net", environment);
    }

    @Override
    public void addData(Data data) {
        doRanksPop(data, DataPopulation::getPopulation, DataJenks::getPopulation, DataJenks::doPopulation, DataPopulation::setCountryRanks, DataPopulation::setPercentCountry, DepartementPopulation::setRegionRanks, DepartementPopulation::setPercentRegion, CommunePopulation::setDepartementRanks, CommunePopulation::setPercentDepartement);
        doRanksPop(data, DataPopulation::getBirth, DataJenks::getBirth, DataJenks::doBirth, DataPopulation::setBirthCountryRanks, DataPopulation::setBirthPercentCountry, DepartementPopulation::setBirthRegionRanks, DepartementPopulation::setBirthPercentRegion, CommunePopulation::setBirthDepartementRanks, CommunePopulation::setBirthPercentDepartement);
        doRanksPop(data, DataPopulation::getDeath, DataJenks::getDeath, DataJenks::doDeath, DataPopulation::setDeathCountryRanks, DataPopulation::setDeathPercentCountry, DepartementPopulation::setDeathRegionRanks, DepartementPopulation::setDeathPercentRegion, CommunePopulation::setDeathDepartementRanks, CommunePopulation::setDeathPercentDepartement);
        doDensity(data, DataPopulation::getDensity, DataJenks::getDensity, DataJenks::doDensity, DataPopulation::setDensityCountryRanks, DepartementPopulation::setDensityRegionRanks, CommunePopulation::setDensityDepartementRanks);
        doArea(data);
        doDensity(data, DataPopulation::getBirthPerCapita, DataJenks::getBirthPerCapita, DataJenks::doBirthPerCapita, DataPopulation::setBirthPerCapitaCountryRanks, DepartementPopulation::setBirthPerCapitaRegionRanks, CommunePopulation::setBirthPerCapitaDepartementRanks);
        doDensity(data, DataPopulation::getDeathPerCapita, DataJenks::getDeathPerCapita, DataJenks::doDeathPerCapita, DataPopulation::setDeathPerCapitaCountryRanks, DepartementPopulation::setDeathPerCapitaRegionRanks, CommunePopulation::setDeathPerCapitaDepartementRanks);
    }

    private void doArea(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            if (!this.environment.matchesProfiles("dev") || CollectionUtils.isEmpty(data.getCommunes().getJenks().getArea())) {
                data.getCommunes()
                    .getJenks()
                    .doArea(data.getCommunes()
                                .getCommunes()
                                .values()
                                .stream()
                                .map(Commune::getArea)
                                .filter(Objects::nonNull)
                                .map(BigDecimal::doubleValue)
                                .toList());
            }

            List<BigDecimal> areas = data.getCommunes().getCommunes().values().stream().map(DataNode::getArea).filter(Objects::nonNull).sorted().toList();
            Map<String, List<BigDecimal>> departementsArea = data.getCommunes()
                                                                 .getCommunes()
                                                                 .values()
                                                                 .stream()
                                                                 .filter(c -> c.getArea() != null)
                                                                 .collect(Collectors.groupingBy(Commune::getDepartement, Collectors.mapping(DataNode::getArea, Collectors.toList())));
            Map<String, List<BigDecimal>> regionsArea = data.getCommunes()
                                                            .getCommunes()
                                                            .values()
                                                            .stream()
                                                            .filter(c -> c.getArea() != null)
                                                            .filter(c -> data.getDepartements().getDepartements().containsKey(c.getDepartement()))
                                                            .collect(Collectors.groupingBy(c -> data.getDepartements()
                                                                                                    .getDepartements()
                                                                                                    .get(c.getDepartement())
                                                                                                    .getRegion(), Collectors.mapping(DataNode::getArea, Collectors.toList())));
            departementsArea.values().forEach(Collections::sort);
            regionsArea.values().forEach(Collections::sort);

            data.getCommunes().getCommunes().values().parallelStream().filter(commune -> commune.getArea() != null).forEach(commune -> {
                commune.setAreaCountryRanks(areas.size() - CollecUtils.rank(areas, commune.getArea()));

                Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                if (departement != null) {
                    commune.setAreaDepartementRanks(
                            departementsArea.get(departement.getId()).size() - CollecUtils.rank(departementsArea.get(departement.getId()), commune.getArea()));
                    commune.setAreaRegionRanks(
                            regionsArea.get(departement.getRegion()).size() - CollecUtils.rank(regionsArea.get(departement.getRegion()), commune.getArea()));
                }
            });
        }

        if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
            if (!this.environment.matchesProfiles("dev") || CollectionUtils.isEmpty(data.getDepartements().getJenks().getArea())) {
                data.getDepartements()
                    .getJenks()
                    .doArea(data.getDepartements()
                                .getDepartements()
                                .values()
                                .stream()
                                .map(DataNode::getArea)
                                .filter(Objects::nonNull)
                                .map(BigDecimal::doubleValue)
                                .toList());
            }

            List<BigDecimal> areas = data.getDepartements()
                                         .getDepartements()
                                         .values()
                                         .stream()
                                         .map(DataNode::getArea)
                                         .filter(Objects::nonNull)
                                         .sorted()
                                         .toList();
            Map<String, List<BigDecimal>> regionsArea = data.getDepartements()
                                                            .getDepartements()
                                                            .values()
                                                            .stream()
                                                            .filter(c -> c.getArea() != null)
                                                            .collect(Collectors.groupingBy(Departement::getRegion, Collectors.mapping(DataNode::getArea, Collectors.toList())));
            regionsArea.values().forEach(Collections::sort);

            data.getDepartements().getDepartements().values().parallelStream().filter(departement -> departement.getArea() != null).forEach(departement -> {
                departement.setAreaCountryRanks(areas.size() - CollecUtils.rank(areas, departement.getArea()));
                departement.setAreaRegionRanks(
                        regionsArea.get(departement.getRegion()).size() - CollecUtils.rank(regionsArea.get(departement.getRegion()), departement.getArea()));
            });
        }

        if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
            if (!this.environment.matchesProfiles("dev") || CollectionUtils.isEmpty(data.getRegions().getJenks().getArea())) {
                data.getRegions()
                    .getJenks()
                    .doArea(data.getRegions()
                                .getRegions()
                                .values()
                                .stream()
                                .map(DataNode::getArea)
                                .filter(Objects::nonNull)
                                .map(BigDecimal::doubleValue)
                                .toList());
            }

            List<BigDecimal> areas = data.getRegions().getRegions().values().stream().map(DataNode::getArea).filter(Objects::nonNull).sorted().toList();
            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getArea() != null)
                .forEach(region -> region.setAreaCountryRanks(areas.size() - CollecUtils.rank(areas, region.getArea())));
        }
    }

    private void doRanksPop(Data data, Function<DataPopulation, SortedMap<Integer, Integer>> mapper,
                            Function<DataJenks, SortedMap<Integer, SortedSet<Double>>> jenksMapper,
                            BiConsumer<DataJenks, Map<Integer, List<Double>>> jenksConsumer, TriConsumer<DataPopulation, Integer, Integer> countryRankConsumer,
                            TriConsumer<DataPopulation, Integer, Double> countryPercentConsumer,
                            TriConsumer<DepartementPopulation, Integer, Integer> regionRankConsumer,
                            TriConsumer<DepartementPopulation, Integer, Double> regionPercentConsumer,
                            TriConsumer<CommunePopulation, Integer, Integer> departementRankConsumer,
                            TriConsumer<CommunePopulation, Integer, Double> departementPercentConsumer) {
        Map<Integer, BigDecimal> totalData = new HashMap<>();
        Map<String, Map<Integer, BigDecimal>> totalDepartementsData = new HashMap<>();
        Map<String, Map<Integer, BigDecimal>> totalRegionsData = new HashMap<>();

        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            SortedMap<Integer, List<Double>> datas = new TreeMap<>();

            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .map(Commune::getPopulation)
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> datas.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            datas.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getCommunes().getJenks()))) {
                jenksConsumer.accept(data.getCommunes().getJenks(), datas);
            }

            Map<String, SortedMap<Integer, List<Double>>> departementsDatas = new HashMap<>();
            Map<String, SortedMap<Integer, List<Double>>> regionsDatas = new HashMap<>();
            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(commune.getPopulation())))
                .forEach(commune -> {
                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        departementsDatas.putIfAbsent(commune.getDepartement(), new TreeMap<>());
                        regionsDatas.putIfAbsent(departement.getRegion(), new TreeMap<>());

                        mapper.apply(commune.getPopulation()).forEach((year, pop) -> {
                            departementsDatas.get(commune.getDepartement()).putIfAbsent(year, new ArrayList<>());
                            regionsDatas.get(departement.getRegion()).putIfAbsent(year, new ArrayList<>());

                            departementsDatas.get(commune.getDepartement()).get(year).add(pop.doubleValue());
                            regionsDatas.get(departement.getRegion()).get(year).add(pop.doubleValue());
                        });
                    }
                });

            departementsDatas.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);
            regionsDatas.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);

            datas.forEach((year, p) -> totalData.put(year, BigDecimal.valueOf(p.stream().mapToInt(Double::intValue).sum())));
            departementsDatas.forEach((dep, map) -> totalDepartementsData.put(dep, map.entrySet()
                                                                                      .stream()
                                                                                      .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(e.getValue()
                                                                                                                                                            .stream()
                                                                                                                                                            .mapToInt(Double::intValue)
                                                                                                                                                            .sum())))));
            regionsDatas.forEach((dep, map) -> totalRegionsData.put(dep, map.entrySet()
                                                                            .stream()
                                                                            .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(e.getValue()
                                                                                                                                                  .stream()
                                                                                                                                                  .mapToInt(Double::intValue)
                                                                                                                                                  .sum())))));

            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(commune.getPopulation())))
                .forEach(commune -> mapper.apply(commune.getPopulation()).forEach((year, pop) -> {
                    countryRankConsumer.accept(commune.getPopulation(), year, datas.get(year).size() - CollecUtils.rank(datas.get(year), pop.doubleValue()));

                    if (totalData.containsKey(year)) {
                        countryPercentConsumer.accept(commune.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                               .multiply(BigDecimal.valueOf(100))
                                                                                               .divide(totalData.get(year), 3, RoundingMode.HALF_UP)
                                                                                               .doubleValue());
                    }

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        departementRankConsumer.accept(commune.getPopulation(), year, departementsDatas.get(commune.getDepartement()).get(year).size()
                                                                                      - CollecUtils.rank(departementsDatas.get(commune.getDepartement())
                                                                                                                          .get(year), pop.doubleValue()));

                        if (totalDepartementsData.containsKey(departement.getId()) && totalDepartementsData.get(departement.getId()).containsKey(year)) {
                            departementPercentConsumer.accept(commune.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                                       .multiply(BigDecimal.valueOf(100))
                                                                                                       .divide(totalDepartementsData.get(departement.getId())
                                                                                                                                    .get(year), 3, RoundingMode.HALF_UP)
                                                                                                       .doubleValue());
                        }

                        regionRankConsumer.accept(commune.getPopulation(), year, regionsDatas.get(departement.getRegion()).get(year).size()
                                                                                 - CollecUtils.rank(regionsDatas.get(departement.getRegion())
                                                                                                                .get(year), pop.doubleValue()));

                        if (totalRegionsData.containsKey(departement.getRegion()) && totalRegionsData.get(departement.getRegion()).containsKey(year)) {
                            regionPercentConsumer.accept(commune.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                                  .multiply(BigDecimal.valueOf(100))
                                                                                                  .divide(totalRegionsData.get(departement.getRegion())
                                                                                                                          .get(year), 3, RoundingMode.HALF_UP)
                                                                                                  .doubleValue());
                        }
                    }
                }));
        }

        if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
            SortedMap<Integer, List<Double>> datas = new TreeMap<>();

            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .map(Departement::getPopulation)
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> datas.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            datas.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getDepartements().getJenks()))) {
                jenksConsumer.accept(data.getDepartements().getJenks(), datas);
            }

            Map<String, SortedMap<Integer, List<Double>>> regionsPops = new HashMap<>();
            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(departement.getPopulation())))
                .forEach(departement -> {
                    regionsPops.putIfAbsent(departement.getRegion(), new TreeMap<>());

                    mapper.apply(departement.getPopulation()).forEach((year, pop) -> {
                        regionsPops.get(departement.getRegion()).putIfAbsent(year, new ArrayList<>());
                        regionsPops.get(departement.getRegion()).get(year).add(pop.doubleValue());
                    });

                });

            regionsPops.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);

            data.getDepartements()
                .getDepartements()
                .values()
                .parallelStream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(departement.getPopulation())))
                .forEach(departement -> mapper.apply(departement.getPopulation()).forEach((year, pop) -> {
                    countryRankConsumer.accept(departement.getPopulation(), year,
                                               datas.get(year).size() - CollecUtils.rank(datas.get(year), pop.doubleValue()));

                    if (totalData.containsKey(year)) {
                        countryPercentConsumer.accept(departement.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                                   .multiply(BigDecimal.valueOf(100))
                                                                                                   .divide(totalData.get(year), 3, RoundingMode.HALF_UP)
                                                                                                   .doubleValue());
                    }

                    regionRankConsumer.accept(departement.getPopulation(), year, regionsPops.get(departement.getRegion()).get(year).size()
                                                                                 - CollecUtils.rank(regionsPops.get(departement.getRegion())
                                                                                                               .get(year), pop.doubleValue()));

                    if (totalRegionsData.containsKey(departement.getRegion()) && totalRegionsData.get(departement.getRegion()).containsKey(year)) {
                        regionPercentConsumer.accept(departement.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                                  .multiply(BigDecimal.valueOf(100))
                                                                                                  .divide(totalRegionsData.get(departement.getRegion())
                                                                                                                          .get(year), 3, RoundingMode.HALF_UP)
                                                                                                  .doubleValue());
                    }
                }));
        }

        if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
            SortedMap<Integer, List<Double>> datas = new TreeMap<>();

            data.getRegions()
                .getRegions()
                .values()
                .stream()
                .map(Region::getPopulation)
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> datas.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            datas.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getRegions().getJenks()))) {
                jenksConsumer.accept(data.getRegions().getJenks(), datas);
            }

            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(region.getPopulation())))
                .forEach(region -> mapper.apply(region.getPopulation()).forEach((year, pop) -> {
                    countryRankConsumer.accept(region.getPopulation(), year, datas.get(year).size() - CollecUtils.rank(datas.get(year), pop.doubleValue()));

                    if (totalData.containsKey(year)) {
                        countryPercentConsumer.accept(region.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                              .multiply(BigDecimal.valueOf(100))
                                                                                              .divide(totalData.get(year), 3, RoundingMode.HALF_UP)
                                                                                              .doubleValue());
                    }
                }));
        }
    }

    private <N extends Number & Comparable<N>> void doDensity(Data data, Function<DataPopulation, SortedMap<Integer, N>> mapper,
                                                              Function<DataJenks, SortedMap<Integer, SortedSet<Double>>> jenksMapper,
                                                              BiConsumer<DataJenks, Map<Integer, List<N>>> jenksConsumer,
                                                              TriConsumer<DataPopulation, Integer, Integer> countryRankConsumer,
                                                              TriConsumer<DepartementPopulation, Integer, Integer> regionRankConsumer,
                                                              TriConsumer<CommunePopulation, Integer, Integer> departementRankConsumer) {
        if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
            Map<Integer, List<N>> datas = new HashMap<>();
            data.getRegions()
                .getRegions()
                .values()
                .stream()
                .map(Region::getPopulation)
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .filter(e -> e.getValue() != null)
                .forEach(entry -> datas.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(entry.getValue());

                    return numbers;
                }));

            datas.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getRegions().getJenks()))) {
                jenksConsumer.accept(data.getRegions().getJenks(), datas);
            }

            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(region.getPopulation())))
                .forEach(region -> mapper.apply(region.getPopulation()).forEach((year, value) -> {
                    if (value == null) {
                        return;
                    }

                    countryRankConsumer.accept(region.getPopulation(), year, datas.get(year).size() - datas.get(year).indexOf(value));
                }));
        }

        if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
            Map<Integer, List<N>> datas = new HashMap<>();
            Map<String, Map<Integer, List<N>>> regionDatas = new HashMap<>();
            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .filter(d -> d.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(d.getPopulation())))
                .forEach(departement -> mapper.apply(departement.getPopulation()).forEach((year, value) -> {
                    if (value == null) {
                        return;
                    }

                    datas.compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    regionDatas.putIfAbsent(departement.getRegion(), new HashMap<>());
                    regionDatas.get(departement.getRegion()).compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });
                }));


            datas.values().forEach(Collections::sort);
            regionDatas.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getDepartements().getJenks()))) {
                jenksConsumer.accept(data.getDepartements().getJenks(), datas);
            }

            data.getDepartements()
                .getDepartements()
                .values()
                .parallelStream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(departement.getPopulation())))
                .forEach(departement -> mapper.apply(departement.getPopulation()).forEach((year, value) -> {
                    if (value == null) {
                        return;
                    }

                    countryRankConsumer.accept(departement.getPopulation(), year, datas.get(year).size() - CollecUtils.rank(datas.get(year), value));
                    regionRankConsumer.accept(departement.getPopulation(), year, regionDatas.get(departement.getRegion()).get(year).size()
                                                                                 - CollecUtils.rank(regionDatas.get(departement.getRegion()).get(year), value));
                }));
        }

        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            Map<Integer, List<N>> datas = new HashMap<>();
            Map<String, Map<Integer, List<N>>> departementDatas = new HashMap<>();
            Map<String, Map<Integer, List<N>>> regionDatas = new HashMap<>();

            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(c -> c.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(c.getPopulation())))
                .forEach(commune -> mapper.apply(commune.getPopulation()).forEach((year, value) -> {
                    if (value == null) {
                        return;
                    }

                    datas.compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    departementDatas.putIfAbsent(commune.getDepartement(), new HashMap<>());
                    departementDatas.get(commune.getDepartement()).compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        regionDatas.putIfAbsent(departement.getRegion(), new HashMap<>());
                        regionDatas.get(departement.getRegion()).compute(year, (integer, numbers) -> {
                            if (numbers == null) {
                                numbers = new ArrayList<>();
                            }

                            numbers.add(value);

                            return numbers;
                        });
                    }
                }));

            datas.values().forEach(Collections::sort);
            departementDatas.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);
            regionDatas.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(jenksMapper.apply(data.getCommunes().getJenks()))) {
                jenksConsumer.accept(data.getCommunes().getJenks(), datas);
            }

            data.getCommunes()
                .getCommunes()
                .values()
                .parallelStream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(mapper.apply(commune.getPopulation())))
                .forEach(commune -> mapper.apply(commune.getPopulation()).forEach((year, value) -> {
                    if (value == null) {
                        return;
                    }

                    countryRankConsumer.accept(commune.getPopulation(), year, datas.get(year).size() - CollecUtils.rank(datas.get(year), value));
                    departementRankConsumer.accept(commune.getPopulation(), year, departementDatas.get(commune.getDepartement()).get(year).size()
                                                                                  - CollecUtils.rank(departementDatas.get(commune.getDepartement())
                                                                                                                     .get(year), value));

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        regionRankConsumer.accept(commune.getPopulation(), year, regionDatas.get(departement.getRegion()).get(year).size()
                                                                                 - CollecUtils.rank(regionDatas.get(departement.getRegion()).get(year), value));
                    }
                }));
        }
    }
}
