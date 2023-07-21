package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commune extends DataNode<CommunePopulation> {

    private String departement;

    private String zipCode;

    private Point point;

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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
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
}
