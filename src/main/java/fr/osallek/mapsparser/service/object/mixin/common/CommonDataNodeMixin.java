package fr.osallek.mapsparser.service.object.mixin.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.Level;

public abstract class CommonDataNodeMixin {

    @JsonProperty("level")
    public abstract Level getLevel();
}
