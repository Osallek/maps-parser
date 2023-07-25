package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

public abstract class DeathPerCapitaCommunePopulationMixin {

    @JsonProperty("deathPerCapitaDepartementRanks")
    public abstract SortedMap<Integer, Integer> getDeathPerCapitaDepartementRanks();
}
