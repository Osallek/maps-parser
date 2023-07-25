package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Departement extends DataNode<DepartementPopulation> {

    private String chefLieu;

    private String region;

    private Collection<String> communes;

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

    public Collection<String> getCommunes() {
        return communes;
    }

    public void setCommunes(Collection<String> communes) {
        this.communes = communes;
    }

    public Integer getAreaRegionRanks() {
        return areaRegionRanks;
    }

    public void setAreaRegionRanks(Integer areaRegionRanks) {
        this.areaRegionRanks = areaRegionRanks;
    }
}
