package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Region extends DataNode<DataPopulation> {

    private String chefLieu;

    private Collection<String> departements;

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

    public Collection<String> getDepartements() {
        return departements;
    }

    public void setDepartements(Collection<String> departements) {
        this.departements = departements;
    }
}
