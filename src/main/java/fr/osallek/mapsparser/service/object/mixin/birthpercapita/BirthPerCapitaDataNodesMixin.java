package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataJenks;

public abstract class BirthPerCapitaDataNodesMixin {

    @JsonProperty("jenks")
    public abstract DataJenks getJenks();
}
