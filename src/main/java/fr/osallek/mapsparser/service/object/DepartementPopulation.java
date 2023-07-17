package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.ObjectUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartementPopulation extends DataPopulation {

    protected SortedMap<Integer, Integer> regionRanks;

    public SortedMap<Integer, Integer> getRegionRanks() {
        return regionRanks;
    }

    public void setRegionRanks(SortedMap<Integer, Integer> regionRanks) {
        this.regionRanks = regionRanks;
    }

    @JsonIgnore
    public void setRegionRanks(Integer year, Integer rank) {
        if (this.regionRanks == null) {
            this.regionRanks = new TreeMap<>();
        }

        if (rank != null) {
            this.regionRanks.put(year, rank);
        }
    }
}
