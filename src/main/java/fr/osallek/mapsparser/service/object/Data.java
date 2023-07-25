package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data extends Hashed {

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

    @Override
    public void initHash() {
        this.communes.initHash();
        this.departements.initHash();
        this.regions.initHash();
        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Data data)) {
            return false;
        }

        return new EqualsBuilder().append(communes, data.communes)
                                  .append(departements, data.departements)
                                  .append(regions, data.regions)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(communes).append(departements).append(regions).toHashCode();
    }
}
