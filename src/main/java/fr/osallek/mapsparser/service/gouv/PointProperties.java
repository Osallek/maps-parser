package fr.osallek.mapsparser.service.gouv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PointProperties {

    @JsonProperty("codeInsee")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringUtils.normalizeSpace(StringUtils.trimToNull(id));
    }
}
