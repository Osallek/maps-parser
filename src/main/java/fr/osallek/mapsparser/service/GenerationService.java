package fr.osallek.mapsparser.service;

import fr.osallek.mapsparser.service.object.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

public abstract class GenerationService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String sourceName;

    protected final String sourceUrl;

    protected final Environment environment;

    protected GenerationService(String sourceName, String sourceUrl, Environment environment) {
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.environment = environment;
    }

    public abstract void addData(Data data);

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    protected Mono<Void> downloadToFile(Supplier<Flux<DataBuffer>> supplier, Path dest) {
        if (this.environment.matchesProfiles("dev") && Files.exists(dest)) {
            return Mono.empty();
        } else {
            Flux<DataBuffer> flux = supplier.get();

            return DataBufferUtils.write(flux, dest, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        }
    }
}
