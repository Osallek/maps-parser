package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.Point;

public abstract class CommonCommuneMixin {

    @JsonProperty("point")
    public abstract Point getPoint();

    @JsonProperty("departement")
    public abstract String getDepartement();

    @JsonProperty("zipCode")
    public abstract String getZipCode();
}
