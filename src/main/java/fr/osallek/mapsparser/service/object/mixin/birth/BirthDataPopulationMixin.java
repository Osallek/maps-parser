package fr.osallek.mapsparser.service.object.mixin.birth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.SortedMap;

public abstract class BirthDataPopulationMixin {

    @JsonProperty("birth")
    public abstract SortedMap<Integer, BigDecimal> getBirth();

    @JsonProperty("birthCountryRanks")
    public abstract SortedMap<Integer, Integer> getBirthCountryRanks();
}
