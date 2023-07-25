package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class PopCommunePopulationMixin {

    @JsonProperty("departementRanks")
    public abstract SortedMap<Integer, Integer> getDepartementRanks();

    @JsonProperty("percentDepartement")
    public abstract SortedMap<Integer, Double> getPercentDepartement();
}
