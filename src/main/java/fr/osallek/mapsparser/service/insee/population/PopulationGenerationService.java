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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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

                    if (MapUtils.isNotEmpty(data.getDepartements())) {
                        data.getDepartements()
                            .values()
                            .stream()
                            .filter(d -> MapUtils.isNotEmpty(d.getPopulation()))
                            .forEach(departement -> departement.getPopulation().clear());
                    }

                    if (MapUtils.isNotEmpty(data.getRegions())) {
                        data.getRegions()
                            .values()
                            .stream()
                            .filter(d -> MapUtils.isNotEmpty(d.getPopulation()))
                            .forEach(region -> region.getPopulation().clear());
                    }

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

                            if (MapUtils.isNotEmpty(data.getDepartements())) {
                                Departement departement = data.getDepartements().get(current.departement());

                                if (departement != null) {
                                    departement.addPopulation(2020, current.pmun20());
                                    departement.addPopulation(2019, current.pmun19());
                                    departement.addPopulation(2018, current.pmun18());
                                    departement.addPopulation(2017, current.pmun17());
                                    departement.addPopulation(2016, current.pmun16());
                                    departement.addPopulation(2015, current.pmun15());
                                    departement.addPopulation(2014, current.pmun14());
                                    departement.addPopulation(2013, current.pmun13());
                                    departement.addPopulation(2012, current.pmun12());
                                    departement.addPopulation(2011, current.pmun11());
                                    departement.addPopulation(2010, current.pmun10());
                                    departement.addPopulation(2009, current.pmun09());
                                    departement.addPopulation(2008, current.pmun08());
                                    departement.addPopulation(2007, current.pmun07());
                                    departement.addPopulation(2006, current.pmun06());
                                    departement.addPopulation(1999, current.psdc99());
                                    departement.addPopulation(1990, current.psdc90());
                                    departement.addPopulation(1982, current.psdc82());
                                    departement.addPopulation(1975, current.psdc75());
                                    departement.addPopulation(1968, current.psdc68());
                                    departement.addPopulation(1962, current.psdc62());
                                    departement.addPopulation(1954, current.ptot54());
                                    departement.addPopulation(1936, current.ptot36());
                                    departement.addPopulation(1931, current.ptot1931());
                                    departement.addPopulation(1926, current.ptot1926());
                                    departement.addPopulation(1921, current.ptot1921());
                                    departement.addPopulation(1911, current.ptot1911());
                                    departement.addPopulation(1906, current.ptot1906());
                                    departement.addPopulation(1901, current.ptot1901());
                                    departement.addPopulation(1896, current.ptot1896());
                                    departement.addPopulation(1891, current.ptot1891());
                                    departement.addPopulation(1886, current.ptot1886());
                                    departement.addPopulation(1881, current.ptot1881());
                                    departement.addPopulation(1876, current.ptot1876());
                                }
                            }

                            if (MapUtils.isNotEmpty(data.getRegions())) {
                                Region region = data.getRegions().get(current.region());

                                if (region != null) {
                                    region.addPopulation(2020, current.pmun20());
                                    region.addPopulation(2019, current.pmun19());
                                    region.addPopulation(2018, current.pmun18());
                                    region.addPopulation(2017, current.pmun17());
                                    region.addPopulation(2016, current.pmun16());
                                    region.addPopulation(2015, current.pmun15());
                                    region.addPopulation(2014, current.pmun14());
                                    region.addPopulation(2013, current.pmun13());
                                    region.addPopulation(2012, current.pmun12());
                                    region.addPopulation(2011, current.pmun11());
                                    region.addPopulation(2010, current.pmun10());
                                    region.addPopulation(2009, current.pmun09());
                                    region.addPopulation(2008, current.pmun08());
                                    region.addPopulation(2007, current.pmun07());
                                    region.addPopulation(2006, current.pmun06());
                                    region.addPopulation(1999, current.psdc99());
                                    region.addPopulation(1990, current.psdc90());
                                    region.addPopulation(1982, current.psdc82());
                                    region.addPopulation(1975, current.psdc75());
                                    region.addPopulation(1968, current.psdc68());
                                    region.addPopulation(1962, current.psdc62());
                                    region.addPopulation(1954, current.ptot54());
                                    region.addPopulation(1936, current.ptot36());
                                    region.addPopulation(1931, current.ptot1931());
                                    region.addPopulation(1926, current.ptot1926());
                                    region.addPopulation(1921, current.ptot1921());
                                    region.addPopulation(1911, current.ptot1911());
                                    region.addPopulation(1906, current.ptot1906());
                                    region.addPopulation(1901, current.ptot1901());
                                    region.addPopulation(1896, current.ptot1896());
                                    region.addPopulation(1891, current.ptot1891());
                                    region.addPopulation(1886, current.ptot1886());
                                    region.addPopulation(1881, current.ptot1881());
                                    region.addPopulation(1876, current.ptot1876());
                                }
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
