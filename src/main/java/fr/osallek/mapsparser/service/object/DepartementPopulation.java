package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartementPopulation extends DataPopulation {

    protected SortedMap<Integer, Integer> regionRanks;

    protected SortedMap<Integer, Integer> densityRegionRanks;

    protected SortedMap<Integer, Double> percentRegion;

    protected SortedMap<Integer, Integer> birthRegionRanks;

    protected SortedMap<Integer, Integer> deathRegionRanks;

    protected SortedMap<Integer, Double> birthPercentRegion;

    protected SortedMap<Integer, Double> deathPercentRegion;

    protected SortedMap<Integer, Integer> birthPerCapitaRegionRanks;

    protected SortedMap<Integer, Integer> deathPerCapitaRegionRanks;

    public SortedMap<Integer, Integer> getRegionRanks() {
        return regionRanks;
    }

    public void setRegionRanks(SortedMap<Integer, Integer> regionRanks) {
        this.regionRanks = regionRanks;
    }

    public SortedMap<Integer, Integer> getDensityRegionRanks() {
        return densityRegionRanks;
    }

    public void setDensityRegionRanks(SortedMap<Integer, Integer> densityRegionRanks) {
        this.densityRegionRanks = densityRegionRanks;
    }

    public SortedMap<Integer, Double> getPercentRegion() {
        return percentRegion;
    }

    public void setPercentRegion(SortedMap<Integer, Double> percentRegion) {
        this.percentRegion = percentRegion;
    }

    public SortedMap<Integer, Integer> getBirthRegionRanks() {
        return birthRegionRanks;
    }

    public void setBirthRegionRanks(SortedMap<Integer, Integer> birthRegionRanks) {
        this.birthRegionRanks = birthRegionRanks;
    }

    public SortedMap<Integer, Integer> getDeathRegionRanks() {
        return deathRegionRanks;
    }

    public void setDeathRegionRanks(SortedMap<Integer, Integer> deathRegionRanks) {
        this.deathRegionRanks = deathRegionRanks;
    }

    public SortedMap<Integer, Double> getBirthPercentRegion() {
        return birthPercentRegion;
    }

    public void setBirthPercentRegion(SortedMap<Integer, Double> birthPercentRegion) {
        this.birthPercentRegion = birthPercentRegion;
    }

    public SortedMap<Integer, Double> getDeathPercentRegion() {
        return deathPercentRegion;
    }

    public void setDeathPercentRegion(SortedMap<Integer, Double> deathPercentRegion) {
        this.deathPercentRegion = deathPercentRegion;
    }

    public SortedMap<Integer, Integer> getBirthPerCapitaRegionRanks() {
        return birthPerCapitaRegionRanks;
    }

    public void setBirthPerCapitaRegionRanks(SortedMap<Integer, Integer> birthPerCapitaRegionRanks) {
        this.birthPerCapitaRegionRanks = birthPerCapitaRegionRanks;
    }

    public SortedMap<Integer, Integer> getDeathPerCapitaRegionRanks() {
        return deathPerCapitaRegionRanks;
    }

    public void setDeathPerCapitaRegionRanks(SortedMap<Integer, Integer> deathPerCapitaRegionRanks) {
        this.deathPerCapitaRegionRanks = deathPerCapitaRegionRanks;
    }

    @JsonIgnore
    public void setRegionRanks(Integer year, Integer rank) {
        if (this.regionRanks == null) {
            this.regionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.regionRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDensityRegionRanks(Integer year, Integer rank) {
        if (this.densityRegionRanks == null) {
            this.densityRegionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.densityRegionRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setPercentRegion(Integer year, Double percent) {
        if (this.percentRegion == null) {
            this.percentRegion = new TreeMap<>();
        }

        if (percent != null) {
            this.percentRegion.put(year, percent);
        }
    }

    @JsonIgnore
    public void setBirthRegionRanks(Integer year, Integer rank) {
        if (this.birthRegionRanks == null) {
            this.birthRegionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthRegionRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setBirthPercentRegion(Integer year, Double percent) {
        if (this.birthPercentRegion == null) {
            this.birthPercentRegion = new TreeMap<>();
        }

        if (percent != null) {
            this.birthPercentRegion.put(year, percent);
        }
    }

    @JsonIgnore
    public void setBirthPerCapitaRegionRanks(Integer year, Integer rank) {
        if (this.birthPerCapitaRegionRanks == null) {
            this.birthPerCapitaRegionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthPerCapitaRegionRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDeathRegionRanks(Integer year, Integer rank) {
        if (this.deathRegionRanks == null) {
            this.deathRegionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathRegionRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDeathPercentRegion(Integer year, Double percent) {
        if (this.deathPercentRegion == null) {
            this.deathPercentRegion = new TreeMap<>();
        }

        if (percent != null) {
            this.deathPercentRegion.put(year, percent);
        }
    }

    @JsonIgnore
    public void setDeathPerCapitaRegionRanks(Integer year, Integer rank) {
        if (this.deathPerCapitaRegionRanks == null) {
            this.deathPerCapitaRegionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathPerCapitaRegionRanks.put(year, rank);
        }
    }
}
