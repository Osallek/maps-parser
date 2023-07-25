package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class PopDataJenksMixin {

    @JsonProperty("population")
    public abstract SortedMap<Integer, SortedSet<Double>> getPopulation();
}
