package fr.osallek.mapsparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.MyGZIPOutputStream;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataNode;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
import fr.osallek.mapsparser.service.object.mixin.common.CommonCommuneMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonDepartementMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonRegionMixin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class MapsParserService implements ApplicationListener<ApplicationReadyEvent>, ExitCodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapsParserService.class);

    private final Environment environment;

    private final List<GenerationService> generationServices;

    private final ObjectMapper objectMapper;

    private final ObjectMapper emptyObjectMapper;

    private Exception e;

    private Data data;

    public MapsParserService(Environment environment, List<GenerationService> generationServices, ObjectMapper objectMapper,
                             @Qualifier("emptyObjectMapper") ObjectMapper emptyObjectMapper) {
        this.environment = environment;
        this.generationServices = generationServices;
        this.objectMapper = objectMapper;
        this.emptyObjectMapper = emptyObjectMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            LOGGER.info("Starting generation");

            readData();

            for (GenerationService generationService : this.generationServices) {
                LOGGER.info("Doing {}", generationService.getClass().getSimpleName());
                generationService.addData(this.data);
                LOGGER.info("Done {}", generationService.getClass().getSimpleName());
            }

            writeData();

            LOGGER.info("Done generation");
        } catch (Exception e) {
            LOGGER.error("An error occurred while running: {}", e.getMessage(), e);
            this.e = e;
        } finally {
            Constants.EXECUTOR.shutdown();
        }
    }

    @Override
    public int getExitCode() {
        return this.e == null ? 0 : 1;
    }

    private void readData() throws IOException {
        if (Files.exists(Constants.DATA_PATH)) {
            LOGGER.info("Reading previous data");
            try (InputStream inputStream = Files.newInputStream(Constants.DATA_PATH); BufferedInputStream buffered = new BufferedInputStream(
                    inputStream); GZIPInputStream gzip = new GZIPInputStream(buffered)) {
                this.data = this.objectMapper.readValue(gzip, Data.class);
            }
            LOGGER.info("Read previous data");
        } else if (Files.exists(
                Constants.DATA_PATH.resolveSibling(FilenameUtils.removeExtension(Constants.DATA_FILE.getFileName().getFileName().toString())))) {
            LOGGER.info("Reading previous data");
            try (InputStream inputStream = Files.newInputStream(Constants.DATA_PATH); BufferedInputStream buffered = new BufferedInputStream(inputStream)) {
                this.data = this.objectMapper.readValue(buffered, Data.class);
            }
            LOGGER.info("Read previous data");
        } else {
            this.data = new Data();
            LOGGER.info("No previous data");
        }

        FileUtils.forceMkdir(Constants.SOURCES_FOLDER.toFile());
    }

    private void writeData() throws IOException {
        LOGGER.info("Writing data");
        FileUtils.forceMkdir(Constants.DATA_FOLDER.toFile());

/*        Path tmp = Constants.DATA_FOLDER.resolve(Constants.DATA_FILE.getFileName() + ".tmp");
        try (OutputStream outputStream = Files.newOutputStream(tmp); BufferedOutputStream buffered = new BufferedOutputStream(
                outputStream); GZIPOutputStream gzip = new MyGZIPOutputStream(buffered)) {
            this.objectMapper.writeValue(gzip, this.data);
            Files.copy(tmp, Constants.DATA_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            FileUtils.deleteQuietly(tmp.toFile());
        }*/

        Path commonTmp = Constants.DATA_FOLDER.resolve(Constants.COMMON_FILE.getFileName() + ".tmp");
        try (OutputStream outputStream = Files.newOutputStream(commonTmp);
             BufferedOutputStream buffered = new BufferedOutputStream(outputStream); GZIPOutputStream gzip = new MyGZIPOutputStream(buffered)) {
            Map<Class<?>, Class<?>> mixins = new HashMap<>();
            mixins.put(DataNode.class, CommonDataNodeMixin.class);
            mixins.put(Commune.class, CommonCommuneMixin.class);
            mixins.put(Departement.class, CommonDepartementMixin.class);
            mixins.put(Region.class, CommonRegionMixin.class);

            this.emptyObjectMapper.setMixIns(mixins);
            this.emptyObjectMapper.writeValue(gzip, this.data);
            Files.copy(commonTmp, Constants.COMMON_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            FileUtils.deleteQuietly(commonTmp.toFile());
        }

        if (this.environment.matchesProfiles("dev")) {
            this.objectMapper.writerWithDefaultPrettyPrinter()
                             .writeValue(Constants.DATA_PATH.resolveSibling(
                                     FilenameUtils.removeExtension(Constants.DATA_FILE.getFileName().getFileName().toString())).toFile(), this.data);
        }

        LOGGER.info("Written data");
    }
}
