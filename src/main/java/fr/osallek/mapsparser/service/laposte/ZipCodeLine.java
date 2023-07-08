package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZipCodeFields(@JsonProperty("code_postal") String zipCode, @JsonProperty("code_commune_insee") String id,
                            @JsonProperty("coordonnees_geographiques") List<Double> point) {

    public ZipCodeFields(@JsonProperty("code_postal") String zipCode, @JsonProperty("code_commune_insee") String id,
                         @JsonProperty("coordonnees_geographiques") List<Double> point) {
        this.zipCode = StringUtils.deleteWhitespace(StringUtils.trimToNull(zipCode));
        this.id = StringUtils.deleteWhitespace(StringUtils.trimToNull(id));
        this.point = point;
    }
}
