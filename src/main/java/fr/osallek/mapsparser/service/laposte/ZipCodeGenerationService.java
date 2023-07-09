package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Point;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

@Service
@Order(0)
public class ZipCodeGenerationService extends GenerationService {

    private final Path tmp = Constants.SOURCES_FOLDER.resolve("zipCodes.csv");

    private final WebClient webClient;

    private final ObjectReader reader;

    public ZipCodeGenerationService(WebClient.Builder builder, CsvMapper csvMapper, Environment environment) {
        super("La Poste", "https://www.lapostegroupe.com", environment);
        this.webClient = builder.baseUrl("https://datanova.laposte.fr/api/explore/v2.1/catalog/datasets/laposte_hexasmal/exports/csv").build();
        this.reader = csvMapper.readerFor(ZipCodeLine.class)
                               .with(csvMapper.schemaFor(ZipCodeLine.class).withHeader().withColumnReordering(true).withColumnSeparator(';'));
    }

    @Override
    public void addData(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes())) {
            downloadToFile(() -> this.webClient.get()
                                               .retrieve()
                                               .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
                                                         r -> Constants.handleError(r, getClass().getSimpleName(), this.logger))
                                               .bodyToFlux(DataBuffer.class), this.tmp).block();

            try {
                MappingIterator<ZipCodeLine> it = this.reader.readValues(BOMInputStream.builder().setInputStream(new FileInputStream(this.tmp.toFile())).get());

                while (it.hasNext()) {
                    ZipCodeLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        Commune commune = data.getCommunes().get(current.id());

                        if (commune != null) {
                            if (StringUtils.isNotBlank(current.zipCode())) {
                                commune.setZipCode(current.zipCode());
                            }

                            if (CollectionUtils.isNotEmpty(current.point())) {
                                commune.setPoint(new Point(current.point()));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
    }
}
