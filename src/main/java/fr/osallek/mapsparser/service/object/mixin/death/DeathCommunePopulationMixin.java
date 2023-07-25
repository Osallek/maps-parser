package fr.osallek.mapsparser.service.object.mixin.death;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DeathCommunePopulationMixin {

    @JsonProperty("deathDepartementRanks")
    public abstract SortedMap<Integer, Integer> getDeathDepartementRanks();
}
