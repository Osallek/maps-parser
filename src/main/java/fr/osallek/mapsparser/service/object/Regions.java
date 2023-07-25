package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Regions extends DataNodes {

    @JsonProperty("regions")
    protected SortedMap<String, Region> regions; //Sorted only to be easier to explore manually

    public Regions() {
        super(Level.REGION);
    }

    public SortedMap<String, Region> getRegions() {
        return regions;
    }

    public void setRegions(SortedMap<String, Region> regions) {
        this.regions = regions;
    }
}
