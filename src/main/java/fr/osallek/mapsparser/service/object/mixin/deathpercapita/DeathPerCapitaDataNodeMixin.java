package fr.osallek.mapsparser.service.object.mixin.deathpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataPopulation;

public abstract class DeathPerCapitaDataNodeMixin<P extends DataPopulation> {

    @JsonProperty("population")
    public abstract P getPopulation();
}
