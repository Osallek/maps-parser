package fr.osallek.mapsparser.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import fr.osallek.mapsparser.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Order
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new LocalDateTimeSerializer(Constants.DATE_TIME_FORMATTER));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.DATE_TIME_FORMATTER));
        simpleModule.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
        simpleModule.addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
        simpleModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);

        return JsonMapper.builder()
                         .findAndAddModules()
                         .addModule(simpleModule)
                         .enable(JsonWriteFeature.QUOTE_FIELD_NAMES)
                         .enable(JsonParser.Feature.ALLOW_COMMENTS)
                         .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                         .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                         .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                         .build()
                         .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Bean
    public CsvMapper csvMapper() {
        return CsvMapper.builder()
                        .findAndAddModules()
                        .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
                        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                        .build();
    }
}
