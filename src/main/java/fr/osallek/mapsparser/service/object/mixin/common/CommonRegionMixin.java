package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public abstract class CommonRegionMixin {

    @JsonProperty("chefLieu")
    public abstract String getChefLieu();

    @JsonProperty("departements")
    public abstract Collection<String> getDepartements();
}
