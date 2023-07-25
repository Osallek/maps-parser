package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DensityCommunePopulationMixin {

    @JsonProperty("densityDepartementRanks")
    public abstract SortedMap<Integer, Integer> getDensityDepartementRanks();
}
