package fr.osallek.mapsparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.common.MyGZIPOutputStream;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.CommunePopulation;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.DataJenks;
import fr.osallek.mapsparser.service.object.DataNode;
import fr.osallek.mapsparser.service.object.DataNodes;
import fr.osallek.mapsparser.service.object.DataPopulation;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.DepartementPopulation;
import fr.osallek.mapsparser.service.object.IdName;
import fr.osallek.mapsparser.service.object.Region;
import fr.osallek.mapsparser.service.object.mixin.area.AreaCommuneMixin;
import fr.osallek.mapsparser.service.object.mixin.area.AreaDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.area.AreaDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.area.AreaDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.area.AreaDepartementMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.birth.BirthDepartementPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.birthpercapita.BirthPerCapitaDepartementPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonCommuneMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonDepartementMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonIdNameMixin;
import fr.osallek.mapsparser.service.object.mixin.common.CommonRegionMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.death.DeathDepartementPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.deathpercapita.DeathPerCapitaDepartementPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.density.DensityDepartementPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopCommunePopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopDataJenksMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopDataNodeMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopDataNodesMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopDataPopulationMixin;
import fr.osallek.mapsparser.service.object.mixin.pop.PopDepartementPopulationMixin;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
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

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> commonMixins = new HashMap<>();
                commonMixins.put(IdName.class, CommonIdNameMixin.class);
                commonMixins.put(DataNode.class, CommonDataNodeMixin.class);
                commonMixins.put(Commune.class, CommonCommuneMixin.class);
                commonMixins.put(Departement.class, CommonDepartementMixin.class);
                commonMixins.put(Region.class, CommonRegionMixin.class);
                writePart(Constants.COMMON_PATH, commonMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> areaMixins = new HashMap<>();
                areaMixins.put(DataNodes.class, AreaDataNodesMixin.class);
                areaMixins.put(DataNode.class, AreaDataNodeMixin.class);
                areaMixins.put(DataJenks.class, AreaDataJenksMixin.class);
                areaMixins.put(Commune.class, AreaCommuneMixin.class);
                areaMixins.put(Departement.class, AreaDepartementMixin.class);
                writePart(Constants.AREA_PATH, areaMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> popMixins = new HashMap<>();
                popMixins.put(DataNodes.class, PopDataNodesMixin.class);
                popMixins.put(DataNode.class, PopDataNodeMixin.class);
                popMixins.put(DataJenks.class, PopDataJenksMixin.class);
                popMixins.put(DataPopulation.class, PopDataPopulationMixin.class);
                popMixins.put(DepartementPopulation.class, PopDepartementPopulationMixin.class);
                popMixins.put(CommunePopulation.class, PopCommunePopulationMixin.class);
                writePart(Constants.POP_PATH, popMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> densityMixins = new HashMap<>();
                densityMixins.put(DataNodes.class, DensityDataNodesMixin.class);
                densityMixins.put(DataNode.class, DensityDataNodeMixin.class);
                densityMixins.put(DataJenks.class, DensityDataJenksMixin.class);
                densityMixins.put(DataPopulation.class, DensityDataPopulationMixin.class);
                densityMixins.put(DepartementPopulation.class, DensityDepartementPopulationMixin.class);
                densityMixins.put(CommunePopulation.class, DensityCommunePopulationMixin.class);
                writePart(Constants.DENSITY_PATH, densityMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> birthMixins = new HashMap<>();
                birthMixins.put(DataNodes.class, BirthDataNodesMixin.class);
                birthMixins.put(DataNode.class, BirthDataNodeMixin.class);
                birthMixins.put(DataJenks.class, BirthDataJenksMixin.class);
                birthMixins.put(DataPopulation.class, BirthDataPopulationMixin.class);
                birthMixins.put(DepartementPopulation.class, BirthDepartementPopulationMixin.class);
                birthMixins.put(CommunePopulation.class, BirthCommunePopulationMixin.class);
                writePart(Constants.BIRTH_PATH, birthMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> deathMixins = new HashMap<>();
                deathMixins.put(DataNodes.class, DeathDataNodesMixin.class);
                deathMixins.put(DataNode.class, DeathDataNodeMixin.class);
                deathMixins.put(DataJenks.class, DeathDataJenksMixin.class);
                deathMixins.put(DataPopulation.class, DeathDataPopulationMixin.class);
                deathMixins.put(DepartementPopulation.class, DeathDepartementPopulationMixin.class);
                deathMixins.put(CommunePopulation.class, DeathCommunePopulationMixin.class);
                writePart(Constants.DEATH_PATH, deathMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> birthPerCapitaMixins = new HashMap<>();
                birthPerCapitaMixins.put(DataNodes.class, BirthPerCapitaDataNodesMixin.class);
                birthPerCapitaMixins.put(DataNode.class, BirthPerCapitaDataNodeMixin.class);
                birthPerCapitaMixins.put(DataJenks.class, BirthPerCapitaDataJenksMixin.class);
                birthPerCapitaMixins.put(DataPopulation.class, BirthPerCapitaDataPopulationMixin.class);
                birthPerCapitaMixins.put(DepartementPopulation.class, BirthPerCapitaDepartementPopulationMixin.class);
                birthPerCapitaMixins.put(CommunePopulation.class, BirthPerCapitaCommunePopulationMixin.class);
                writePart(Constants.BIRTH_PER_CAPITA_PATH, birthPerCapitaMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        futures.add(CompletableFuture.runAsync(() -> {
            try {
                Map<Class<?>, Class<?>> deathPerCapitaMixins = new HashMap<>();
                deathPerCapitaMixins.put(DataNodes.class, DeathPerCapitaDataNodesMixin.class);
                deathPerCapitaMixins.put(DataNode.class, DeathPerCapitaDataNodeMixin.class);
                deathPerCapitaMixins.put(DataJenks.class, DeathPerCapitaDataJenksMixin.class);
                deathPerCapitaMixins.put(DataPopulation.class, DeathPerCapitaDataPopulationMixin.class);
                deathPerCapitaMixins.put(DepartementPopulation.class, DeathPerCapitaDepartementPopulationMixin.class);
                deathPerCapitaMixins.put(CommunePopulation.class, DeathPerCapitaCommunePopulationMixin.class);
                writePart(Constants.DEATH_PER_CAPITA_PATH, deathPerCapitaMixins);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }, Constants.EXECUTOR));

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        LOGGER.info("Written data");
    }

    private void writePart(Path path, Map<Class<?>, Class<?>> mixins) throws IOException {
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try (OutputStream outputStream = Files.newOutputStream(tmp); BufferedOutputStream buffered = new BufferedOutputStream(
                outputStream); GZIPOutputStream gzip = new MyGZIPOutputStream(buffered)) {
            this.emptyObjectMapper.copy().setMixIns(mixins).writeValue(gzip, this.data);
            Files.copy(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            FileUtils.deleteQuietly(tmp.toFile());
        }

        if (this.environment.matchesProfiles("dev")) {
            tmp = path.resolveSibling(FilenameUtils.removeExtension(path.getFileName().toString()));
            try (OutputStream outputStream = Files.newOutputStream(tmp); BufferedOutputStream buffered = new BufferedOutputStream(outputStream);) {
                this.emptyObjectMapper.copy().setMixIns(mixins).writerWithDefaultPrettyPrinter().writeValue(buffered, this.data);
            }
        }
    }
}
