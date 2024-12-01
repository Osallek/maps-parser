package fr.osallek.mapsparser.service.insee.death;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeathHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("DECESD22") Integer decesd22, @JsonProperty("DECESD21") Integer decesd21,
                                     @JsonProperty("DECESD20") Integer decesd20, @JsonProperty("DECESD19") Integer decesd19,
                                     @JsonProperty("DECESD18") Integer decesd18, @JsonProperty("DECESD17") Integer decesd17,
                                     @JsonProperty("DECESD16") Integer decesd16, @JsonProperty("DECESD15") Integer decesd15,
                                     @JsonProperty("DECESD14") Integer decesd14, @JsonProperty("DECESD13") Integer decesd13,
                                     @JsonProperty("DECESD12") Integer decesd12, @JsonProperty("DECESD11") Integer decesd11,
                                     @JsonProperty("DECESD10") Integer decesd10, @JsonProperty("DECESD09") Integer decesd09,
                                     @JsonProperty("DECESD08") Integer decesd08) {

    public DeathHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("DECESD22") Integer decesd22, @JsonProperty("DECESD21") Integer decesd21,
                                  @JsonProperty("DECESD20") Integer decesd20, @JsonProperty("DECESD19") Integer decesd19,
                                  @JsonProperty("DECESD18") Integer decesd18, @JsonProperty("DECESD17") Integer decesd17,
                                  @JsonProperty("DECESD16") Integer decesd16, @JsonProperty("DECESD15") Integer decesd15,
                                  @JsonProperty("DECESD14") Integer decesd14, @JsonProperty("DECESD13") Integer decesd13,
                                  @JsonProperty("DECESD12") Integer decesd12, @JsonProperty("DECESD11") Integer decesd11,
                                  @JsonProperty("DECESD10") Integer decesd10, @JsonProperty("DECESD09") Integer decesd09,
                                  @JsonProperty("DECESD08") Integer decesd08) {
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.decesd22 = decesd22;
        this.decesd21 = decesd21;
        this.decesd20 = decesd20;
        this.decesd19 = decesd19;
        this.decesd18 = decesd18;
        this.decesd17 = decesd17;
        this.decesd16 = decesd16;
        this.decesd15 = decesd15;
        this.decesd14 = decesd14;
        this.decesd13 = decesd13;
        this.decesd12 = decesd12;
        this.decesd11 = decesd11;
        this.decesd10 = decesd10;
        this.decesd09 = decesd09;
        this.decesd08 = decesd08;
    }
}
