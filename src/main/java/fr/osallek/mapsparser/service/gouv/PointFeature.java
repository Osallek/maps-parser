package fr.osallek.mapsparser.service.gouv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PointFeature {

    @JsonProperty("geometry")
    private PointGeometry geometry;

    @JsonProperty("properties")
    private PointProperties properties;

    public PointGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PointGeometry geometry) {
        this.geometry = geometry;
    }

    public PointProperties getProperties() {
        return properties;
    }

    public void setProperties(PointProperties properties) {
        this.properties = properties;
    }
}
