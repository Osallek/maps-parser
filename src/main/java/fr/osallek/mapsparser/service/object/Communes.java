package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public void initHash() {
        if (MapUtils.isNotEmpty(this.communes)) {
            this.communes.values().forEach(Hashed::initHash);
        }

        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Communes communes1)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o)).append(communes, communes1.communes).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(communes).toHashCode();
    }
}
