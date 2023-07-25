package fr.osallek.mapsparser.service.object.mixin.area;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedSet;

public abstract class AreaDataJenksMixin {

    @JsonProperty("area")
    public abstract SortedSet<Double> getArea();
}
