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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
            if (MapUtils.isNotEmpty(data.getCommunes())) {
                try (FileInputStream fileInputStream = new FileInputStream(Constants.SOURCES_FOLDER.resolve("populationHistory-1876-2020.csv").toFile());
                     InputStreamReader bufferedReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1)) {
                    MappingIterator<PopulationHistoryImportLine> it = this.reader.readValues(bufferedReader);

                    while (it.hasNext()) {
                        PopulationHistoryImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Commune commune = data.getCommunes().get(current.id());

                            if (commune != null) {
                                commune.setPopulation(2020, current.pmun20());
                                commune.setPopulation(2019, current.pmun19());
                                commune.setPopulation(2018, current.pmun18());
                                commune.setPopulation(2017, current.pmun17());
                                commune.setPopulation(2016, current.pmun16());
                                commune.setPopulation(2015, current.pmun15());
                                commune.setPopulation(2014, current.pmun14());
                                commune.setPopulation(2013, current.pmun13());
                                commune.setPopulation(2012, current.pmun12());
                                commune.setPopulation(2011, current.pmun11());
                                commune.setPopulation(2010, current.pmun10());
                                commune.setPopulation(2009, current.pmun09());
                                commune.setPopulation(2008, current.pmun08());
                                commune.setPopulation(2007, current.pmun07());
                                commune.setPopulation(2006, current.pmun06());
                                commune.setPopulation(1999, current.psdc99());
                                commune.setPopulation(1990, current.psdc90());
                                commune.setPopulation(1982, current.psdc82());
                                commune.setPopulation(1975, current.psdc75());
                                commune.setPopulation(1968, current.psdc68());
                                commune.setPopulation(1962, current.psdc62());
                                commune.setPopulation(1954, current.ptot54());
                                commune.setPopulation(1936, current.ptot36());
                                commune.setPopulation(1931, current.ptot1931());
                                commune.setPopulation(1926, current.ptot1926());
                                commune.setPopulation(1921, current.ptot1921());
                                commune.setPopulation(1911, current.ptot1911());
                                commune.setPopulation(1906, current.ptot1906());
                                commune.setPopulation(1901, current.ptot1901());
                                commune.setPopulation(1896, current.ptot1896());
                                commune.setPopulation(1891, current.ptot1891());
                                commune.setPopulation(1886, current.ptot1886());
                                commune.setPopulation(1881, current.ptot1881());
                                commune.setPopulation(1876, current.ptot1876());
                            }
                        }
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements())) {
                    for (Departement departement : data.getDepartements().values()) {
                        departement.getCommunes().stream().map(s -> data.getCommunes().get(s)).filter(Objects::nonNull).forEach(commune -> {
                            if (MapUtils.isNotEmpty(commune.getPopulation())) {
                                commune.getPopulation().forEach(departement::addPopulation);
                            }
                        });
                    }

                    if (MapUtils.isNotEmpty(data.getRegions())) {
                        for (Region region : data.getRegions().values()) {
                            region.getDepartements().stream().map(s -> data.getDepartements().get(s)).filter(Objects::nonNull).forEach(departement -> {
                                if (MapUtils.isNotEmpty(departement.getPopulation())) {
                                    departement.getPopulation().forEach(region::addPopulation);
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
