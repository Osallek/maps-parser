package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPopulation {

    protected SortedMap<Integer, Integer> population;

    protected SortedMap<Integer, Integer> countryRanks;

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

    @JsonIgnore
    public void setCountryRanks(Integer year, Integer rank) {
        if (this.countryRanks == null) {
            this.countryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.countryRanks.put(year, rank);
        }
    }
}
