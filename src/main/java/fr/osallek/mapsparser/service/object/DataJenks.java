package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.Jenks;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataJenks extends Hashed {

    @JsonIgnore
    private static final int NB_CLASSES = 17;

    @JsonIgnore
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected SortedMap<Integer, DataJenksNode> population;

    protected DataJenksNode area;

    protected SortedMap<Integer, DataJenksNode> density;

    protected SortedMap<Integer, DataJenksNode> birth;

    protected SortedMap<Integer, DataJenksNode> death;

    protected SortedMap<Integer, DataJenksNode> birthPerCapita;

    protected SortedMap<Integer, DataJenksNode> deathPerCapita;

    @JsonIgnore
    public CompletableFuture<Void> doArea(Data data) {
        return doNode(data, DataNode::getArea, DataNode::getArea, DataNode::getArea).thenAccept(dataJenksNode -> this.area = dataJenksNode);
    }

    @JsonIgnore
    public CompletableFuture<Void> doPopulation(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getPopulation)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data, d -> Optional.ofNullable(d.getPopulation().getPopulation())
                                                                                  .map(map -> map.get(year))
                                                                                  .orElse(null), d -> Optional.ofNullable(d.getPopulation().getPopulation())
                                                                                                              .map(map -> map.get(year))
                                                                                                              .orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getPopulation())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.population = new TreeMap<>(tmp));
    }

    @JsonIgnore
    public CompletableFuture<Void> doDensity(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getDensity)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data,
                                                               d -> Optional.ofNullable(d.getPopulation().getDensity()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getDensity()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getDensity())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.density = new TreeMap<>(tmp));
    }

    @JsonIgnore
    public CompletableFuture<Void> doBirth(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getBirth)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data,
                                                               d -> Optional.ofNullable(d.getPopulation().getBirth()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getBirth()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getBirth())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.birth = new TreeMap<>(tmp));
    }

    @JsonIgnore
    public CompletableFuture<Void> doDeath(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getDeath)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data,
                                                               d -> Optional.ofNullable(d.getPopulation().getDeath()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getDeath()).map(map -> map.get(year)).orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getDeath())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.death = new TreeMap<>(tmp));
    }

    @JsonIgnore
    public CompletableFuture<Void> doBirthPerCapita(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getBirthPerCapita)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data, d -> Optional.ofNullable(d.getPopulation().getBirthPerCapita())
                                                                                  .map(map -> map.get(year))
                                                                                  .orElse(null), d -> Optional.ofNullable(d.getPopulation().getBirthPerCapita())
                                                                                                              .map(map -> map.get(year))
                                                                                                              .orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getBirthPerCapita())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.birthPerCapita = new TreeMap<>(tmp));
    }

    @JsonIgnore
    public CompletableFuture<Void> doDeathPerCapita(Data data) {
        Map<Integer, DataJenksNode> tmp = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(data.getRegions()
                                           .getRegions()
                                           .values()
                                           .stream()
                                           .map(DataNode::getPopulation)
                                           .map(DataPopulation::getDeathPerCapita)
                                           .map(SortedMap::keySet)
                                           .flatMap(Collection::stream)
                                           .distinct()
                                           .map(year -> doNode(data, d -> Optional.ofNullable(d.getPopulation().getDeathPerCapita())
                                                                                  .map(map -> map.get(year))
                                                                                  .orElse(null), d -> Optional.ofNullable(d.getPopulation().getDeathPerCapita())
                                                                                                              .map(map -> map.get(year))
                                                                                                              .orElse(null),
                                                               d -> Optional.ofNullable(d.getPopulation().getDeathPerCapita())
                                                                            .map(map -> map.get(year))
                                                                            .orElse(null)).thenAccept(dataJenksNode -> tmp.put(year, dataJenksNode)))
                                           .toArray(CompletableFuture[]::new)).thenAccept(unused -> this.deathPerCapita = new TreeMap<>(tmp));
    }

    @JsonIgnore
    private CompletableFuture<DataJenksNode> doNode(Data data, Function<Region, Number> regionMapper, Function<Departement, Number> departementMapper,
                                                    Function<Commune, Number> communeMapper) {
        CompletableFuture<DataJenksNode> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            List<Number> numbers = data.getRegions().getRegions().values().stream().map(regionMapper).filter(Objects::nonNull).toList();
            int nbRegionsClasses = Math.min(NB_CLASSES, numbers.size());
            Jenks jenksRegions = new Jenks(numbers);
            Jenks.Breaks breaksRegions = jenksRegions.computeBreaks(nbRegionsClasses);

            numbers = data.getDepartements().getDepartements().values().stream().map(departementMapper).filter(Objects::nonNull).toList();
            int nbDepartementsClasses = Math.min(NB_CLASSES, numbers.size());
            Jenks jenksDepartements = new Jenks(numbers);
            Jenks.Breaks breaksDepartements = jenksDepartements.computeBreaks(nbDepartementsClasses);

            numbers = data.getCommunes().getCommunes().values().stream().map(communeMapper).filter(Objects::nonNull).toList();
            int nbCommunesClasses = Math.min(NB_CLASSES, numbers.size());
            Jenks jenksCommunes = new Jenks(numbers);
            Jenks.Breaks breaksCommunes = jenksCommunes.computeBreaks(nbCommunesClasses);

            future.complete(new DataJenksNode(data.getRegions()
                                                  .getRegions()
                                                  .values()
                                                  .stream()
                                                  .filter(r -> regionMapper.apply(r) != null)
                                                  .collect(Collectors.toMap(Region::getId, r -> breaksRegions.classOf(regionMapper.apply(r)), (a, b) -> a,
                                                                            TreeMap::new)), data.getDepartements()
                                                                                                .getDepartements()
                                                                                                .values()
                                                                                                .stream()
                                                                                                .filter(r -> departementMapper.apply(r) != null)
                                                                                                .collect(Collectors.toMap(Departement::getId,
                                                                                                                          r -> breaksDepartements.classOf(
                                                                                                                                  departementMapper.apply(r)),
                                                                                                                          (a, b) -> a, TreeMap::new)),
                                              data.getCommunes()
                                                  .getCommunes()
                                                  .values()
                                                  .stream()
                                                  .filter(r -> communeMapper.apply(r) != null)
                                                  .collect(Collectors.toMap(Commune::getId, r -> breaksCommunes.classOf(communeMapper.apply(r)), (a, b) -> a,
                                                                            TreeMap::new))));
        }, Constants.EXECUTOR);

        return future;
    }

    public SortedMap<Integer, DataJenksNode> getPopulation() {
        return population;
    }

    public void setPopulation(SortedMap<Integer, DataJenksNode> population) {
        this.population = population;
    }

    public DataJenksNode getArea() {
        return area;
    }

    public void setArea(DataJenksNode area) {
        this.area = area;
    }

    public SortedMap<Integer, DataJenksNode> getDensity() {
        return density;
    }

    public void setDensity(SortedMap<Integer, DataJenksNode> density) {
        this.density = density;
    }

    public SortedMap<Integer, DataJenksNode> getBirth() {
        return birth;
    }

    public void setBirth(SortedMap<Integer, DataJenksNode> birth) {
        this.birth = birth;
    }

    public SortedMap<Integer, DataJenksNode> getDeath() {
        return death;
    }

    public void setDeath(SortedMap<Integer, DataJenksNode> death) {
        this.death = death;
    }

    public SortedMap<Integer, DataJenksNode> getBirthPerCapita() {
        return birthPerCapita;
    }

    public void setBirthPerCapita(SortedMap<Integer, DataJenksNode> birthPerCapita) {
        this.birthPerCapita = birthPerCapita;
    }

    public SortedMap<Integer, DataJenksNode> getDeathPerCapita() {
        return deathPerCapita;
    }

    public void setDeathPerCapita(SortedMap<Integer, DataJenksNode> deathPerCapita) {
        this.deathPerCapita = deathPerCapita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DataJenks that)) {
            return false;
        }

        return new EqualsBuilder().append(population, that.population)
                                  .append(area, that.area)
                                  .append(density, that.density)
                                  .append(birth, that.birth)
                                  .append(death, that.death)
                                  .append(birthPerCapita, that.birthPerCapita)
                                  .append(deathPerCapita, that.deathPerCapita)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(population)
                                          .append(area)
                                          .append(density)
                                          .append(birth)
                                          .append(death)
                                          .append(birthPerCapita)
                                          .append(deathPerCapita)
                                          .toHashCode();
    }
}
