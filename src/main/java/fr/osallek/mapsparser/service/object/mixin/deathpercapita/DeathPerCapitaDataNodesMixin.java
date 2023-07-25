package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class DeathPerCapitaDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
