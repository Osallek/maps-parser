package fr.osallek.mapsparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.MyGZIPOutputStream;
import fr.osallek.mapsparser.service.object.Common;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class MapsParserService implements ApplicationListener<ApplicationReadyEvent>, ExitCodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapsParserService.class);

    private final List<GenerationService> generationServices;

    private final ObjectMapper objectMapper;

    private final Environment environment;

    private Exception e;

    private Data data;

    public MapsParserService(List<GenerationService> generationServices, ObjectMapper objectMapper, Environment environment) {
        this.generationServices = generationServices;
        this.objectMapper = objectMapper;
        this.environment = environment;
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
        this.data.initHash();
    }

    private void writeData() throws IOException {
        LOGGER.info("Writing data");

        FileUtils.forceMkdir(Constants.DATA_FOLDER.toFile());

        if (this.data.getInitHash() != this.data.hashCode()) {
            Path tmp = Constants.DATA_FOLDER.resolve(Constants.DATA_FILE.getFileName() + ".tmp");
            try (OutputStream outputStream = Files.newOutputStream(tmp); BufferedOutputStream buffered = new BufferedOutputStream(outputStream);
                 GZIPOutputStream gzip = new MyGZIPOutputStream(buffered)) {
                this.objectMapper.writeValue(gzip, this.data);
                Files.copy(tmp, Constants.DATA_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                FileUtils.deleteQuietly(tmp.toFile());
            }
        }

        List<CompletableFuture<?>> futures = new ArrayList<>();

        FileUtils.forceMkdir(Constants.REGIONS_FOLDER.toFile());
        for (Region region : this.data.getRegions().getRegions().values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    writePart(Constants.REGIONS_FOLDER.resolve(region.getId() + ".json.gz"), region);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }, Constants.EXECUTOR));
        }

        FileUtils.forceMkdir(Constants.DEPARTEMENTS_FOLDER.toFile());
        for (Departement departement : this.data.getDepartements().getDepartements().values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    writePart(Constants.DEPARTEMENTS_FOLDER.resolve(departement.getId() + ".json.gz"), departement);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }, Constants.EXECUTOR));
        }

        FileUtils.forceMkdir(Constants.COMMUNES_FOLDER.toFile());
        for (Commune commune : this.data.getCommunes().getCommunes().values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    writePart(Constants.COMMUNES_FOLDER.resolve(commune.getId() + ".json.gz"), commune);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }, Constants.EXECUTOR));
        }

        FileUtils.forceMkdir(Constants.JENKS_FOLDER.toFile());
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("population.json.gz"), this.data.getJenks().getPopulation());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("area.json.gz"), this.data.getJenks().getArea());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("density.json.gz"), this.data.getJenks().getDensity());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("birth.json.gz"), this.data.getJenks().getBirth());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("death.json.gz"), this.data.getJenks().getDeath());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("birth-per-capita.json.gz"), this.data.getJenks().getBirthPerCapita());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.JENKS_FOLDER.resolve("death-per-capita.json.gz"), this.data.getJenks().getDeathPerCapita());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                writePart(Constants.COMMON_PATH, new Common(this.data));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        LOGGER.info("Written data");
    }

    private void writePart(Path path, Object o) throws IOException {
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try (OutputStream outputStream = Files.newOutputStream(tmp); BufferedOutputStream buffered = new BufferedOutputStream(outputStream);
             GZIPOutputStream gzip = new MyGZIPOutputStream(buffered)) {
            if (this.environment.matchesProfiles("dev")) {
                this.objectMapper.writerWithDefaultPrettyPrinter().writeValue(gzip, o);
            } else {
                this.objectMapper.writeValue(gzip, o);
            }

            Files.copy(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            FileUtils.deleteQuietly(tmp.toFile());
        }
    }
}
