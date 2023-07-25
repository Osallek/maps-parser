package fr.osallek.mapsparser.service.object.mixin.birthpercapita;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.SortedMap;

public abstract class BirthPerCapitaDataPopulationMixin {

    @JsonProperty("birthPerCapita")
    public abstract SortedMap<Integer, BigDecimal> getBirthPerCapita();

    @JsonProperty("birthPerCapitaCountryRanks")
    public abstract SortedMap<Integer, Integer> getBirthPerCapitaCountryRanks();
}
