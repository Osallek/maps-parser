package fr.osallek.mapsparser.service.intern;

import fr.osallek.mapsparser.common.CollecUtils;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataNode;
import fr.osallek.mapsparser.service.object.DataPopulation;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
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
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class InternGenerationService extends GenerationService {

    public InternGenerationService(Environment environment) {
        super("Osallek", "https://maps.osallek.net", environment);
    }

    @Override
    public void addData(Data data) {
        Map<Integer, BigDecimal> totalPops = new HashMap<>();
        Map<String, Map<Integer, BigDecimal>> totalDepartementsPops = new HashMap<>();
        Map<String, Map<Integer, BigDecimal>> totalRegionsPops = new HashMap<>();

        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            SortedMap<Integer, List<Double>> pops = new TreeMap<>();

            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .map(Commune::getPopulation)
                .filter(Objects::nonNull)
                .map(DataPopulation::getPopulation)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> pops.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            pops.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getCommunes().getJenks().getPopulation())) {
                data.getCommunes().getJenks().doPopulation(pops);
            }

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
                    commune.setAreaDepartementRanks(departementsArea.get(departement.getId()).size()
                                                    - CollecUtils.rank(departementsArea.get(departement.getId()), commune.getArea()));
                    commune.setAreaRegionRanks(regionsArea.get(departement.getRegion()).size()
                                               - CollecUtils.rank(regionsArea.get(departement.getRegion()), commune.getArea()));
                }
            });

            Map<String, SortedMap<Integer, List<Double>>> departementsPops = new HashMap<>();
            Map<String, SortedMap<Integer, List<Double>>> regionsPops = new HashMap<>();
            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation()))
                .forEach(commune -> {
                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        departementsPops.putIfAbsent(commune.getDepartement(), new TreeMap<>());
                        regionsPops.putIfAbsent(departement.getRegion(), new TreeMap<>());

                        commune.getPopulation().getPopulation().forEach((year, pop) -> {
                            departementsPops.get(commune.getDepartement()).putIfAbsent(year, new ArrayList<>());
                            regionsPops.get(departement.getRegion()).putIfAbsent(year, new ArrayList<>());

                            departementsPops.get(commune.getDepartement()).get(year).add(pop.doubleValue());
                            regionsPops.get(departement.getRegion()).get(year).add(pop.doubleValue());
                        });
                    }
                });

            departementsPops.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);
            regionsPops.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);

            pops.forEach((year, p) -> totalPops.put(year, BigDecimal.valueOf(p.stream().mapToInt(Double::intValue).sum())));
            departementsPops.forEach((dep, map) -> totalDepartementsPops.put(dep, map.entrySet()
                                                                                     .stream()
                                                                                     .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                               e -> BigDecimal.valueOf(e.getValue()
                                                                                                                                        .stream()
                                                                                                                                        .mapToInt(Double::intValue)
                                                                                                                                        .sum())))));
            regionsPops.forEach((dep, map) -> totalRegionsPops.put(dep, map.entrySet()
                                                                           .stream()
                                                                           .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                     e -> BigDecimal.valueOf(e.getValue()
                                                                                                                              .stream()
                                                                                                                              .mapToInt(Double::intValue)
                                                                                                                              .sum())))));

            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation()))
                .forEach(commune -> commune.getPopulation().getPopulation().forEach((year, pop) -> {
                    commune.getPopulation().setCountryRanks(year, pops.get(year).size() - CollecUtils.rank(pops.get(year), pop.doubleValue()));

                    if (totalPops.containsKey(year)) {
                        commune.getPopulation()
                               .setPercentCountry(year, BigDecimal.valueOf(pop)
                                                                  .multiply(BigDecimal.valueOf(100))
                                                                  .divide(totalPops.get(year), 3, RoundingMode.HALF_UP).doubleValue());
                    }

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        commune.getPopulation()
                               .setDepartementRanks(year, departementsPops.get(commune.getDepartement()).get(year).size()
                                                          - CollecUtils.rank(departementsPops.get(commune.getDepartement())
                                                                                             .get(year), pop.doubleValue()));

                        if (totalDepartementsPops.containsKey(departement.getId()) && totalDepartementsPops.get(departement.getId()).containsKey(year)) {
                            commune.getPopulation()
                                   .setPercentDepartement(year, BigDecimal.valueOf(pop)
                                                                          .multiply(BigDecimal.valueOf(100))
                                                                          .divide(totalDepartementsPops.get(departement.getId())
                                                                                                       .get(year), 3, RoundingMode.HALF_UP).doubleValue());
                        }

                        commune.getPopulation()
                               .setRegionRanks(year, regionsPops.get(departement.getRegion()).get(year).size()
                                                     - CollecUtils.rank(regionsPops.get(departement.getRegion()).get(year), pop.doubleValue()));

                        if (totalRegionsPops.containsKey(departement.getRegion()) && totalRegionsPops.get(departement.getRegion()).containsKey(year)) {
                            commune.getPopulation()
                                   .setPercentRegion(year, BigDecimal.valueOf(pop)
                                                                     .multiply(BigDecimal.valueOf(100))
                                                                     .divide(totalRegionsPops.get(departement.getRegion())
                                                                                             .get(year), 3, RoundingMode.HALF_UP).doubleValue());
                        }
                    }
                }));

            Map<Integer, List<BigDecimal>> densities = new HashMap<>();
            Map<String, Map<Integer, List<BigDecimal>>> departementDensities = new HashMap<>();
            Map<String, Map<Integer, List<BigDecimal>>> regionDensities = new HashMap<>();
            data.getCommunes()
                .getCommunes()
                .values()
                .stream()
                .filter(c -> c.getPopulation() != null && MapUtils.isNotEmpty(c.getPopulation().getDensity()))
                .forEach(commune -> commune.getPopulation().getDensity().forEach((year, value) -> {
                    densities.compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    departementDensities.putIfAbsent(commune.getDepartement(), new HashMap<>());
                    departementDensities.get(commune.getDepartement()).compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        regionDensities.putIfAbsent(departement.getRegion(), new HashMap<>());
                        regionDensities.get(departement.getRegion()).compute(year, (integer, numbers) -> {
                            if (numbers == null) {
                                numbers = new ArrayList<>();
                            }

                            numbers.add(value);

                            return numbers;
                        });
                    }
                }));

            densities.values().forEach(Collections::sort);
            departementDensities.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);
            regionDensities.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getCommunes().getJenks().getDensity())) {
                data.getCommunes().getJenks().doDensity(densities);
            }

            data.getCommunes()
                .getCommunes()
                .values()
                .parallelStream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getDensity()))
                .forEach(commune -> commune.getPopulation()
                                           .getDensity()
                                           .forEach((year, density) -> {
                                               commune.getPopulation()
                                                      .setDensityCountryRanks(year, densities.get(year).size()
                                                                                    - CollecUtils.rank(densities.get(year), density));
                                               commune.getPopulation()
                                                      .setDensityDepartementRanks(year, departementDensities.get(commune.getDepartement()).get(year).size()
                                                                                        - CollecUtils.rank(departementDensities.get(commune.getDepartement())
                                                                                                                               .get(year), density));

                                               Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                                               if (departement != null) {
                                                   commune.getPopulation()
                                                          .setDensityRegionRanks(year, regionDensities.get(departement.getRegion()).get(year).size()
                                                                                       - CollecUtils.rank(regionDensities.get(departement.getRegion())
                                                                                                                         .get(year), density));
                                               }
                                           }));
        }

        if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
            SortedMap<Integer, List<Double>> pops = new TreeMap<>();

            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .map(Departement::getPopulation)
                .filter(Objects::nonNull)
                .map(DataPopulation::getPopulation)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> pops.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            pops.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getDepartements().getJenks().getPopulation())) {
                data.getDepartements().getJenks().doPopulation(pops);
            }

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
                departement.setAreaRegionRanks(regionsArea.get(departement.getRegion()).size()
                                               - CollecUtils.rank(regionsArea.get(departement.getRegion()), departement.getArea()));
            });

            Map<String, SortedMap<Integer, List<Double>>> regionsPops = new HashMap<>();
            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getPopulation()))
                .forEach(departement -> {
                    regionsPops.putIfAbsent(departement.getRegion(), new TreeMap<>());

                    departement.getPopulation().getPopulation().forEach((year, pop) -> {
                        regionsPops.get(departement.getRegion()).putIfAbsent(year, new ArrayList<>());
                        regionsPops.get(departement.getRegion()).get(year).add(pop.doubleValue());
                    });

                });

            regionsPops.values().stream().map(SortedMap::values).flatMap(Collection::stream).forEach(Collections::sort);

            data.getDepartements()
                .getDepartements()
                .values()
                .parallelStream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getPopulation()))
                .forEach(departement -> departement.getPopulation().getPopulation().forEach((year, pop) -> {
                    departement.getPopulation().setCountryRanks(year, pops.get(year).size() - CollecUtils.rank(pops.get(year), pop.doubleValue()));

                    if (totalPops.containsKey(year)) {
                        departement.getPopulation()
                                   .setPercentCountry(year, BigDecimal.valueOf(pop)
                                                                      .multiply(BigDecimal.valueOf(100))
                                                                      .divide(totalPops.get(year), 3, RoundingMode.HALF_UP).doubleValue());
                    }

                    departement.getPopulation()
                               .setRegionRanks(year, regionsPops.get(departement.getRegion()).get(year).size()
                                                     - CollecUtils.rank(regionsPops.get(departement.getRegion()).get(year), pop.doubleValue()));

                    if (totalRegionsPops.containsKey(departement.getRegion()) && totalRegionsPops.get(departement.getRegion()).containsKey(year)) {
                        departement.getPopulation()
                                   .setPercentRegion(year, BigDecimal.valueOf(pop)
                                                                     .multiply(BigDecimal.valueOf(100))
                                                                     .divide(totalRegionsPops.get(departement.getRegion())
                                                                                             .get(year), 3, RoundingMode.HALF_UP).doubleValue());
                    }
                }));

            Map<Integer, List<BigDecimal>> densities = new HashMap<>();
            Map<String, Map<Integer, List<BigDecimal>>> regionDensities = new HashMap<>();
            data.getDepartements()
                .getDepartements()
                .values()
                .stream()
                .filter(d -> d.getPopulation() != null && MapUtils.isNotEmpty(d.getPopulation().getDensity()))
                .forEach(departement -> departement.getPopulation().getDensity().forEach((year, value) -> {
                    densities.compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });

                    regionDensities.putIfAbsent(departement.getRegion(), new HashMap<>());
                    regionDensities.get(departement.getRegion()).compute(year, (integer, numbers) -> {
                        if (numbers == null) {
                            numbers = new ArrayList<>();
                        }

                        numbers.add(value);

                        return numbers;
                    });
                }));

            densities.values().forEach(Collections::sort);
            regionDensities.values().stream().map(Map::values).flatMap(Collection::stream).forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getDepartements().getJenks().getDensity())) {
                data.getDepartements().getJenks().doDensity(densities);
            }

            data.getDepartements()
                .getDepartements()
                .values()
                .parallelStream()
                .filter(departement -> departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getDensity()))
                .forEach(departement -> departement.getPopulation()
                                                   .getDensity()
                                                   .forEach((year, density) -> {
                                                       departement.getPopulation()
                                                                  .setDensityCountryRanks(year, densities.get(year).size()
                                                                                                - CollecUtils.rank(densities.get(year), density));
                                                       departement.getPopulation()
                                                                  .setDensityRegionRanks(year, regionDensities.get(departement.getRegion()).get(year).size()
                                                                                               - CollecUtils.rank(regionDensities.get(departement.getRegion())
                                                                                                                                 .get(year), density));
                                                   }));
        }

        if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
            SortedMap<Integer, List<Double>> pops = new TreeMap<>();

            data.getRegions()
                .getRegions()
                .values()
                .stream()
                .map(Region::getPopulation)
                .filter(Objects::nonNull)
                .map(DataPopulation::getPopulation)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> pops.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(Double.valueOf(entry.getValue()));

                    return numbers;
                }));

            pops.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getRegions().getJenks().getPopulation())) {
                data.getRegions().getJenks().doPopulation(pops);
            }

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

            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getPopulation()))
                .forEach(region -> region.getPopulation().getPopulation().forEach((year, pop) -> {
                    region.getPopulation().setCountryRanks(year, pops.get(year).size() - CollecUtils.rank(pops.get(year), pop.doubleValue()));

                    if (totalPops.containsKey(year)) {
                        region.getPopulation()
                              .setPercentCountry(year, BigDecimal.valueOf(pop)
                                                                 .multiply(BigDecimal.valueOf(100))
                                                                 .divide(totalPops.get(year), 3, RoundingMode.HALF_UP)
                                                                 .doubleValue());
                    }
                }));

            Map<Integer, List<BigDecimal>> densities = new HashMap<>();
            data.getRegions()
                .getRegions()
                .values()
                .stream()
                .map(Region::getPopulation)
                .filter(Objects::nonNull)
                .map(DataPopulation::getDensity)
                .filter(MapUtils::isNotEmpty)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(entry -> densities.compute(entry.getKey(), (integer, numbers) -> {
                    if (numbers == null) {
                        numbers = new ArrayList<>();
                    }

                    numbers.add(entry.getValue());

                    return numbers;
                }));

            densities.values().forEach(Collections::sort);

            if (!this.environment.matchesProfiles("dev") || MapUtils.isEmpty(data.getRegions().getJenks().getDensity())) {
                data.getRegions().getJenks().doDensity(densities);
            }

            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getDensity()))
                .forEach(region -> region.getPopulation()
                                         .getDensity()
                                         .forEach((year, density) -> region.getPopulation()
                                                                           .setDensityCountryRanks(year, densities.get(year).size() - densities.get(year)
                                                                                                                                               .indexOf(density))));
        }
    }
}
