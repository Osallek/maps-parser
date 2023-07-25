package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commune extends DataNode<CommunePopulation> {

    private String departement;

    private String zipCode;

    private Integer areaRegionRanks;

    private Integer areaDepartementRanks;

    public Commune(String id, String name, String departement) {
        super(id, name, Level.COMMUNE, new CommunePopulation());
        this.departement = departement;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getAreaRegionRanks() {
        return areaRegionRanks;
    }

    public void setAreaRegionRanks(Integer areaRegionRanks) {
        this.areaRegionRanks = areaRegionRanks;
    }

    public Integer getAreaDepartementRanks() {
        return areaDepartementRanks;
    }

    public void setAreaDepartementRanks(Integer areaDepartementRanks) {
        this.areaDepartementRanks = areaDepartementRanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Commune commune)) {
            return false;
        }

        return new EqualsBuilder().appendSuper(super.equals(o))
                                  .append(departement, commune.departement)
                                  .append(zipCode, commune.zipCode)
                                  .append(areaRegionRanks, commune.areaRegionRanks)
                                  .append(areaDepartementRanks, commune.areaDepartementRanks)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
                                          .append(departement)
                                          .append(zipCode)
                                          .append(areaRegionRanks)
                                          .append(areaDepartementRanks)
                                          .toHashCode();
    }
}
