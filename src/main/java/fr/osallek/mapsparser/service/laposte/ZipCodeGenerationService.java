package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Order(0)
public class ZipCodeGenerationService extends GenerationService {

    private final ObjectReader reader;

    public ZipCodeGenerationService(WebClient.Builder builder, CsvMapper csvMapper, Environment environment) {
        super("La Poste", "https://www.lapostegroupe.com", environment);
        this.reader = csvMapper.readerFor(ZipCodeLine.class)
                               .with(csvMapper.schemaFor(ZipCodeLine.class).withHeader().withColumnReordering(true).withColumnSeparator(';'));
    }

    @Override
    public void addData(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            try (InputStream fileInputStream = Files.newInputStream(Constants.SOURCES_FOLDER.resolve("zipCodes").resolve("zipCodes.csv.gz"));
                 GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(gzipInputStream).get();
                 MappingIterator<ZipCodeLine> it = this.reader.readValues(bomInputStream)) {
                while (it.hasNext()) {
                    ZipCodeLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        Commune commune = data.getCommunes().getCommunes().get(current.id());

                        if (commune != null) {
                            if (StringUtils.isNotBlank(current.zipCode())) {
                                commune.setZipCode(current.zipCode());
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
