package fr.osallek.mapsparser.service.object.mixin.birth;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class BirthDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
