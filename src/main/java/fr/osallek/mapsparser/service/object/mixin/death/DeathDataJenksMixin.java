package fr.osallek.mapsparser.service.object.mixin.death;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class DeathDataJenksMixin {

    @JsonProperty("death")
    public abstract SortedMap<Integer, SortedSet<Double>> getDeath();
}
