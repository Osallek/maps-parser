package fr.osallek.mapsparser.service.insee.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommuneImportLine(@JsonProperty("TYPECOM") String type, @JsonProperty("COM") String id, @JsonProperty("DEP") String departement,
                                @JsonProperty("LIBELLE") String name) {

    public CommuneImportLine(@JsonProperty("TYPECOM") String type, @JsonProperty("COM") String id, @JsonProperty("DEP") String departement,
                             @JsonProperty("LIBELLE") String name) {
        this.type = StringUtils.normalizeSpace(StringUtils.trimToNull(type));
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
        this.departement = StringUtils.normalizeSpace(StringUtils.trimToNull(departement));
        this.name = StringUtils.capitalize(StringUtils.normalizeSpace(StringUtils.trimToNull(name)));
    }
}
