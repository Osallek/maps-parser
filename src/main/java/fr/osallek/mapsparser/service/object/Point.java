package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class Point {

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Point(List<Double> list) {
        this(list.get(0), list.get(1));
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Point point)) {
            return false;
        }

        return new EqualsBuilder().append(lat, point.lat).append(lon, point.lon).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(lat).append(lon).toHashCode();
    }
}
