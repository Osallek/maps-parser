package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataNode extends IdName<String, String> {

    protected final Level level;

    protected BigDecimal area;

    public DataNode(String id, String name, Level level) {
        super(id, name);
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }
}
