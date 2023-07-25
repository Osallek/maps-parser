package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DensityDepartementPopulationMixin {

    @JsonProperty("densityRegionRanks")
    public abstract SortedMap<Integer, Integer> getDensityRegionRanks();
}
