package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public void initHash() {
        if (MapUtils.isNotEmpty(this.departements)) {
            this.departements.values().forEach(Hashed::initHash);
        }

        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Departements that)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o)).append(departements, that.departements).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(departements).toHashCode();
    }
}
