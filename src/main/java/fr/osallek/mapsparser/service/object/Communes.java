package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Communes extends DataNodes {

    @JsonProperty("communes")
    protected SortedMap<String, Commune> communes; //Sorted only to be easier to explore manually

    public Communes() {
        super(Level.COMMUNE);
    }

    public SortedMap<String, Commune> getCommunes() {
        return communes;
    }

    public void setCommunes(SortedMap<String, Commune> communes) {
        this.communes = communes;
    }
}
