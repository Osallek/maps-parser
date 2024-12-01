package fr.osallek.mapsparser.service;

import fr.osallek.mapsparser.common.MyGZIPOutputStream;
import fr.osallek.mapsparser.service.object.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;

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

    protected Mono<Boolean> downloadToFile(Supplier<Flux<DataBuffer>> supplier, Path dest) {
        if (this.environment.matchesProfiles("dev") && Files.exists(dest)) {
            return Mono.just(false);
        } else {
            return Mono.create(sink -> {
                try {
                    OutputStream outputStream = Files.newOutputStream(dest);
                    BufferedOutputStream buffered = new BufferedOutputStream(outputStream);
                    GZIPOutputStream gzip = new MyGZIPOutputStream(buffered);
                    sink.onDispose(() -> {
                        try {
                            gzip.close();
                        } catch (IOException ignored) {
                        }
                    });
                    DataBufferUtils.write(supplier.get(), gzip).subscribe(DataBufferUtils::release, sink::error, sink::success, Context.of(sink.contextView()));
                } catch (IOException e) {
                    sink.error(e);
                }
            }).then(Mono.just(true));
        }
    }
}
