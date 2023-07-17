package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.SortedMap;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunePopulation extends DepartementPopulation {

    protected SortedMap<Integer, Integer> departementRanks;

    public SortedMap<Integer, Integer> getDepartementRanks() {
        return departementRanks;
    }

    public void setDepartementRanks(SortedMap<Integer, Integer> departementRanks) {
        this.departementRanks = departementRanks;
    }

    @JsonIgnore
    public void setDepartementRanks(Integer year, Integer rank) {
        if (this.departementRanks == null) {
            this.departementRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.departementRanks.put(year, rank);
        }
    }
}
