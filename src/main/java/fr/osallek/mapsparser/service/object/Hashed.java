package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Hashed {

    @JsonIgnore
    protected int initHash;

    public void initHash() {
        this.initHash = hashCode();
    }

    @JsonIgnore
    public int getInitHash() {
        return initHash;
    }
}
