package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SortedMap;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPopulation {

    protected SortedMap<Integer, Integer> population;

    protected SortedMap<Integer, BigDecimal> density;

    protected SortedMap<Integer, Integer> countryRanks;

    protected SortedMap<Integer, Integer> densityCountryRanks;

    protected SortedMap<Integer, Double> percentCountry;

    protected SortedMap<Integer, Integer> birth;

    protected SortedMap<Integer, Integer> death;

    protected SortedMap<Integer, Integer> birthCountryRanks;

    protected SortedMap<Integer, Integer> deathCountryRanks;

    protected SortedMap<Integer, Double> birthPercentCountry;

    protected SortedMap<Integer, Double> deathPercentCountry;

    protected SortedMap<Integer, BigDecimal> birthPerCapita;

    protected SortedMap<Integer, BigDecimal> deathPerCapita;

    protected SortedMap<Integer, Integer> birthPerCapitaCountryRanks;

    protected SortedMap<Integer, Integer> deathPerCapitaCountryRanks;

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

    public SortedMap<Integer, Double> getPercentCountry() {
        return percentCountry;
    }

    public void setPercentCountry(SortedMap<Integer, Double> percentCountry) {
        this.percentCountry = percentCountry;
    }

    public SortedMap<Integer, Integer> getBirth() {
        return birth;
    }

    public void setBirth(SortedMap<Integer, Integer> birth) {
        this.birth = birth;
    }

    public SortedMap<Integer, Integer> getDeath() {
        return death;
    }

    public void setDeath(SortedMap<Integer, Integer> death) {
        this.death = death;
    }

    public SortedMap<Integer, Integer> getBirthCountryRanks() {
        return birthCountryRanks;
    }

    public void setBirthCountryRanks(SortedMap<Integer, Integer> birthCountryRanks) {
        this.birthCountryRanks = birthCountryRanks;
    }

    public SortedMap<Integer, Integer> getDeathCountryRanks() {
        return deathCountryRanks;
    }

    public SortedMap<Integer, Double> getBirthPercentCountry() {
        return birthPercentCountry;
    }

    public void setBirthPercentCountry(SortedMap<Integer, Double> birthPercentCountry) {
        this.birthPercentCountry = birthPercentCountry;
    }

    public SortedMap<Integer, Double> getDeathPercentCountry() {
        return deathPercentCountry;
    }

    public void setDeathPercentCountry(SortedMap<Integer, Double> deathPercentCountry) {
        this.deathPercentCountry = deathPercentCountry;
    }

    public SortedMap<Integer, BigDecimal> getBirthPerCapita() {
        return birthPerCapita;
    }

    public void setBirthPerCapita(SortedMap<Integer, BigDecimal> birthPerCapita) {
        this.birthPerCapita = birthPerCapita;
    }

    public SortedMap<Integer, BigDecimal> getDeathPerCapita() {
        return deathPerCapita;
    }

    public void setDeathPerCapita(SortedMap<Integer, BigDecimal> deathPerCapita) {
        this.deathPerCapita = deathPerCapita;
    }

    public SortedMap<Integer, Integer> getBirthPerCapitaCountryRanks() {
        return birthPerCapitaCountryRanks;
    }

    public void setBirthPerCapitaCountryRanks(SortedMap<Integer, Integer> birthPerCapitaCountryRanks) {
        this.birthPerCapitaCountryRanks = birthPerCapitaCountryRanks;
    }

    public SortedMap<Integer, Integer> getDeathPerCapitaCountryRanks() {
        return deathPerCapitaCountryRanks;
    }

    public void setDeathPerCapitaCountryRanks(SortedMap<Integer, Integer> deathPerCapitaCountryRanks) {
        this.deathPerCapitaCountryRanks = deathPerCapitaCountryRanks;
    }

    public void setDeathCountryRanks(SortedMap<Integer, Integer> deathCountryRanks) {
        this.deathCountryRanks = deathCountryRanks;
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

    @JsonIgnore
    public void setPercentCountry(Integer year, Double percent) {
        if (this.percentCountry == null) {
            this.percentCountry = new TreeMap<>();
        }

        if (percent != null) {
            this.percentCountry.put(year, percent);
        }
    }

    @JsonIgnore
    public void addBirth(Integer year, Integer birth) {
        if (this.birth == null) {
            this.birth = new TreeMap<>();
        }

        if (this.birthPerCapita == null) {
            this.birthPerCapita = new TreeMap<>();
        }

        if (birth != null) {
            this.birth.compute(year, (y, old) -> ObjectUtils.defaultIfNull(old, 0) + ObjectUtils.defaultIfNull(birth, 0));

            if (birth == 0) {
                this.birthPerCapita.put(year, BigDecimal.ZERO);
            } else if (MapUtils.isNotEmpty(this.population) && this.population.containsKey(year)) {
                if (this.population.get(year) == 0) {
                    this.birthPerCapita.put(year, BigDecimal.ZERO);
                } else {
                    this.birthPerCapita.put(year, BigDecimal.valueOf(this.birth.get(year)).multiply(BigDecimal.valueOf(1000))
                                                            .divide(BigDecimal.valueOf(this.population.get(year)), 1, RoundingMode.FLOOR));
                }
            }
        }
    }

    @JsonIgnore
    public void setBirthCountryRanks(Integer year, Integer rank) {
        if (this.birthCountryRanks == null) {
            this.birthCountryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthCountryRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setBirthPercentCountry(Integer year, Double percent) {
        if (this.birthPercentCountry == null) {
            this.birthPercentCountry = new TreeMap<>();
        }

        if (percent != null) {
            this.birthPercentCountry.put(year, percent);
        }
    }

    @JsonIgnore
    public void setBirthPerCapitaCountryRanks(Integer year, Integer rank) {
        if (this.birthPerCapitaCountryRanks == null) {
            this.birthPerCapitaCountryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.birthPerCapitaCountryRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void addDeath(Integer year, Integer death) {
        if (this.death == null) {
            this.death = new TreeMap<>();
        }

        if (this.deathPerCapita == null) {
            this.deathPerCapita = new TreeMap<>();
        }

        if (death != null) {
            this.death.compute(year, (y, old) -> ObjectUtils.defaultIfNull(old, 0) + ObjectUtils.defaultIfNull(death, 0));

            if (death == 0) {
                this.deathPerCapita.put(year, BigDecimal.ZERO);
            } else if (MapUtils.isNotEmpty(this.population) && this.population.containsKey(year)) {
                if (this.population.get(year) == 0) {
                    this.deathPerCapita.put(year, BigDecimal.ZERO);
                } else {
                    this.deathPerCapita.put(year, BigDecimal.valueOf(this.death.get(year)).multiply(BigDecimal.valueOf(1000))
                                                            .divide(BigDecimal.valueOf(this.population.get(year)), 1, RoundingMode.FLOOR));
                }
            }
        }
    }

    @JsonIgnore
    public void setDeathCountryRanks(Integer year, Integer rank) {
        if (this.deathCountryRanks == null) {
            this.deathCountryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathCountryRanks.put(year, rank);
        }
    }

    @JsonIgnore
    public void setDeathPercentCountry(Integer year, Double percent) {
        if (this.deathPercentCountry == null) {
            this.deathPercentCountry = new TreeMap<>();
        }

        if (percent != null) {
            this.deathPercentCountry.put(year, percent);
        }
    }

    @JsonIgnore
    public void setDeathPerCapitaCountryRanks(Integer year, Integer rank) {
        if (this.deathPerCapitaCountryRanks == null) {
            this.deathPerCapitaCountryRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.deathPerCapitaCountryRanks.put(year, rank);
        }
    }
}
