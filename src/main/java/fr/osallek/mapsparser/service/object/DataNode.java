package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataNode<P extends DataPopulation> extends IdName<String, String> {

    protected final Level level;

    protected BigDecimal area;

    protected Integer areaCountryRanks;

    protected P population;

    public DataNode(String id, String name, Level level, P population) {
        super(id, name);
        this.level = level;
        this.population = population;
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

    public Integer getAreaCountryRanks() {
        return areaCountryRanks;
    }

    public void setAreaCountryRanks(Integer areaCountryRanks) {
        this.areaCountryRanks = areaCountryRanks;
    }

    public P getPopulation() {
        return population;
    }

    public void setPopulation(P population) {
        this.population = population;
    }

    @JsonIgnore
    public void addPopulation(Integer year, Integer population) {
        if (population != null) {
            this.population.addPopulation(year, population, this.area);
        }
    }

    @Override
    public void initHash() {
        if (this.population != null) {
            this.population.initHash();
        }

        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DataNode<?> dataNode)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o))
                                  .append(level, dataNode.level)
                                  .append(area, dataNode.area)
                                  .append(areaCountryRanks, dataNode.areaCountryRanks)
                                  .append(population, dataNode.population)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(level).append(area).append(areaCountryRanks).append(population).toHashCode();
    }
}
