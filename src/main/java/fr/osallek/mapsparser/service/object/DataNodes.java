package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DataNodes extends Hashed {

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

    @Override
    public void initHash() {
        this.jenks.initHash();
        super.initHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DataNodes dataNodes)) {
            return false;
        }

        return new EqualsBuilder().append(level, dataNodes.level).append(jenks, dataNodes.jenks).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(level).append(jenks).toHashCode();
    }
}
