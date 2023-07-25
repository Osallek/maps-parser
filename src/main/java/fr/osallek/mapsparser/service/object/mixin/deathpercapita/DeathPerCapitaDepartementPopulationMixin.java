package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DeathPerCapitaDepartementPopulationMixin {

    @JsonProperty("deathPerCapitaRegionRanks")
    public abstract SortedMap<Integer, Integer> getDeathPerCapitaRegionRanks();
}
