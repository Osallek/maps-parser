package fr.osallek.mapsparser.service.object.mixin.death;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DeathDepartementPopulationMixin {

    @JsonProperty("deathRegionRanks")
    public abstract SortedMap<Integer, Integer> getDeathRegionRanks();
}
