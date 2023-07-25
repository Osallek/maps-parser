package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public abstract class CommonDepartementMixin {

    @JsonProperty("chefLieu")
    public abstract String getChefLieu();

    @JsonProperty("region")
    public abstract String getRegion();

    @JsonProperty("communes")
    public abstract Collection<String> getCommunes();
}
