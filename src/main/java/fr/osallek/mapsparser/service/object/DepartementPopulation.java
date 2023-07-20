package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartementPopulation extends DataPopulation {

    protected SortedMap<Integer, Integer> regionRanks;

    protected SortedMap<Integer, Integer> densityRegionRanks;

    protected SortedMap<Integer, Double> percentRegion;

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
}
