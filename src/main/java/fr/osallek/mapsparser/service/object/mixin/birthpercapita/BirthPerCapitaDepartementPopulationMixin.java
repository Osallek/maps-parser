package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class BirthPerCapitaDepartementPopulationMixin {

    @JsonProperty("birthPerCapitaRegionRanks")
    public abstract SortedMap<Integer, Integer> getBirthPerCapitaRegionRanks();
}
