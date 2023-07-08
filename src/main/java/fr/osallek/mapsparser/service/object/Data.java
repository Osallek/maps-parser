package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.SortedMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    private SortedMap<String, Commune> communes; //Sorted only to be easier to explore manually

    private SortedMap<String, Departement> departements; //Sorted only to be easier to explore manually

    private SortedMap<String, Region> regions; //Sorted only to be easier to explore manually

    public SortedMap<String, Commune> getCommunes() {
        return communes;
    }

    public void setCommunes(SortedMap<String, Commune> communes) {
        this.communes = communes;
    }

    public SortedMap<String, Departement> getDepartements() {
        return departements;
    }

    public void setDepartements(SortedMap<String, Departement> departements) {
        this.departements = departements;
    }

    public SortedMap<String, Region> getRegions() {
        return regions;
    }

    public void setRegions(SortedMap<String, Region> regions) {
        this.regions = regions;
    }
}
