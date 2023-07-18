package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPopulation {

    protected SortedMap<Integer, Integer> population;

    protected SortedMap<Integer, BigDecimal> density;

    protected SortedMap<Integer, Integer> countryRanks;

    protected SortedMap<Integer, Integer> densityCountryRanks;

    //% of pop for country/region/departement, stacked and pie

    public SortedMap<Integer, Integer> getPopulation() {
        return population;
    }

    public void setPopulation(SortedMap<Integer, Integer> population) {
        this.population = population;
    }

    public SortedMap<Integer, Integer> getCountryRanks() {
        return countryRanks;
    }

    public void setCountryRanks(SortedMap<Integer, Integer> countryRanks) {
        this.countryRanks = countryRanks;
    }

    public SortedMap<Integer, BigDecimal> getDensity() {
        return density;
    }

    public void setDensity(SortedMap<Integer, BigDecimal> density) {
        this.density = density;
    }

    public SortedMap<Integer, Integer> getDensityCountryRanks() {
        return densityCountryRanks;
    }

    public void setDensityCountryRanks(SortedMap<Integer, Integer> densityCountryRanks) {
        this.densityCountryRanks = densityCountryRanks;
    }

    @JsonIgnore
    public void addPopulation(Integer year, Integer population, BigDecimal area) {
        if (this.population == null) {
            this.population = new TreeMap<>();
        }

        if (this.density == null) {
            this.density = new TreeMap<>();
        }

        if (population != null) {
            this.population.compute(year, (y, old) -> ObjectUtils.defaultIfNull(old, 0) + ObjectUtils.defaultIfNull(population, 0));

            if (area != null) {
                this.density.put(year, BigDecimal.valueOf(this.population.get(year)).divide(area, 1, RoundingMode.FLOOR));
            }
        }
    }

    @JsonIgnore
    public void setCountryRanks(Integer year, Integer rank) {
        if (this.countryRanks == null) {
            this.countryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.countryRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDensityCountryRanks(Integer year, Integer rank) {
        if (this.densityCountryRanks == null) {
            this.densityCountryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.densityCountryRanks.put(year, rank);
        }
    }
}
