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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;

@Service
@Order(0)
public class ZipCodeGenerationService extends GenerationService {

    private final Path tmp = Constants.SOURCES_FOLDER.resolve("zipCodes.csv");

    private final WebClient webClient;

    private final ObjectReader reader;

    public ZipCodeGenerationService(WebClient.Builder builder, CsvMapper csvMapper) {
        super("La Poste", "https://www.lapostegroupe.com");
        this.webClient = builder.baseUrl("https://datanova.laposte.fr/api/explore/v2.1/catalog/datasets/laposte_hexasmal/exports/csv").build();
        this.reader = csvMapper.readerFor(ZipCodeLine.class)
                               .with(csvMapper.schemaFor(ZipCodeLine.class).withHeader().withColumnReordering(true).withColumnSeparator(';'));
    }

    @Override
    public void addData(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes())) {
/*            Flux<DataBuffer> dataBufferFlux = this.webClient.get()
                                                            .retrieve()
                                                            .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
                                                                      response -> Constants.handleError(response, getClass().getCanonicalName(), this.logger))
                                                            .bodyToFlux(DataBuffer.class);

            DataBufferUtils.write(dataBufferFlux, this.tmp, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE).block();*/

            try {
                MappingIterator<ZipCodeLine> it = this.reader.readValues(new BOMInputStream(new FileInputStream(this.tmp.toFile())));

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
