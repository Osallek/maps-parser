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

    protected SortedMap<Integer, Integer> birthDepartementRanks;

    protected SortedMap<Integer, Integer> deathDepartementRanks;

    protected SortedMap<Integer, Double> birthPercentDepartement;

    protected SortedMap<Integer, Double> deathPercentDepartement;

    protected SortedMap<Integer, Integer> birthPerCapitaDepartementRanks;

    protected SortedMap<Integer, Integer> deathPerCapitaDepartementRanks;


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

    public SortedMap<Integer, Integer> getBirthDepartementRanks() {
        return birthDepartementRanks;
    }

    public void setBirthDepartementRanks(SortedMap<Integer, Integer> birthDepartementRanks) {
        this.birthDepartementRanks = birthDepartementRanks;
    }

    public SortedMap<Integer, Integer> getDeathDepartementRanks() {
        return deathDepartementRanks;
    }

    public void setDeathDepartementRanks(SortedMap<Integer, Integer> deathDepartementRanks) {
        this.deathDepartementRanks = deathDepartementRanks;
    }

    public SortedMap<Integer, Double> getBirthPercentDepartement() {
        return birthPercentDepartement;
    }

    public void setBirthPercentDepartement(SortedMap<Integer, Double> birthPercentDepartement) {
        this.birthPercentDepartement = birthPercentDepartement;
    }

    public SortedMap<Integer, Double> getDeathPercentDepartement() {
        return deathPercentDepartement;
    }

    public void setDeathPercentDepartement(SortedMap<Integer, Double> deathPercentDepartement) {
        this.deathPercentDepartement = deathPercentDepartement;
    }

    public SortedMap<Integer, Integer> getBirthPerCapitaDepartementRanks() {
        return birthPerCapitaDepartementRanks;
    }

    public void setBirthPerCapitaDepartementRanks(SortedMap<Integer, Integer> birthPerCapitaDepartementRanks) {
        this.birthPerCapitaDepartementRanks = birthPerCapitaDepartementRanks;
    }

    public SortedMap<Integer, Integer> getDeathPerCapitaDepartementRanks() {
        return deathPerCapitaDepartementRanks;
    }

    public void setDeathPerCapitaDepartementRanks(SortedMap<Integer, Integer> deathPerCapitaDepartementRanks) {
        this.deathPerCapitaDepartementRanks = deathPerCapitaDepartementRanks;
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

    @JsonIgnore
    public void setBirthDepartementRanks(Integer year, Integer rank) {
        if (this.birthDepartementRanks == null) {
            this.birthDepartementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthDepartementRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setBirthPercentDepartement(Integer year, Double percent) {
        if (this.birthPercentDepartement == null) {
            this.birthPercentDepartement = new TreeMap<>();
        }

        if (percent != null) {
            this.birthPercentDepartement.put(year, percent);
        }
    }

    @JsonIgnore
    public void setBirthPerCapitaDepartementRanks(Integer year, Integer rank) {
        if (this.birthPerCapitaDepartementRanks == null) {
            this.birthPerCapitaDepartementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthPerCapitaDepartementRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDeathDepartementRanks(Integer year, Integer rank) {
        if (this.deathDepartementRanks == null) {
            this.deathDepartementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathDepartementRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDeathPercentDepartement(Integer year, Double percent) {
        if (this.deathPercentDepartement == null) {
            this.deathPercentDepartement = new TreeMap<>();
        }

        if (percent != null) {
            this.deathPercentDepartement.put(year, percent);
        }
    }

    @JsonIgnore
    public void setDeathPerCapitaDepartementRanks(Integer year, Integer rank) {
        if (this.deathPerCapitaDepartementRanks == null) {
            this.deathPerCapitaDepartementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathPerCapitaDepartementRanks.put(year, rank);
        }
    }
}
