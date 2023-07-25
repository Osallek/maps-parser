package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class DensityDataJenksMixin {

    @JsonProperty("density")
    public abstract SortedMap<Integer, SortedSet<Double>> getDensity();
}
