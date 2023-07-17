package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataNodes {

    protected final Level level;

    protected DataJenks jenks;

    public DataNodes(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public DataJenks getJenks() {
        if (this.jenks == null) {
            this.jenks = new DataJenks();
        }

        return jenks;
    }

    public void setJenks(DataJenks jenks) {
        this.jenks = jenks;
    }
}
