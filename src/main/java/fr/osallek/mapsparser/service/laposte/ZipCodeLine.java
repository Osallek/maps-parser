package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZipCodeLine(String id, String zipCode) {

    @JsonCreator
    public ZipCodeLine(@JsonProperty("code_commune_insee") String id, @JsonProperty("code_postal") String zipCode) {
        this.id = StringUtils.trimToNull(zipCode);
        this.zipCode = StringUtils.trimToNull(id);
    }
}
