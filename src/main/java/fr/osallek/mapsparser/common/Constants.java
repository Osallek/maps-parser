package fr.osallek.mapsparser.common;

import org.slf4j.Logger;
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

    public static final Path DATA_FILE = Path.of("data.json.br");

    public static final Path DATA_PATH = DATA_FOLDER.resolve(DATA_FILE);

    public static final Path SOURCES_FOLDER = Path.of("sources");

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
