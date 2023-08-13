package fr.osallek.mapsparser.service.insee.population.all;

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Service
@Order(6)
public class PopAllService extends GenerationService {

    private static final Pattern FILE_PATTERN = Pattern.compile("TD_POP1B_(\\d+)\\.csv\\.gz");

    private final ObjectReader reader;

    public PopAllService(CsvMapper csvMapper, Environment environment) {
        super("INSEE", "https://www.insee.fr", environment);

        this.reader = csvMapper.readerFor(PopAllImportLine.class)
                               .with(csvMapper.schemaFor(PopAllImportLine.class).withHeader().withColumnReordering(true).withColumnSeparator(';'));
    }

    @Override
    public void addData(Data data) {
        try {
            if (this.environment.matchesProfiles("dev")) {
                return;
            }

            if (MapUtils.isNotEmpty(data.getCommunes().getCommunes())) {
                List<Path> paths;
                try (Stream<Path> stream = Files.list(Constants.SOURCES_FOLDER.resolve("popAll"))
                                                .filter(p -> FILE_PATTERN.matcher(p.getFileName().toString()).matches())) {
                    paths = stream.toList();
                }

                for (Path path : paths) {
                    try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(path));
                         InputStreamReader streamReader = new InputStreamReader(gzipInputStream);
                         MappingIterator<PopAllImportLine> it = this.reader.readValues(streamReader)) {
                        Matcher matcher = FILE_PATTERN.matcher(path.getFileName().toString());
                        matcher.find();
                        Integer year = Integer.parseInt(matcher.group(1));
                        List<PopAllImportLine> lines = it.readAll();

                        lines.stream()
                             .filter(l -> StringUtils.isNotBlank(l.id()) && "COM".equals(l.type()))
                             .filter(l -> l.age() != null && l.sexe() > 0 && l.nb() != null)
                             .collect(Collectors.groupingBy(PopAllImportLine::id,
                                                            Collectors.groupingBy(PopAllImportLine::sexe, TreeMap::new,
                                                                                  Collectors.toMap(PopAllImportLine::age, PopAllImportLine::nb,
                                                                                                   (a, b) -> a, TreeMap::new))))
                             .forEach((id, map) -> {
                                 Commune commune = data.getCommunes().getCommunes().get(id);

                                 if (commune != null) {
                                     commune.getPopulation().addAllPopulation(year, map);
                                 }
                             });
                    }
                }

                if (MapUtils.isNotEmpty(data.getDepartements().getDepartements())) {
                    for (Departement departement : data.getDepartements().getDepartements().values()) {
                        if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getAllPop())) {
                            departement.getPopulation().getAllPop().clear();
                        }

                        departement.getCommunes().stream().map(s -> data.getCommunes().getCommunes().get(s)).filter(Objects::nonNull).forEach(commune -> {
                            if (commune.getPopulation() != null && MapUtils.isNotEmpty(commune.getPopulation().getAllPop())) {
                                commune.getPopulation()
                                       .getAllPop()
                                       .forEach((year, sexeMap) -> sexeMap.forEach((sexe, ageMap) -> ageMap.forEach(
                                               (age, pop) -> departement.getPopulation().addAllPopulation(year, sexe, age, pop))));
                            }
                        });
                    }

                    if (MapUtils.isNotEmpty(data.getRegions().getRegions())) {
                        for (Region region : data.getRegions().getRegions().values()) {
                            if (region.getPopulation() != null && MapUtils.isNotEmpty(region.getPopulation().getAllPop())) {
                                region.getPopulation().getAllPop().clear();
                            }

                            region.getDepartements()
                                  .stream()
                                  .map(s -> data.getDepartements().getDepartements().get(s))
                                  .filter(Objects::nonNull)
                                  .forEach(departement -> {
                                      if (departement.getPopulation() != null && MapUtils.isNotEmpty(departement.getPopulation().getAllPop())) {
                                          departement.getPopulation()
                                                     .getAllPop()
                                                     .forEach((year, sexeMap) -> sexeMap.forEach((sexe, ageMap) -> ageMap.forEach(
                                                             (age, pop) -> region.getPopulation().addAllPopulation(year, sexe, age, pop))));
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
