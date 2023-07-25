package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CommonIdNameMixin<I, N> {

    @JsonProperty("id")
    public abstract I getId();

    @JsonProperty("name")
    public abstract N getName();
}
