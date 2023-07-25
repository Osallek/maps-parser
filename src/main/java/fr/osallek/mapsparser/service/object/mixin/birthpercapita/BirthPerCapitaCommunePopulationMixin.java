package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class BirthPerCapitaCommunePopulationMixin {

    @JsonProperty("birthPerCapitaDepartementRanks")
    public abstract SortedMap<Integer, Integer> getBirthPerCapitaDepartementRanks();
}
