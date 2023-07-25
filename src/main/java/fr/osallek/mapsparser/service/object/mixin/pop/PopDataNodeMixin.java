package fr.osallek.mapsparser.service.object.mixin.pop;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataPopulation;

public abstract class PopDataNodeMixin<P extends DataPopulation> {

    @JsonProperty("population")
    public abstract P getPopulation();
}
