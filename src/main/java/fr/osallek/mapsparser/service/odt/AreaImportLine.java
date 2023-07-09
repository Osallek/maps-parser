package fr.osallek.mapsparser.service.odt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AreaImportLine(@JsonProperty("codgeo") String id, @JsonProperty("superf_choro") BigDecimal area) {

    @JsonCreator
    public AreaImportLine(@JsonProperty("codgeo") String id, @JsonProperty("superf_choro") Double area) {
        this(StringUtils.normalizeSpace(StringUtils.trimToNull(id)), area == null ? null : BigDecimal.valueOf(area));
    }
}
