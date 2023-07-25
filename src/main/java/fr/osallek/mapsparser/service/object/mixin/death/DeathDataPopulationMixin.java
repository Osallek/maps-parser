package fr.osallek.mapsparser.service.object.mixin.death;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.SortedMap;

public abstract class DeathDataPopulationMixin {

    @JsonProperty("death")
    public abstract SortedMap<Integer, BigDecimal> getDeath();

    @JsonProperty("deathCountryRanks")
    public abstract SortedMap<Integer, Integer> getDeathCountryRanks();
}
