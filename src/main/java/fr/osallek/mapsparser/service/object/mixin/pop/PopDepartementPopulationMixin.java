package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class PopDepartementPopulationMixin {

    @JsonProperty("regionRanks")
    public abstract SortedMap<Integer, Integer> getRegionRanks();

    @JsonProperty("percentRegion")
    public abstract SortedMap<Integer, Double> getPercentRegion();
}
