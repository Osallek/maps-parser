package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DataNodes extends Hashed {

    protected final Level level;

    protected DataNodes(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DataNodes dataNodes)) {
            return false;
        }

        return new EqualsBuilder().append(level, dataNodes.level).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(level).toHashCode();
    }
}
