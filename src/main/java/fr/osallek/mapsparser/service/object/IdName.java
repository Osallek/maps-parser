package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdName<I, N> extends Hashed {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof IdName<?, ?> idName)) {
            return false;
        }

        return new EqualsBuilder().append(id, idName.id).append(name, idName.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(name).toHashCode();
    }
}
