package fr.osallek.mapsparser.service.object.mixin.area;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AreaDepartementMixin {

    @JsonProperty("areaRegionRanks")
    public abstract Integer getAreaRegionRanks();
}
