package fr.osallek.mapsparser.service.object.mixin.density;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.osallek.mapsparser.service.object.DataPopulation;

public abstract class DensityDataNodeMixin<P extends DataPopulation> {

    @JsonProperty("population")
    public abstract P getPopulation();
}
