package fr.osallek.mapsparser.service.insee.population;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopulationHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("REG") String region, @JsonProperty("DEP") String departement,
                                          @JsonProperty("PMUN20") Integer pmun20, @JsonProperty("PMUN19") Integer pmun19,
                                          @JsonProperty("PMUN18") Integer pmun18, @JsonProperty("PMUN17") Integer pmun17,
                                          @JsonProperty("PMUN16") Integer pmun16, @JsonProperty("PMUN15") Integer pmun15,
                                          @JsonProperty("PMUN14") Integer pmun14, @JsonProperty("PMUN13") Integer pmun13,
                                          @JsonProperty("PMUN12") Integer pmun12, @JsonProperty("PMUN11") Integer pmun11,
                                          @JsonProperty("PMUN10") Integer pmun10, @JsonProperty("PMUN09") Integer pmun09,
                                          @JsonProperty("PMUN08") Integer pmun08, @JsonProperty("PMUN07") Integer pmun07,
                                          @JsonProperty("PMUN06") Integer pmun06, @JsonProperty("PSDC99") Integer psdc99,
                                          @JsonProperty("PSDC90") Integer psdc90, @JsonProperty("PSDC82") Integer psdc82,
                                          @JsonProperty("PSDC75") Integer psdc75, @JsonProperty("PSDC68") Integer psdc68,
                                          @JsonProperty("PSDC62") Integer psdc62, @JsonProperty("PTOT54") Integer ptot54,
                                          @JsonProperty("PTOT36") Integer ptot36, @JsonProperty("PTOT1931") Integer ptot1931,
                                          @JsonProperty("PTOT1926") Integer ptot1926, @JsonProperty("PTOT1921") Integer ptot1921,
                                          @JsonProperty("PTOT1911") Integer ptot1911, @JsonProperty("PTOT1906") Integer ptot1906,
                                          @JsonProperty("PTOT1901") Integer ptot1901, @JsonProperty("PTOT1896") Integer ptot1896,
                                          @JsonProperty("PTOT1891") Integer ptot1891, @JsonProperty("PTOT1886") Integer ptot1886,
                                          @JsonProperty("PTOT1881") Integer ptot1881, @JsonProperty("PTOT1876") Integer ptot1876) {

    public PopulationHistoryImportLine(@JsonProperty("CODGEO") String id, @JsonProperty("REG") String region, @JsonProperty(
            "DEP") String departement, @JsonProperty("PMUN20") Integer pmun20, @JsonProperty("PMUN19") Integer pmun19, @JsonProperty(
            "PMUN18") Integer pmun18, @JsonProperty("PMUN17") Integer pmun17, @JsonProperty("PMUN16") Integer pmun16, @JsonProperty(
            "PMUN15") Integer pmun15, @JsonProperty("PMUN14") Integer pmun14, @JsonProperty("PMUN13") Integer pmun13, @JsonProperty(
            "PMUN12") Integer pmun12, @JsonProperty("PMUN11") Integer pmun11, @JsonProperty("PMUN10") Integer pmun10, @JsonProperty(
            "PMUN09") Integer pmun09, @JsonProperty("PMUN08") Integer pmun08, @JsonProperty("PMUN07") Integer pmun07, @JsonProperty(
            "PMUN06") Integer pmun06, @JsonProperty("PSDC99") Integer psdc99, @JsonProperty("PSDC90") Integer psdc90, @JsonProperty(
            "PSDC82") Integer psdc82, @JsonProperty("PSDC75") Integer psdc75, @JsonProperty("PSDC68") Integer psdc68, @JsonProperty(
            "PSDC62") Integer psdc62, @JsonProperty("PTOT54") Integer ptot54, @JsonProperty("PTOT36") Integer ptot36, @JsonProperty(
            "PTOT1931") Integer ptot1931, @JsonProperty("PTOT1926") Integer ptot1926, @JsonProperty("PTOT1921") Integer ptot1921, @JsonProperty(
            "PTOT1911") Integer ptot1911, @JsonProperty("PTOT1906") Integer ptot1906, @JsonProperty("PTOT1901") Integer ptot1901, @JsonProperty(
            "PTOT1896") Integer ptot1896, @JsonProperty("PTOT1891") Integer ptot1891, @JsonProperty("PTOT1886") Integer ptot1886, @JsonProperty(
            "PTOT1881") Integer ptot1881, @JsonProperty("PTOT1876") Integer ptot1876) {
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.region = StringUtils.normalizeSpace(StringUtils.trimToNull(region));
        this.departement = StringUtils.normalizeSpace(StringUtils.trimToNull(departement));
        this.pmun20 = pmun20;
        this.pmun19 = pmun19;
        this.pmun18 = pmun18;
        this.pmun17 = pmun17;
        this.pmun16 = pmun16;
        this.pmun15 = pmun15;
        this.pmun14 = pmun14;
        this.pmun13 = pmun13;
        this.pmun12 = pmun12;
        this.pmun11 = pmun11;
        this.pmun10 = pmun10;
        this.pmun09 = pmun09;
        this.pmun08 = pmun08;
        this.pmun07 = pmun07;
        this.pmun06 = pmun06;
        this.psdc99 = psdc99;
        this.psdc90 = psdc90;
        this.psdc82 = psdc82;
        this.psdc75 = psdc75;
        this.psdc68 = psdc68;
        this.psdc62 = psdc62;
        this.ptot54 = ptot54;
        this.ptot36 = ptot36;
        this.ptot1931 = ptot1931;
        this.ptot1926 = ptot1926;
        this.ptot1921 = ptot1921;
        this.ptot1911 = ptot1911;
        this.ptot1906 = ptot1906;
        this.ptot1901 = ptot1901;
        this.ptot1896 = ptot1896;
        this.ptot1891 = ptot1891;
        this.ptot1886 = ptot1886;
        this.ptot1881 = ptot1881;
        this.ptot1876 = ptot1876;
    }
}
