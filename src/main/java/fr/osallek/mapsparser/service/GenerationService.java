package fr.osallek.mapsparser.service;

import fr.osallek.mapsparser.service.object.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenerationService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String sourceName;

    protected final String sourceUrl;

    protected GenerationService(String sourceName, String sourceUrl) {
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
    }

    public abstract void addData(Data data);

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }
}
