package fr.osallek.mapsparser.service.gouv.point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PointResponse {

    @JsonProperty("features")
    private List<List<PointFeature>> features;

    public List<List<PointFeature>> getFeatures() {
        return features;
    }

    public void setFeatures(List<List<PointFeature>> features) {
        this.features = features;
    }
}
