package fr.osallek.mapsparser.service.odt;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
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

@Service
@Order(1)
public class AreaGenerationService extends GenerationService {

    private final ObjectReader reader;

    public AreaGenerationService(CsvMapper csvMapper, Environment environment) {
        super("L'Observatoire des Territoires", "https://www.observatoire-des-territoires.gouv.fr", environment);
        this.reader = csvMapper.readerFor(AreaImportLine.class).with(csvMapper.schemaFor(AreaImportLine.class).withHeader().withColumnReordering(true));
    }

    @Override
    public void addData(Data data) {
        try {
            if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
                try (InputStream fileInputStream = Files.newInputStream(Constants.SOURCES_FOLDER.resolve("area").resolve("superficie_communes.csv.gz"));
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(gzipInputStream).get();
                     MappingIterator<AreaImportLine> it = this.reader.readValues(bomInputStream)) {
                    while (it.hasNext()) {
                        AreaImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Commune commune = data.getCommunes().getCommunes().get(current.id());

                            if (commune != null && (current.area() != null)) {
                                commune.setArea(current.area());
                            }
                        }
                    }
                }
            }

            if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                try (InputStream fileInputStream = Files.newInputStream(Constants.SOURCES_FOLDER.resolve("area").resolve("superficie_departements.csv.gz"));
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(gzipInputStream).get();
                     MappingIterator<AreaImportLine> it = this.reader.readValues(bomInputStream)) {
                    while (it.hasNext()) {
                        AreaImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Departement departement = data.getDepartements().getDepartements().get(current.id());

                            if (departement != null && (current.area() != null)) {
                                departement.setArea(current.area());
                            }
                        }
                    }
                }
            }

            if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                try (InputStream fileInputStream = Files.newInputStream(Constants.SOURCES_FOLDER.resolve("area").resolve("superficie_regions.csv.gz"));
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(gzipInputStream).get();
                     MappingIterator<AreaImportLine> it = this.reader.readValues(bomInputStream)) {
                    while (it.hasNext()) {
                        AreaImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Region region = data.getRegions().getRegions().get(current.id());

                            if (region != null && (current.area() != null)) {
                                region.setArea(current.area());
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
