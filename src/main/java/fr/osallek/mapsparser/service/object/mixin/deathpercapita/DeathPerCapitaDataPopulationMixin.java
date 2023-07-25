package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.SortedMap;

public abstract class DeathPerCapitaDataPopulationMixin {

    @JsonProperty("deathPerCapita")
    public abstract SortedMap<Integer, BigDecimal> getDeathPerCapita();

    @JsonProperty("deathPerCapitaCountryRanks")
    public abstract SortedMap<Integer, Integer> getDeathPerCapitaCountryRanks();
}
