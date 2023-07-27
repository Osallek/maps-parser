package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
