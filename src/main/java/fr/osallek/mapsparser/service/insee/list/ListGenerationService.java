package fr.osallek.mapsparser.service.insee.list;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ListSource extends GenerationService {

    private final ObjectReader communesReader;

    private final ObjectReader departementsReader;

    private final ObjectReader regionsReader;

    public ListSource() {
        super("INSEE", "https://www.insee.fr");

        CsvMapper csvMapper = CsvMapper.builder()
                                       .findAndAddModules()
                                       .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
                                       .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                                       .build();

        this.communesReader = csvMapper.readerFor(CommuneImportLine.class)
                                       .with(csvMapper.schemaFor(CommuneImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(','));
        this.departementsReader = csvMapper.readerFor(DepartementImportLine.class)
                                           .with(csvMapper.schemaFor(DepartementImportLine.class)
                                                          .withHeader()
                                                          .withColumnReordering(true)
                                                          .withColumnSeparator(','));
        this.regionsReader = csvMapper.readerFor(RegionImportLine.class)
                                      .with(csvMapper.schemaFor(RegionImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(','));
    }

    @Override
    public void addData(Data data) {
        Path regionsCsv = Constants.SOURCES_FOLDER.resolve("region.csv");

        if (Files.exists(regionsCsv)) {
            this.logger.info("Reading regions");

            try {
                MappingIterator<RegionImportLine> it = this.regionsReader.readValues(new InputStreamReader(new FileInputStream(regionsCsv.toFile())));

                if (data.getRegions() == null) {
                    data.setRegions(new TreeMap<>());
                }

                while (it.hasNext()) {
                    RegionImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        data.getRegions().compute(current.id(), (id, region) -> {
                            if (region == null) {
                                region = new Region(current.id(), current.name(), current.chefLieu());
                            } else {
                                region.setName(current.name());
                                region.setChefLieu(current.chefLieu());
                            }

                            return region;
                        });
                    }
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }

        Path departementsCsv = Constants.SOURCES_FOLDER.resolve("departement.csv");

        if (Files.exists(departementsCsv)) {
            this.logger.info("Reading departements");

            try {
                MappingIterator<DepartementImportLine> it = this.departementsReader.readValues(
                        new InputStreamReader(new FileInputStream(departementsCsv.toFile())));

                if (data.getDepartements() == null) {
                    data.setDepartements(new TreeMap<>());
                }

                while (it.hasNext()) {
                    DepartementImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        data.getDepartements().compute(current.id(), (id, departement) -> {
                            if (departement == null) {
                                departement = new Departement(current.id(), current.name(), current.region(), current.chefLieu());
                            } else {
                                departement.setName(current.name());
                                departement.setRegion(current.region());
                                departement.setChefLieu(current.chefLieu());
                            }

                            return departement;
                        });
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements()) && MapUtils.isNotEmpty(data.getRegions())) {
                    data.getDepartements()
                        .values()
                        .stream()
                        .collect(Collectors.groupingBy(Departement::getRegion, Collectors.mapping(Departement::getId, Collectors.toCollection(TreeSet::new))))
                        .forEach((region, departements) -> {
                            if (data.getRegions().containsKey(region)) {
                                data.getRegions().get(region).setDepartements(departements);
                            }
                        });
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }

        Path communesCsv = Constants.SOURCES_FOLDER.resolve("commune.csv");

        if (Files.exists(communesCsv)) {
            this.logger.info("Reading communes");

            try {
                MappingIterator<CommuneImportLine> it = this.communesReader.readValues(new InputStreamReader(new FileInputStream(communesCsv.toFile())));

                if (data.getCommunes() == null) {
                    data.setCommunes(new TreeMap<>());
                }

                while (it.hasNext()) {
                    CommuneImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id()) && "COM".equals(current.type())) {
                        data.getCommunes().compute(current.id(), (id, commune) -> {
                            if (commune == null) {
                                commune = new Commune(current.id(), current.name(), current.departement());
                            } else {
                                commune.setName(current.name());
                                commune.setDepartement(current.departement());
                            }

                            return commune;
                        });
                    }
                }

                if (MapUtils.isNotEmpty(data.getCommunes()) && MapUtils.isNotEmpty(data.getDepartements())) {
                    data.getCommunes()
                        .values()
                        .stream()
                        .collect(Collectors.groupingBy(Commune::getDepartement, Collectors.mapping(Commune::getId, Collectors.toCollection(TreeSet::new))))
                        .forEach((departement, communes) -> {
                            if (data.getDepartements().containsKey(departement)) {
                                data.getDepartements().get(departement).setCommunes(communes);
                            }
                        });
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
    }
}
