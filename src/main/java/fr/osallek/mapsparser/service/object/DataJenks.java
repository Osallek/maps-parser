package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.Jenks;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataJenks {

    private static final int NB_CLASSES = 17;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected SortedMap<Integer, SortedSet<Double>> population;

    protected SortedSet<Double> area;

    protected SortedMap<Integer, SortedSet<Double>> density;

    protected SortedMap<Integer, SortedSet<Double>> birth;

    protected SortedMap<Integer, SortedSet<Double>> death;

    protected SortedMap<Integer, SortedSet<Double>> birthPerCapita;

    protected SortedMap<Integer, SortedSet<Double>> deathPerCapita;

    @JsonIgnore
    public void doPopulation(Map<Integer, List<Double>> data) {
        doYears(data, map -> this.population = map);
    }

    @JsonIgnore
    public void doArea(List<Double> areas) {
        int nbClasses = Math.min(NB_CLASSES, areas.size());
        Jenks jenks = new Jenks(areas);
        Jenks.Breaks breaks = jenks.computeBreaks(nbClasses);

        this.area = IntStream.range(0, nbClasses - 1).mapToObj(breaks::getClassMax).collect(Collectors.toCollection(TreeSet::new));
    }

    @JsonIgnore
    public void doDensity(Map<Integer, List<BigDecimal>> data) {
        doYears(data, map -> this.density = map);
    }

    @JsonIgnore
    public void doBirth(Map<Integer, List<Double>> data) {
        doYears(data, map -> this.birth = map);
    }

    @JsonIgnore
    public void doDeath(Map<Integer, List<Double>> data) {
        doYears(data, map -> this.death = map);
    }

    @JsonIgnore
    public void doBirthPerCapita(Map<Integer, List<BigDecimal>> data) {
        doYears(data, map -> this.birthPerCapita = map);
    }

    @JsonIgnore
    public void doDeathPerCapita(Map<Integer, List<BigDecimal>> data) {
        doYears(data, map -> this.deathPerCapita = map);
    }

    @JsonIgnore
    private <N extends Number> void doYears(Map<Integer, List<N>> data, Consumer<SortedMap<Integer, SortedSet<Double>>> consumer) {
        CountDownLatch countDownLatch = new CountDownLatch(data.size());
        int nbClasses = Math.min(NB_CLASSES, data.values().iterator().next().size());
        Map<Integer, SortedSet<Double>> map = new ConcurrentHashMap<>();

        data.forEach((key, value) -> {
            Jenks jenks = new Jenks(value);

            Constants.EXECUTOR.submit(() -> {
                try {
                    Jenks.Breaks breaks = jenks.computeBreaks(nbClasses);

                    map.put(key, IntStream.range(0, nbClasses - 1).mapToObj(breaks::getClassMax).collect(Collectors.toCollection(TreeSet::new)));
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            this.logger.error("An error occurred while waiting for jenks: {}", e.getMessage(), e);
        }

        consumer.accept(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o, TreeMap::new)));
    }

    public SortedMap<Integer, SortedSet<Double>> getPopulation() {
        return population;
    }

    public void setPopulation(SortedMap<Integer, SortedSet<Double>> population) {
        this.population = population;
    }

    public SortedSet<Double> getArea() {
        return area;
    }

    public void setArea(SortedSet<Double> area) {
        this.area = area;
    }

    public SortedMap<Integer, SortedSet<Double>> getDensity() {
        return density;
    }

    public void setDensity(SortedMap<Integer, SortedSet<Double>> density) {
        this.density = density;
    }

    public SortedMap<Integer, SortedSet<Double>> getBirth() {
        return birth;
    }

    public void setBirth(SortedMap<Integer, SortedSet<Double>> birth) {
        this.birth = birth;
    }

    public SortedMap<Integer, SortedSet<Double>> getDeath() {
        return death;
    }

    public void setDeath(SortedMap<Integer, SortedSet<Double>> death) {
        this.death = death;
    }

    public SortedMap<Integer, SortedSet<Double>> getBirthPerCapita() {
        return birthPerCapita;
    }

    public void setBirthPerCapita(SortedMap<Integer, SortedSet<Double>> birthPerCapita) {
        this.birthPerCapita = birthPerCapita;
    }

    public SortedMap<Integer, SortedSet<Double>> getDeathPerCapita() {
        return deathPerCapita;
    }

    public void setDeathPerCapita(SortedMap<Integer, SortedSet<Double>> deathPerCapita) {
        this.deathPerCapita = deathPerCapita;
    }
}
