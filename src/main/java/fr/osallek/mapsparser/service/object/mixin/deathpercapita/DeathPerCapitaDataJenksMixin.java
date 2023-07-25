package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class DeathPerCapitaDataJenksMixin {

    @JsonProperty("deathPerCapita")
    public abstract SortedMap<Integer, SortedSet<Double>> getDeathPerCapita();
}
