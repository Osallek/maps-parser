package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZipCodeLine(String zipCode, String id, List<Double> point) {

    @JsonCreator
    public ZipCodeLine(@JsonProperty("code_commune_insee") String id, @JsonProperty("code_postal") String zipCode,
                       @JsonProperty("coordonnees_geographiques") String point) {
        this(StringUtils.trimToNull(zipCode), StringUtils.trimToNull(id),
             Arrays.stream(StringUtils.split(StringUtils.deleteWhitespace(point), ',')).map(Double::parseDouble).toList());
    }
}
