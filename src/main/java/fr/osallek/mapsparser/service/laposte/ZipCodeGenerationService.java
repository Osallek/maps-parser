package fr.osallek.mapsparser.service.laposte;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.MyGZIPOutputStream;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
@Order(0)
public class ZipCodeGenerationService extends GenerationService {

    private static final Path PATH = Constants.SOURCES_FOLDER.resolve("zipCodes").resolve("zipCodes.csv.gz");

    private final ObjectReader reader;

    private final WebClient webClient;

    public ZipCodeGenerationService(WebClient.Builder builder, CsvMapper csvMapper, Environment environment) {
        super("La Poste", "https://www.lapostegroupe.com", environment);
        this.reader = csvMapper.readerFor(ZipCodeLine.class)
                               .with(csvMapper.schemaFor(ZipCodeLine.class).withHeader().withColumnReordering(true).withColumnSeparator(';'));
        this.webClient = builder.baseUrl("https://datanova.laposte.fr/data-fair/api/v1/datasets/laposte-hexasmal/raw").build();
    }

    @Override
    public void addData(Data data) {
        if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
            boolean downloaded = BooleanUtils.toBoolean(downloadToFile(() -> this.webClient.get()
                                                                                           .retrieve()
                                                                                           .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
                                                                                                     r -> Constants.handleError(r, getClass().getSimpleName(),
                                                                                                                                this.logger))
                                                                                           .bodyToFlux(DataBuffer.class), PATH).block());
            if (downloaded) {
                Path tmp = PATH.getParent().resolve("tmp");

                try (InputStream fileInputStream = Files.newInputStream(PATH);
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     OutputStream outputStream = Files.newOutputStream(tmp)) {
                    IOUtils.copy(gzipInputStream, outputStream);
                } catch (IOException e) {
                    this.logger.error(e.getMessage(), e);
                }

                try (InputStream fileInputStream = Files.newInputStream(tmp);
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                     OutputStream outputStream = Files.newOutputStream(PATH);
                     GZIPOutputStream gzip = new MyGZIPOutputStream(outputStream);
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gzip, StandardCharsets.UTF_8))) {
                    writer.append(bufferedReader.readLine().substring(1));
                    writer.newLine();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        writer.append(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    this.logger.error(e.getMessage(), e);
                }

                FileUtils.deleteQuietly(tmp.toFile());
            }

            try (InputStream fileInputStream = Files.newInputStream(PATH);
                 GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 MappingIterator<ZipCodeLine> it = this.reader.readValues(gzipInputStream)) {
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
