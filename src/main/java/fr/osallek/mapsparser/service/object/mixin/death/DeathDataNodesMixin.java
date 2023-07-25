package fr.osallek.mapsparser.service.object.mixin.death;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class DeathDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
