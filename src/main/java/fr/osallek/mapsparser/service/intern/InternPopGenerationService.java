package fr.osallek.mapsparser.service.intern;

import fr.osallek.mapsparser.common.CollecUtils;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.CommunePopulation;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataPopulation;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.DepartementPopulation;
import fr.osallek.mapsparser.service.object.Region;
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
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
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
        if (data.getCommunes().getCommunes().values().stream().anyMatch(c -> c.getInitHash() != c.hashCode())) {
            doRanksPop(data, DataPopulation::getPopulation, DataPopulation::setCountryRanks, DataPopulation::setPercentCountry,
                       DepartementPopulation::setRegionRanks, DepartementPopulation::setPercentRegion, CommunePopulation::setDepartementRanks,
                       CommunePopulation::setPercentDepartement);
            doRanksPop(data, DataPopulation::getBirth, DataPopulation::setBirthCountryRanks, DataPopulation::setBirthPercentCountry,
                       DepartementPopulation::setBirthRegionRanks, DepartementPopulation::setBirthPercentRegion, CommunePopulation::setBirthDepartementRanks,
                       CommunePopulation::setBirthPercentDepartement);
            doRanksPop(data, DataPopulation::getDeath, DataPopulation::setDeathCountryRanks, DataPopulation::setDeathPercentCountry,
                       DepartementPopulation::setDeathRegionRanks, DepartementPopulation::setDeathPercentRegion, CommunePopulation::setDeathDepartementRanks,
                       CommunePopulation::setDeathPercentDepartement);
            doDensity(data, DataPopulation::getBirthPerCapita, DataPopulation::setBirthPerCapitaCountryRanks,
                      DepartementPopulation::setBirthPerCapitaRegionRanks, CommunePopulation::setBirthPerCapitaDepartementRanks);
            doDensity(data, DataPopulation::getDeathPerCapita, DataPopulation::setDeathPerCapitaCountryRanks,
                      DepartementPopulation::setDeathPerCapitaRegionRanks, CommunePopulation::setDeathPerCapitaDepartementRanks);
            doDensity(data, DataPopulation::getDensity, DataPopulation::setDensityCountryRanks, DepartementPopulation::setDensityRegionRanks,
                      CommunePopulation::setDensityDepartementRanks);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getPopulation())) {
            futures.add(data.getJenks().doPopulation(data).thenAccept(unused -> logger.info("Done Population Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || data.getJenks().getArea() == null) {
            futures.add(data.getJenks().doArea(data).thenAccept(unused -> logger.info("Done Area Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getDensity())) {
            futures.add(data.getJenks().doDensity(data).thenAccept(unused -> logger.info("Done Density Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getBirth())) {
            futures.add(data.getJenks().doBirth(data).thenAccept(unused -> logger.info("Done Birth Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getDeath())) {
            futures.add(data.getJenks().doDeath(data).thenAccept(unused -> logger.info("Done Death Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getBirthPerCapita())) {
            futures.add(data.getJenks().doBirthPerCapita(data).thenAccept(unused -> logger.info("Done Birth Per Capita Jenks")));
        }

        if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getJenks().getDeathPerCapita())) {
            futures.add(data.getJenks().doDeathPerCapita(data).thenAccept(unused -> logger.info("Done Death Per Capita Jenks")));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private void doRanksPop(Data data, Function<DataPopulation, SortedMap<Integer, Integer>> mapper,
                            TriConsumer<DataPopulation, Integer, Integer> countryRankConsumer,
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
                                                                                      .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(
                                                                                              e.getValue().stream().mapToInt(Double::intValue).sum())))));
            regionsDatas.forEach((dep, map) -> totalRegionsData.put(dep, map.entrySet()
                                                                            .stream()
                                                                            .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(
                                                                                    e.getValue().stream().mapToInt(Double::intValue).sum())))));

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
                        departementRankConsumer.accept(commune.getPopulation(), year,
                                                       departementsDatas.get(commune.getDepartement()).get(year).size() - CollecUtils.rank(
                                                               departementsDatas.get(commune.getDepartement()).get(year), pop.doubleValue()));

                        if (totalDepartementsData.containsKey(departement.getId()) && totalDepartementsData.get(departement.getId()).containsKey(year)) {
                            departementPercentConsumer.accept(commune.getPopulation(), year, BigDecimal.valueOf(pop)
                                                                                                       .multiply(BigDecimal.valueOf(100))
                                                                                                       .divide(totalDepartementsData.get(departement.getId())
                                                                                                                                    .get(year), 3,
                                                                                                               RoundingMode.HALF_UP)
                                                                                                       .doubleValue());
                        }

                        regionRankConsumer.accept(commune.getPopulation(), year, regionsDatas.get(departement.getRegion()).get(year).size() - CollecUtils.rank(
                                regionsDatas.get(departement.getRegion()).get(year), pop.doubleValue()));

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

                    regionRankConsumer.accept(departement.getPopulation(), year, regionsPops.get(departement.getRegion()).get(year).size() - CollecUtils.rank(
                            regionsPops.get(departement.getRegion()).get(year), pop.doubleValue()));

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
                    regionRankConsumer.accept(departement.getPopulation(), year, regionDatas.get(departement.getRegion()).get(year).size() - CollecUtils.rank(
                            regionDatas.get(departement.getRegion()).get(year), value));
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
                    departementRankConsumer.accept(commune.getPopulation(), year,
                                                   departementDatas.get(commune.getDepartement()).get(year).size() - CollecUtils.rank(
                                                           departementDatas.get(commune.getDepartement()).get(year), value));

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        regionRankConsumer.accept(commune.getPopulation(), year, regionDatas.get(departement.getRegion()).get(year).size() - CollecUtils.rank(
                                regionDatas.get(departement.getRegion()).get(year), value));
                    }
                }));
        }
    }
}
