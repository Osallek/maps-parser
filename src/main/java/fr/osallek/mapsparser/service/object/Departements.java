package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Departements extends DataNodes {

    @JsonProperty("departements")
    protected SortedMap<String, Departement> departements; //Sorted only to be easier to explore manually

    public Departements() {
        super(Level.DEPARTEMENT);
    }

    public SortedMap<String, Departement> getDepartements() {
        return departements;
    }

    public void setDepartements(SortedMap<String, Departement> departements) {
        this.departements = departements;
    }
}
