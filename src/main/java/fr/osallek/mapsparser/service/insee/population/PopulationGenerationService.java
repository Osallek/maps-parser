package fr.osallek.mapsparser.service.insee.population;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fr.osallek.mapsparser.common.Constants;
import fr.osallek.mapsparser.service.GenerationService;
import fr.osallek.mapsparser.service.object.Commune;
import fr.osallek.mapsparser.service.object.Data;
import fr.osallek.mapsparser.service.object.Departement;
import fr.osallek.mapsparser.service.object.Region;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

@Service
@Order(2)
public class PopulationGenerationService extends GenerationService {

    private final ObjectReader reader;

    public PopulationGenerationService(CsvMapper csvMapper, Environment environment) {
        super("INSEE", "https://www.insee.fr", environment);

        this.reader = csvMapper.readerFor(PopulationHistoryImportLine.class)
                               .with(csvMapper.schemaFor(PopulationHistoryImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(','));
    }

    @Override
    public void addData(Data data) {
        try {
            if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
                try (FileInputStream fileInputStream = new FileInputStream(
                        Constants.SOURCES_FOLDER.resolve("pop").resolve("populationHistory-1876-2020.csv.gz").toFile());
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     InputStreamReader streamReader = new InputStreamReader(gzipInputStream, StandardCharsets.ISO_8859_1);
                     MappingIterator<PopulationHistoryImportLine> it = this.reader.readValues(streamReader)) {
                    boolean paris = false;

                    while (it.hasNext()) {
                        PopulationHistoryImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Commune commune;

                            if (current.id().startsWith("75")) {
                                commune = data.getCommunes().getCommunes().get("75056");

                                if (!paris && commune != null) {
                                    paris = true;
                                    if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation())) {
                                        commune.getPopulation().getPopulation().clear();
                                    }
                                }
                            } else {
                                commune = data.getCommunes().getCommunes().get(current.id());

                                if (commune != null) {
                                    if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation())) {
                                        commune.getPopulation().getPopulation().clear();
                                    }
                                }
                            }

                            if (commune != null) {
                                commune.addPopulation(2020, current.pmun20());
                                commune.addPopulation(2019, current.pmun19());
                                commune.addPopulation(2018, current.pmun18());
                                commune.addPopulation(2017, current.pmun17());
                                commune.addPopulation(2016, current.pmun16());
                                commune.addPopulation(2015, current.pmun15());
                                commune.addPopulation(2014, current.pmun14());
                                commune.addPopulation(2013, current.pmun13());
                                commune.addPopulation(2012, current.pmun12());
                                commune.addPopulation(2011, current.pmun11());
                                commune.addPopulation(2010, current.pmun10());
                                commune.addPopulation(2009, current.pmun09());
                                commune.addPopulation(2008, current.pmun08());
                                commune.addPopulation(2007, current.pmun07());
                                commune.addPopulation(2006, current.pmun06());
                                commune.addPopulation(1999, current.psdc99());
                                commune.addPopulation(1990, current.psdc90());
                                commune.addPopulation(1982, current.psdc82());
                                commune.addPopulation(1975, current.psdc75());
                                commune.addPopulation(1968, current.psdc68());
                                commune.addPopulation(1962, current.psdc62());
                                commune.addPopulation(1954, current.ptot54());
                                commune.addPopulation(1936, current.ptot36());
                                commune.addPopulation(1931, current.ptot1931());
                                commune.addPopulation(1926, current.ptot1926());
                                commune.addPopulation(1921, current.ptot1921());
                                commune.addPopulation(1911, current.ptot1911());
                                commune.addPopulation(1906, current.ptot1906());
                                commune.addPopulation(1901, current.ptot1901());
                                commune.addPopulation(1896, current.ptot1896());
                                commune.addPopulation(1891, current.ptot1891());
                                commune.addPopulation(1886, current.ptot1886());
                                commune.addPopulation(1881, current.ptot1881());
                                commune.addPopulation(1876, current.ptot1876());
                            }
                        }
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                    for (Departement departement : data.getDepartements().getDepartements().values()) {
                        if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getPopulation())) {
                            departement.getPopulation().getPopulation().clear();
                        }

                        departement.getCommunes().stream().map(s -> data.getCommunes().getCommunes().get(s)).filter(Objects::nonNull).forEach(commune -> {
                            if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getPopulation())) {
                                commune.getPopulation().getPopulation().forEach(departement::addPopulation);
                            }
                        });
                    }

                    if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                        for (Region region : data.getRegions().getRegions().values()) {
                            if (region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getPopulation())) {
                                region.getPopulation().getPopulation().clear();
                            }

                            region.getDepartements()
                                  .stream()
                                  .map(s -> data.getDepartements().getDepartements().get(s))
                                  .filter(Objects::nonNull)
                                  .forEach(departement -> {
                                      if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getPopulation())) {
                                          departement.getPopulation().getPopulation().forEach(region::addPopulation);
                                      }
                                  });
                        }
                    }
                }
            }
        } catch (IOException e) {
            this.logger.error(e.getMessage(), e);
        }
    }
}
