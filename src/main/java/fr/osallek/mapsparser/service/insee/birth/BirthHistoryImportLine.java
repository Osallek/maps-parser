package fr.osallek.mapsparser.service.insee.birth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BirthHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("NAISD21") Integer naisd21, @JsonProperty("NAISD20") Integer naisd20,
                                     @JsonProperty("NAISD19") Integer naisd19, @JsonProperty("NAISD18") Integer naisd18,
                                     @JsonProperty("NAISD17") Integer naisd17, @JsonProperty("NAISD16") Integer naisd16,
                                     @JsonProperty("NAISD15") Integer naisd15, @JsonProperty("NAISD14") Integer naisd14,
                                     @JsonProperty("NAISD13") Integer naisd13, @JsonProperty("NAISD12") Integer naisd12,
                                     @JsonProperty("NAISD11") Integer naisd11, @JsonProperty("NAISD10") Integer naisd10,
                                     @JsonProperty("NAISD09") Integer naisd09, @JsonProperty("NAISD08") Integer naisd08) {

    public BirthHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("NAISD21") Integer naisd21, @JsonProperty(
            "NAISD20") Integer naisd20, @JsonProperty("NAISD19") Integer naisd19, @JsonProperty("NAISD18") Integer naisd18, @JsonProperty(
            "NAISD17") Integer naisd17, @JsonProperty("NAISD16") Integer naisd16, @JsonProperty("NAISD15") Integer naisd15, @JsonProperty(
            "NAISD14") Integer naisd14, @JsonProperty("NAISD13") Integer naisd13, @JsonProperty("NAISD12") Integer naisd12, @JsonProperty(
            "NAISD11") Integer naisd11, @JsonProperty("NAISD10") Integer naisd10, @JsonProperty("NAISD09") Integer naisd09, @JsonProperty(
            "NAISD08") Integer naisd08) {
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.naisd21 = naisd21;
        this.naisd20 = naisd20;
        this.naisd19 = naisd19;
        this.naisd18 = naisd18;
        this.naisd17 = naisd17;
        this.naisd16 = naisd16;
        this.naisd15 = naisd15;
        this.naisd14 = naisd14;
        this.naisd13 = naisd13;
        this.naisd12 = naisd12;
        this.naisd11 = naisd11;
        this.naisd10 = naisd10;
        this.naisd09 = naisd09;
        this.naisd08 = naisd08;
    }
}
