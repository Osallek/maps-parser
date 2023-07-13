package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataNode extends IdName<String, String> {

    protected final Level level;

    protected BigDecimal area;

    protected SortedMap<Integer, Integer> population;

    public DataNode(String id, String name, Level level) {
        super(id, name);
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public SortedMap<Integer, Integer> getPopulation() {
        return population;
    }

    public void setPopulation(SortedMap<Integer, Integer> population) {
        this.population = population;
    }

    @JsonIgnore
    public void setPopulation(Integer year, Integer population) {
        if (this.population == null) {
            this.population = new TreeMap<>();
        }

        if (population != null) {
            this.population.put(year, population);
        }
    }

    @JsonIgnore
    public void addPopulation(Integer year, Integer population) {
        if (this.population == null) {
            this.population = new TreeMap<>();
        }

        if (population != null) {
            this.population.compute(year, (y, old) -> ObjectUtils.defaultIfNull(old, 0) + ObjectUtils.defaultIfNull(population, 0));
        }
    }
}
