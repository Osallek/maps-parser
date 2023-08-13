package fr.osallek.mapsparser.service.insee.population.all;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopAllImportLine(@JsonProperty("NIVGEO") @JsonAlias("NIVEAU") String type, @JsonProperty("CODGEO") String id,
                               @JsonProperty("SEXE") @JsonAlias("C_SEXE") int sexe, @JsonProperty("AGED100") @JsonAlias("C_AGED100") Integer age,
                               @JsonProperty("NB") Integer nb) {

    @JsonCreator
    public PopAllImportLine(@JsonProperty("NIVGEO") @JsonAlias("NIVEAU") String type, @JsonProperty("CODGEO") String id,
                            @JsonProperty("SEXE") @JsonAlias("C_SEXE") int sexe, @JsonProperty("AGED100") @JsonAlias("C_AGED100") Integer age,
                            @JsonProperty("NB") String nb) {
        this(StringUtils.normalizeSpace(StringUtils.trimToNull(type)), StringUtils.normalizeSpace(StringUtils.trimToNull(id)), sexe, age,
             StringUtils.isBlank(nb) ? null : new BigDecimal(nb.replace(',', '.')).setScale(0, RoundingMode.HALF_UP).intValue());
    }
}
