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

    @JsonIgnore
    public void doPopulation(SortedMap<Integer, List<Double>> populations) {
        CountDownLatch countDownLatch = new CountDownLatch(populations.size());
        int nbClasses = Math.min(NB_CLASSES, populations.values().iterator().next().size());
        Map<Integer, SortedSet<Double>> map = new ConcurrentHashMap<>();

        populations.forEach((key, value) -> {
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
            this.logger.error("An error occurred while waiting for populations jenks: {}", e.getMessage(), e);
        }

        this.population = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o, TreeMap::new));
    }

    @JsonIgnore
    public void doArea(List<Double> areas) {
        int nbClasses = Math.min(NB_CLASSES, areas.size());
        Jenks jenks = new Jenks(areas);
        Jenks.Breaks breaks = jenks.computeBreaks(nbClasses);

        this.area = IntStream.range(0, nbClasses - 1).mapToObj(breaks::getClassMax).collect(Collectors.toCollection(TreeSet::new));
    }

    @JsonIgnore
    public void doDensity(Map<Integer, List<BigDecimal>> densities) {
        CountDownLatch countDownLatch = new CountDownLatch(densities.size());
        int nbClasses = Math.min(NB_CLASSES, densities.values().iterator().next().size());
        Map<Integer, SortedSet<Double>> map = new ConcurrentHashMap<>();

        densities.forEach((key, value) -> {
            Jenks jenks = new Jenks(value.stream().map(BigDecimal::doubleValue).toList());

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
            this.logger.error("An error occurred while waiting for densities jenks: {}", e.getMessage(), e);
        }

        this.density = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o, TreeMap::new));
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
}
