package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    @JsonProperty("communes")
    private Communes communes;

    @JsonProperty("departements")
    private Departements departements;

    @JsonProperty("regions")
    private Regions regions;

    public Communes getCommunes() {
        if (this.communes == null) {
            this.communes = new Communes();
        }

        return communes;
    }

    public void setCommunes(Communes communes) {
        this.communes = communes;
    }

    public Departements getDepartements() {
        if (this.departements == null) {
            this.departements = new Departements();
        }

        return departements;
    }

    public void setDepartements(Departements departements) {
        this.departements = departements;
    }

    public Regions getRegions() {
        if (this.regions == null) {
            this.regions = new Regions();
        }

        return regions;
    }

    public void setRegions(Regions regions) {
        this.regions = regions;
    }
}
