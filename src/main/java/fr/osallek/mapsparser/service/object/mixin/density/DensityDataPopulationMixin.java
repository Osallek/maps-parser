package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.SortedMap;

public abstract class DensityDataPopulationMixin {

    @JsonProperty("density")
    public abstract SortedMap<Integer, BigDecimal> getDensity();

    @JsonProperty("densityCountryRanks")
    public abstract SortedMap<Integer, Integer> getDensityCountryRanks();
}
