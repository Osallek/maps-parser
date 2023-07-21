package fr.osallek.mapsparser.service.insee.death;

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
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Order(4)
public class DeathGenerationService extends GenerationService {

    private final ObjectReader reader;

    public DeathGenerationService(CsvMapper csvMapper, Environment environment) {
        super("INSEE", "https://www.insee.fr", environment);

        this.reader = csvMapper.readerFor(DeathHistoryImportLine.class)
                               .with(csvMapper.schemaFor(DeathHistoryImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(','));
    }

    @Override
    public void addData(Data data) {
        try {
            if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
                try (FileInputStream fileInputStream = new FileInputStream(Constants.SOURCES_FOLDER.resolve("base_deces_2021.csv").toFile());
                     InputStreamReader bufferedReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1)) {
                    MappingIterator<DeathHistoryImportLine> it = this.reader.readValues(bufferedReader);
                    while (it.hasNext()) {
                        DeathHistoryImportLine current = it.next();

                        if (StringUtils.isNotBlank(current.id())) {
                            Commune commune = data.getCommunes().getCommunes().get(current.id());

                            if (commune != null) {
                                if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getDeath())) {
                                    commune.getPopulation().getDeath().clear();
                                }
                            }

                            if (commune != null) {
                                commune.getPopulation().addDeath(2021, current.decesd21());
                                commune.getPopulation().addDeath(2020, current.decesd20());
                                commune.getPopulation().addDeath(2019, current.decesd19());
                                commune.getPopulation().addDeath(2018, current.decesd18());
                                commune.getPopulation().addDeath(2017, current.decesd17());
                                commune.getPopulation().addDeath(2016, current.decesd16());
                                commune.getPopulation().addDeath(2015, current.decesd15());
                                commune.getPopulation().addDeath(2014, current.decesd14());
                                commune.getPopulation().addDeath(2013, current.decesd13());
                                commune.getPopulation().addDeath(2012, current.decesd12());
                                commune.getPopulation().addDeath(2011, current.decesd11());
                                commune.getPopulation().addDeath(2010, current.decesd10());
                                commune.getPopulation().addDeath(2009, current.decesd09());
                                commune.getPopulation().addDeath(2008, current.decesd08());
                            }
                        }
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                    for (Departement departement : data.getDepartements().getDepartements().values()) {
                        if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getDeath())) {
                            departement.getPopulation().getDeath().clear();
                        }

                        departement.getCommunes().stream().map(s -> data.getCommunes().getCommunes().get(s)).filter(Objects::nonNull).forEach(commune -> {
                            if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getDeath())) {
                                commune.getPopulation().getDeath().forEach((year, pop) -> departement.getPopulation().addDeath(year, pop));
                            }
                        });
                    }

                    if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                        for (Region region : data.getRegions().getRegions().values()) {
                            if (region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getDeath())) {
                                region.getPopulation().getDeath().clear();
                            }

                            region.getDepartements()
                                  .stream()
                                  .map(s -> data.getDepartements().getDepartements().get(s))
                                  .filter(Objects::nonNull)
                                  .forEach(departement -> {
                                      if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getDeath())) {
                                          departement.getPopulation().getDeath().forEach((year, pop) -> region.getPopulation().addDeath(year, pop));
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
