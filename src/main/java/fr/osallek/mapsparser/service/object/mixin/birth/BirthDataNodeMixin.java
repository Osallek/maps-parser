package fr.osallek.mapsparser.service.object.mixin.birth;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataPopulation;

public abstract class BirthDataNodeMixin<P extends DataPopulation> {

    @JsonProperty("population")
    public abstract P getPopulation();
}
