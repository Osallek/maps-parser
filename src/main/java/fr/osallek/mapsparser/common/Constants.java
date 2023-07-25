package fr.osallek.mapsparser.common;

import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class Constants {

    private Constants() {
    }

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final Integer PROGRESS_BAR_LENGTH = 120;

    public static final Path DATA_FOLDER = Path.of("data");

    public static final Path DATA_FILE = Path.of("data.json.gz");

    public static final Path DATA_PATH = DATA_FOLDER.resolve(DATA_FILE);

    public static final Path COMMON_FILE = Path.of("common.json.gz");

    public static final Path COMMON_PATH = DATA_FOLDER.resolve(COMMON_FILE);

    public static final Path AREA_FILE = Path.of("area.json.gz");

    public static final Path AREA_PATH = DATA_FOLDER.resolve(AREA_FILE);

    public static final Path POP_FILE = Path.of("pop.json.gz");

    public static final Path POP_PATH = DATA_FOLDER.resolve(POP_FILE);

    public static final Path DENSITY_FILE = Path.of("density.json.gz");

    public static final Path DENSITY_PATH = DATA_FOLDER.resolve(DENSITY_FILE);

    public static final Path BIRTH_FILE = Path.of("birth.json.gz");

    public static final Path BIRTH_PATH = DATA_FOLDER.resolve(BIRTH_FILE);

    public static final Path DEATH_FILE = Path.of("death.json.gz");

    public static final Path DEATH_PATH = DATA_FOLDER.resolve(DEATH_FILE);

    public static final Path BIRTH_PER_CAPITA_FILE = Path.of("birth-per-capita.json.gz");

    public static final Path BIRTH_PER_CAPITA_PATH = DATA_FOLDER.resolve(BIRTH_PER_CAPITA_FILE);

    public static final Path DEATH_PER_CAPITA_FILE = Path.of("death-per-capita.json.gz");

    public static final Path DEATH_PER_CAPITA_PATH = DATA_FOLDER.resolve(DEATH_PER_CAPITA_FILE);

    public static final Path SOURCES_FOLDER = Path.of("sources");

    public static final ThreadPoolTaskExecutor EXECUTOR;

    static {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("parser-");
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setKeepAliveSeconds(0);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.initialize();

        EXECUTOR = executor;
    }

    public static Mono<Throwable> handleError(ClientResponse response, String name, Logger logger) {
        return response.bodyToMono(String.class).map(error -> {
            logger.error("An error occurred with {}: {}", name, error);
            return new RuntimeException();
        });
    }

    public static Mono<ClientResponse> log(ClientRequest request, ExchangeFunction next, Logger logger) {
        Instant start = Instant.now();

        if (logger.isInfoEnabled()) {
            logger.info("Doing request: {} {}", request.method(), request.url());
        }

        return next.exchange(request).doOnNext((ClientResponse response) -> {
            if (logger.isInfoEnabled()) {
                logger.info("Got response {} for {} {} in {}ms", response.statusCode().value(), request.method(), request.url(),
                            Duration.between(start, Instant.now()).toMillis());
            }
        });
    }
}
