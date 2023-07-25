package fr.osallek.mapsparser.service.object.mixin.area;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public abstract class AreaDataNodeMixin {

    @JsonProperty("area")
    public abstract BigDecimal getArea();

    @JsonProperty("areaCountryRanks")
    public abstract Integer getAreaCountryRanks();
}
