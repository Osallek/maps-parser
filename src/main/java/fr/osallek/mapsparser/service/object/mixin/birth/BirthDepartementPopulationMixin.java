package fr.osallek.mapsparser.service.object.mixin.birth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class BirthDepartementPopulationMixin {

    @JsonProperty("birthRegionRanks")
    public abstract SortedMap<Integer, Integer> getBirthRegionRanks();
}
