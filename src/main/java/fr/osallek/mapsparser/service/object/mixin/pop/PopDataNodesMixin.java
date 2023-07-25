package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class PopDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
