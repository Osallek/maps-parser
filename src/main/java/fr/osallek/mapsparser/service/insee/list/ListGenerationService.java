package fr.osallek.mapsparser.service.insee.list;

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
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ListGenerationService extends GenerationService {

    private final ObjectReader communesReader;

    private final ObjectReader departementsReader;

    private final ObjectReader regionsReader;

    public ListGenerationService(CsvMapper csvMapper, Environment environment) {
        super("INSEE", "https://www.insee.fr", environment);

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
        Path regionsCsv = Constants.SOURCES_FOLDER.resolve("list").resolve("region.csv.gz");

        if (Files.exists(regionsCsv)) {
            try (InputStream fileInputStream = Files.newInputStream(regionsCsv); GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 InputStreamReader streamReader = new InputStreamReader(gzipInputStream);
                 MappingIterator<RegionImportLine> it = this.regionsReader.readValues(streamReader)) {
                if (data.getRegions().getRegions() == null) {
                    data.getRegions().setRegions(new TreeMap<>());
                }

                while (it.hasNext()) {
                    RegionImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        if (current.id().charAt(0) == '0') {
                            continue; //Ignore DROM-COM
                        }

                        data.getRegions().getRegions().compute(current.id(), (id, region) -> {
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

        Path departementsCsv = Constants.SOURCES_FOLDER.resolve("list").resolve("departement.csv.gz");

        if (Files.exists(departementsCsv)) {
            try (InputStream fileInputStream = Files.newInputStream(departementsCsv); GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 InputStreamReader streamReader = new InputStreamReader(gzipInputStream);
                 MappingIterator<DepartementImportLine> it = this.departementsReader.readValues(streamReader)) {
                if (data.getDepartements().getDepartements() == null) {
                    data.getDepartements().setDepartements(new TreeMap<>());
                }

                while (it.hasNext()) {
                    DepartementImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id())) {
                        if (current.id().charAt(0) == '9' && current.id().charAt(1) == '7') {
                            continue; //Ignore DROM-COM
                        }

                        data.getDepartements().getDepartements().compute(current.id(), (id, departement) -> {
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

                if (MapUtils.isNotEmpty(data.getDepartements().getDepartements()) && MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                    data.getDepartements()
                        .getDepartements()
                        .values()
                        .stream()
                        .collect(Collectors.groupingBy(Departement::getRegion, Collectors.mapping(Departement::getId, Collectors.toCollection(TreeSet::new))))
                        .forEach((region, departements) -> {
                            if (data.getRegions().getRegions().containsKey(region)) {
                                data.getRegions().getRegions().get(region).setDepartements(departements);
                            }
                        });
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }

        Path communesCsv = Constants.SOURCES_FOLDER.resolve("list").resolve("commune.csv.gz");

        if (Files.exists(communesCsv)) {
            try (InputStream fileInputStream = Files.newInputStream(communesCsv); GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                 InputStreamReader streamReader = new InputStreamReader(gzipInputStream);
                 MappingIterator<CommuneImportLine> it = this.communesReader.readValues(streamReader)) {
                if (data.getCommunes().getCommunes() == null) {
                    data.getCommunes().setCommunes(new TreeMap<>());
                }

                while (it.hasNext()) {
                    CommuneImportLine current = it.next();

                    if (StringUtils.isNotBlank(current.id()) && "COM".equals(current.type())) {
                        if (current.id().charAt(0) == '9' && current.id().charAt(1) == '7') {
                            continue; //Ignore DROM-COM
                        }

                        data.getCommunes().getCommunes().compute(current.id(), (id, commune) -> {
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

                if (MapUtils.isNotEmpty(data.getCommunes().getCommunes()) && MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                    data.getCommunes()
                        .getCommunes()
                        .values()
                        .stream()
                        .collect(Collectors.groupingBy(Commune::getDepartement, Collectors.mapping(Commune::getId, Collectors.toCollection(TreeSet::new))))
                        .forEach((departement, communes) -> {
                            if (data.getDepartements().getDepartements().containsKey(departement)) {
                                data.getDepartements().getDepartements().get(departement).setCommunes(communes);
                            }
                        });
                }
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
    }
}
