package fr.osallek.mapsparser.service.object.mixin.area;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AreaCommuneMixin {

    @JsonProperty("areaRegionRanks")
    public abstract Integer getAreaRegionRanks();

    @JsonProperty("areaDepartementRanks")
    public abstract Integer getAreaDepartementRanks();
}
