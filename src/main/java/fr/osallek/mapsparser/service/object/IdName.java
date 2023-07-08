package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdName<I, N> {

    protected I id;

    protected N name;

    public IdName() {
    }

    public IdName(I id) {
        this.id = id;
    }

    public IdName(I id, N name) {
        this.id = id;
        this.name = name;
    }

    public I getId() {
        return id;
    }

    public void setId(I id) {
        this.id = id;
    }

    public N getName() {
        return name;
    }

    public void setName(N name) {
        this.name = name;
    }
}
