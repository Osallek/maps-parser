package fr.osallek.mapsparser.service.gouv.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Point;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

//@Service
@Order(5)
public class PointGenerationService extends GenerationService {

    private static final Path ALL = Constants.SOURCES_FOLDER.resolve("point").resolve("points.json.gz");

    private static final Path PARIS = Constants.SOURCES_FOLDER.resolve("point").resolve("paris.json.gz");

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public PointGenerationService(WebClient.Builder builder, Environment environment, ObjectMapper objectMapper) {
        super("Api Gouv", "https://api.gouv.fr", environment);
        this.webClient = builder.baseUrl("https://etablissements-publics.api.gouv.fr/v3/organismes/").build();
        this.objectMapper = objectMapper;
    }

    @Override
    public void addData(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            downloadToFile(() -> this.webClient.get()
                                               .uri(b -> b.path("mairie").build())
                                               .retrieve()
                                               .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
                                                         r -> Constants.handleError(r, getClass().getSimpleName(), this.logger))
                                               .bodyToFlux(DataBuffer.class), ALL).block();
            downloadToFile(() -> this.webClient.get()
                                               .uri(b -> b.path("paris_mairie").build())
                                               .retrieve()
                                               .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
                                                         r -> Constants.handleError(r, getClass().getSimpleName(), this.logger))
                                               .bodyToFlux(DataBuffer.class), PARIS).block();

            readFile(data, ALL);
            readFile(data, PARIS);
        }
    }

    private void readFile(Data data, Path path) {
        try (InputStream fileInputStream = Files.newInputStream(path); GZIPInputStream gzip = new GZIPInputStream(fileInputStream)) {
            PointResponse response = this.objectMapper.readValue(gzip, PointResponse.class);

            if (CollectionUtils.isNotEmpty(response.getFeatures())) {
                for (List<PointFeature> features : response.getFeatures()) {
                    if (CollectionUtils.isNotEmpty(features)) {
                        for (PointFeature feature : features) {
                            if (StringUtils.isNotBlank(feature.getProperties().getId()) &&
                                CollectionUtils.size(feature.getGeometry().getCoordinates()) == 2 &&
                                data.getCommunes().getCommunes().containsKey(feature.getProperties().getId())) {
                                data.getCommunes()
                                    .getCommunes()
                                    .get(feature.getProperties().getId())
                                    .setPoint(new Point(feature.getGeometry().getCoordinates().get(1), feature.getGeometry().getCoordinates().get(0)));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            this.logger.error(e.getMessage(), e);
        }
    }
}
