package fr.osallek.mapsparser.service.insee.birth;

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
@Order(3)
public class BirthGenerationService extends GenerationService {

    private final ObjectReader reader;

    public BirthGenerationService(CsvMapper csvMapper, Environment environment) {
        super("INSEE", "https://www.insee.fr", environment);

        this.reader = csvMapper.readerFor(BirthHistoryImportLine.class)
                               .with(csvMapper.schemaFor(BirthHistoryImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(','));
    }

    @Override
    public void addData(Data data) {
        try {
            //Todo brith/death per capita
            if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
                try (FileInputStream fileInputStream = new FileInputStream(
                        Constants.SOURCES_FOLDER.resolve("birth").resolve("base_naissances_2021.csv.gz").toFile());
                     GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                     InputStreamReader streamReader = new InputStreamReader(gzipInputStream, StandardCharsets.ISO_8859_1);
                     MappingIterator<BirthHistoryImportLine> it = this.reader.readValues(streamReader)) {
                    while (it.hasNext()) {
                        BirthHistoryImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Commune commune = data.getCommunes().getCommunes().get(current.id());

                            if (commune != null) {
                                if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getBirth())) {
                                    commune.getPopulation().getBirth().clear();
                                }
                            }

                            if (commune != null) {
                                commune.getPopulation().addBirth(2021, current.naisd21());
                                commune.getPopulation().addBirth(2020, current.naisd20());
                                commune.getPopulation().addBirth(2019, current.naisd19());
                                commune.getPopulation().addBirth(2018, current.naisd18());
                                commune.getPopulation().addBirth(2017, current.naisd17());
                                commune.getPopulation().addBirth(2016, current.naisd16());
                                commune.getPopulation().addBirth(2015, current.naisd15());
                                commune.getPopulation().addBirth(2014, current.naisd14());
                                commune.getPopulation().addBirth(2013, current.naisd13());
                                commune.getPopulation().addBirth(2012, current.naisd12());
                                commune.getPopulation().addBirth(2011, current.naisd11());
                                commune.getPopulation().addBirth(2010, current.naisd10());
                                commune.getPopulation().addBirth(2009, current.naisd09());
                                commune.getPopulation().addBirth(2008, current.naisd08());
                            }
                        }
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                    for (Departement departement : data.getDepartements().getDepartements().values()) {
                        if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getBirth())) {
                            departement.getPopulation().getBirth().clear();
                        }

                        departement.getCommunes().stream().map(s -> data.getCommunes().getCommunes().get(s)).filter(Objects::nonNull).forEach(commune -> {
                            if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getBirth())) {
                                commune.getPopulation().getBirth().forEach((year, pop) -> departement.getPopulation().addBirth(year, pop));
                            }
                        });
                    }

                    if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                        for (Region region : data.getRegions().getRegions().values()) {
                            if (region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getBirth())) {
                                region.getPopulation().getBirth().clear();
                            }

                            region.getDepartements()
                                  .stream()
                                  .map(s -> data.getDepartements().getDepartements().get(s))
                                  .filter(Objects::nonNull)
                                  .forEach(departement -> {
                                      if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getBirth())) {
                                          departement.getPopulation().getBirth().forEach((year, pop) -> region.getPopulation().addBirth(year, pop));
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
