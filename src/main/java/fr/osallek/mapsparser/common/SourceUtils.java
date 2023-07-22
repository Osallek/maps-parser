package fr.osallek.mapsparser.common;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

public final class SourceUtils {

    private SourceUtils() {
    }

    public static Optional<InputStream> getSource(Path relativePath) throws IOException {
        Path rawPath = Constants.SOURCES_FOLDER.resolve(relativePath);
        Path gzPath = rawPath.resolveSibling(rawPath.getFileName() + ".gz");

        if (Files.exists(gzPath)) {
            return Optional.of(new GZIPInputStream(Files.newInputStream(gzPath)));
        } else if (Files.exists(rawPath)) {
            return Optional.of(Files.newInputStream(rawPath));
        }

        return Optional.empty();
    }

    public static <T> Optional<MappingIterator<T>> getSourceIterator(Path relativePath, ObjectReader objectReader) throws IOException {
        AtomicReference<IOException> e = new AtomicReference<>(null);
        Optional<MappingIterator<T>> iterator = getSource(relativePath).map(InputStreamReader::new).flatMap(reader -> {
            try {
                return Optional.of(objectReader.readValues(reader));
            } catch (IOException ioException) {
                e.set(ioException);
                return Optional.empty();
            }
        });

        if (e.get() != null) {
            throw e.get();
        }

        return iterator;
    }
}
