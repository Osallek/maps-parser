package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.SortedMap;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunePopulation extends DepartementPopulation {

    protected SortedMap<Integer, Integer> departementRanks;

    protected SortedMap<Integer, Integer> densityDepartementRanks;

    protected SortedMap<Integer, Double> percentDepartement;

    public SortedMap<Integer, Integer> getDepartementRanks() {
        return departementRanks;
    }

    public void setDepartementRanks(SortedMap<Integer, Integer> departementRanks) {
        this.departementRanks = departementRanks;
    }

    public SortedMap<Integer, Integer> getDensityDepartementRanks() {
        return densityDepartementRanks;
    }

    public void setDensityDepartementRanks(SortedMap<Integer, Integer> densityDepartementRanks) {
        this.densityDepartementRanks = densityDepartementRanks;
    }

    public SortedMap<Integer, Double> getPercentDepartement() {
        return percentDepartement;
    }

    public void setPercentDepartement(SortedMap<Integer, Double> percentDepartement) {
        this.percentDepartement = percentDepartement;
    }

    @JsonIgnore
    public void setDepartementRanks(Integer year, Integer rank) {
        if (this.departementRanks == null) {
            this.departementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.departementRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDensityDepartementRanks(Integer year, Integer rank) {
        if (this.densityDepartementRanks == null) {
            this.densityDepartementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.densityDepartementRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setPercentDepartement(Integer year, Double percent) {
        if (this.percentDepartement == null) {
            this.percentDepartement = new TreeMap<>();
        }

        if (percent != null) {
            this.percentDepartement.put(year, percent);
        }
    }
}
