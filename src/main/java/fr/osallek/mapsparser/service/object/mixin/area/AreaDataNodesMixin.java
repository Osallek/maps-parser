package fr.osallek.mapsparser.service.object.mixin.area;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class AreaDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
