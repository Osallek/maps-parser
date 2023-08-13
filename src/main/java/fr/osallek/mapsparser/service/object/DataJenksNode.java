package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataJenksNode extends Hashed {

    @JsonProperty("regions")
    protected SortedMap<String, Integer> regions; //Sorted only to be easier to explore manually

    protected int nbRegions;

    @JsonProperty("departements")
    protected SortedMap<String, Integer> departements; //Sorted only to be easier to explore manually

    protected int nbDepartements;

    @JsonProperty("communes")
    protected SortedMap<String, Integer> communes; //Sorted only to be easier to explore manually

    protected int nbCommunes;

    public DataJenksNode() {
    }

    public DataJenksNode(SortedMap<String, Integer> regions, SortedMap<String, Integer> departements, SortedMap<String, Integer> communes) {
        this.regions = regions;
        this.departements = departements;
        this.communes = communes;
        this.nbRegions = regions.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        this.nbDepartements = departements.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        this.nbCommunes = communes.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public SortedMap<String, Integer> getRegions() {
        return regions;
    }

    public void setRegions(SortedMap<String, Integer> regions) {
        this.regions = regions;
    }

    public SortedMap<String, Integer> getDepartements() {
        return departements;
    }

    public void setDepartements(SortedMap<String, Integer> departements) {
        this.departements = departements;
    }

    public SortedMap<String, Integer> getCommunes() {
        return communes;
    }

    public void setCommunes(SortedMap<String, Integer> communes) {
        this.communes = communes;
    }

    public int getNbRegions() {
        return nbRegions;
    }

    public void setNbRegions(int nbRegions) {
        this.nbRegions = nbRegions;
    }

    public int getNbDepartements() {
        return nbDepartements;
    }

    public void setNbDepartements(int nbDepartements) {
        this.nbDepartements = nbDepartements;
    }

    public int getNbCommunes() {
        return nbCommunes;
    }

    public void setNbCommunes(int nbCommunes) {
        this.nbCommunes = nbCommunes;
    }
}
