package fr.osallek.mapsparser.service.object.mixin.birth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class BirthDataJenksMixin {

    @JsonProperty("birth")
    public abstract SortedMap<Integer, SortedSet<Double>> getBirth();
}
