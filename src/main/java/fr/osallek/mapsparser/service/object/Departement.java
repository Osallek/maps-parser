package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.SortedSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Departement extends DataNode<DepartementPopulation> {

    private String chefLieu;

    private String region;

    private SortedSet<String> communes;

    protected Integer areaRegionRanks;

    public Departement(String id, String name, String region, String chefLieu) {
        super(id, name, Level.DEPARTEMENT, new DepartementPopulation());
        this.region = region;
        this.chefLieu = chefLieu;
    }

    public String getChefLieu() {
        return chefLieu;
    }

    public void setChefLieu(String chefLieu) {
        this.chefLieu = chefLieu;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public SortedSet<String> getCommunes() {
        return communes;
    }

    public void setCommunes(SortedSet<String> communes) {
        this.communes = communes;
    }

    public Integer getAreaRegionRanks() {
        return areaRegionRanks;
    }

    public void setAreaRegionRanks(Integer areaRegionRanks) {
        this.areaRegionRanks = areaRegionRanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Departement that)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o))
                                  .append(chefLieu, that.chefLieu)
                                  .append(region, that.region)
                                  .append(communes, that.communes)
                                  .append(areaRegionRanks, that.areaRegionRanks)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(chefLieu).append(region).append(communes).append(areaRegionRanks).toHashCode();
    }
}
