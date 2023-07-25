package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class PopDataPopulationMixin {

    @JsonProperty("population")
    public abstract SortedMap<Integer, Integer> getPopulation();

    @JsonProperty("countryRanks")
    public abstract SortedMap<Integer, Integer> getCountryRanks();

    @JsonProperty("percentCountry")
    public abstract SortedMap<Integer, Double> getPercentCountry();
}
