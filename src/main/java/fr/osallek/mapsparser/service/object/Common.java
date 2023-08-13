package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedSet;
import java.util.TreeSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Common {

    @JsonProperty("communes")
    private SortedSet<String> communes;

    @JsonProperty("departements")
    private SortedSet<String> departements;

    @JsonProperty("regions")
    private SortedSet<String> regions;

    public Common(Data data) {
        this.communes = new TreeSet<>(data.getCommunes().getCommunes().keySet());
        this.departements = new TreeSet<>(data.getDepartements().getDepartements().keySet());
        this.regions = new TreeSet<>(data.getRegions().getRegions().keySet());
    }

    public SortedSet<String> getCommunes() {
        return communes;
    }

    public void setCommunes(SortedSet<String> communes) {
        this.communes = communes;
    }

    public SortedSet<String> getDepartements() {
        return departements;
    }

    public void setDepartements(SortedSet<String> departements) {
        this.departements = departements;
    }

    public SortedSet<String> getRegions() {
        return regions;
    }

    public void setRegions(SortedSet<String> regions) {
        this.regions = regions;
    }
}
