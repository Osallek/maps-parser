package fr.osallek.mapsparser.service.insee.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegionImportLine(@JsonProperty("CHEFLIEU") String chefLieu, @JsonProperty("REG") String id, @JsonProperty("LIBELLE") String name) {

    public RegionImportLine(@JsonProperty("CHEFLIEU") String chefLieu, @JsonProperty("REG") String id, @JsonProperty("LIBELLE") String name) {
        this.chefLieu = StringUtils.normalizeSpace(StringUtils.trimToNull(chefLieu));
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.name = StringUtils.capitalize(StringUtils.normalizeSpace(StringUtils.trimToNull(name)));
    }
}
