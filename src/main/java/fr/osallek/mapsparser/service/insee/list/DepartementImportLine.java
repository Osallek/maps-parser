package fr.osallek.mapsparser.service.insee.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepartementImportLine(@JsonProperty("CHEFLIEU") String chefLieu, @JsonProperty("DEP") String id, @JsonProperty("REG") String region,
                                    @JsonProperty("LIBELLE") String name) {

    public DepartementImportLine(@JsonProperty("CHEFLIEU") String chefLieu, @JsonProperty("DEP") String id, @JsonProperty("REG") String region,
                                 @JsonProperty("LIBELLE") String name) {
        this.chefLieu = StringUtils.normalizeSpace(StringUtils.trimToNull(chefLieu));
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.region = StringUtils.normalizeSpace(StringUtils.trimToNull(region));
        this.name = StringUtils.capitalize(StringUtils.normalizeSpace(StringUtils.trimToNull(name)));
    }
}
