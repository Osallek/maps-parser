package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class DensityDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
