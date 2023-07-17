package fr.osallek.mapsparser.service.intern;

import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataNode;
import fr.osallek.mapsparser.service.object.DataPopulation;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
import java.math.BigDecimal;
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

            data.getCommunes()
                .getCommunes()
                .values()
                .parallelStream()
                .filter(commune -> commune.getArea() != null)
                .forEach(commune -> {
                    commune.setAreaCountryRanks(areas.size() - areas.indexOf(commune.getArea()));

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        commune.setAreaDepartementRanks(
                                departementsArea.get(departement.getId()).size() - departementsArea.get(departement.getId()).indexOf(commune.getArea()));
                        commune.setAreaRegionRanks(
                                regionsArea.get(departement.getRegion()).size() - regionsArea.get(departement.getRegion()).indexOf(commune.getArea()));
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

            data.getCommunes()
                .getCommunes()
                .values()
                .parallelStream()
                .filter(commune -> commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation()))
                .forEach(commune -> commune.getPopulation().getPopulation().forEach((year, pop) -> {
                    commune.getPopulation().setCountryRanks(year, pops.get(year).size() - pops.get(year).indexOf(pop.doubleValue()));

                    Departement departement = data.getDepartements().getDepartements().get(commune.getDepartement());

                    if (departement != null) {
                        commune.getPopulation()
                               .setDepartementRanks(year, departementsPops.get(commune.getDepartement()).get(year).size()
                                                          - departementsPops.get(commune.getDepartement()).get(year).indexOf(pop.doubleValue()));
                        commune.getPopulation()
                               .setRegionRanks(year, regionsPops.get(departement.getRegion()).get(year).size() - regionsPops.get(departement.getRegion())
                                                                                                                            .get(year)
                                                                                                                            .indexOf(pop.doubleValue()));
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

            data.getDepartements()
                .getDepartements()
                .values()
                .parallelStream()
                .filter(departement -> departement.getArea() != null)
                .forEach(departement -> {
                    departement.setAreaCountryRanks(areas.size() - areas.indexOf(departement.getArea()));
                    departement.setAreaRegionRanks(
                            regionsArea.get(departement.getRegion()).size() - regionsArea.get(departement.getRegion()).indexOf(departement.getArea()));
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
                    departement.getPopulation().setCountryRanks(year, pops.get(year).size() - pops.get(year).indexOf(pop.doubleValue()));
                    departement.getPopulation()
                               .setRegionRanks(year, regionsPops.get(departement.getRegion()).get(year).size() - regionsPops.get(departement.getRegion())
                                                                                                                            .get(year)
                                                                                                                            .indexOf(pop.doubleValue()));
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
                .forEach(region -> region.setAreaCountryRanks(areas.size() - areas.indexOf(region.getArea())));

            data.getRegions()
                .getRegions()
                .values()
                .parallelStream()
                .filter(region -> region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getPopulation()))
                .forEach(region -> region.getPopulation()
                                         .getPopulation()
                                         .forEach((year, pop) -> region.getPopulation()
                                                                       .setCountryRanks(year,
                                                                                        pops.get(year).size() - pops.get(year).indexOf(pop.doubleValue()))));
        }
    }
}
