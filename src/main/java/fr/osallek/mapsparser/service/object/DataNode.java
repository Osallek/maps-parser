package fr.osallek.mapsparser.service.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataNode extends IdName<String, String> {

    protected final Level level;

    public DataNode(String id, String name, Level level) {
        super(id, name);
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
