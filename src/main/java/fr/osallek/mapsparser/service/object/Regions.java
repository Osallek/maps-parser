package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public void initHash() {
        if (MapUtils.isNotEmpty(this.regions)) {
            this.regions.values().forEach(Hashed::initHash);
        }

        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Regions regions1)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o)).append(regions, regions1.regions).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(regions).toHashCode();
    }
}
