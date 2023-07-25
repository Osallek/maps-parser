package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.SortedSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Region extends DataNode<DataPopulation> {

    private String chefLieu;

    private SortedSet<String> departements;

    public Region(String id, String name, String chefLieu) {
        super(id, name, Level.REGION, new DataPopulation());
        this.chefLieu = chefLieu;
    }

    public String getChefLieu() {
        return chefLieu;
    }

    public void setChefLieu(String chefLieu) {
        this.chefLieu = chefLieu;
    }

    public SortedSet<String> getDepartements() {
        return departements;
    }

    public void setDepartements(SortedSet<String> departements) {
        this.departements = departements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Region region)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o))
                                  .append(chefLieu, region.chefLieu)
                                  .append(departements, region.departements)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(chefLieu).append(departements).toHashCode();
    }
}
