package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataPopulation;

public abstract class BirthPerCapitaDataNodeMixin<P extends DataPopulation> {

    @JsonProperty("population")
    public abstract P getPopulation();
}
