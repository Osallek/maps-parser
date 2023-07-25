package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CommonCommuneMixin {

    @JsonProperty("departement")
    public abstract String getDepartement();

    @JsonProperty("zipCode")
    public abstract String getZipCode();
}
