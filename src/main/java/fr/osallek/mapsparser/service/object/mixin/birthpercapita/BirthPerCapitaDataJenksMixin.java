package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;
import java.util.SortedSet;

public abstract class BirthPerCapitaDataJenksMixin {

    @JsonProperty("birthPerCapita")
    public abstract SortedMap<Integer, SortedSet<Double>> getBirthPerCapita();
}
