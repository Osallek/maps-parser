package fr.osallek.mapsparser.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class IntegerDeserializer extends StdDeserializer<Integer> {

    public IntegerDeserializer() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = StringUtils.deleteWhitespace(p.getValueAsString());

        return StringUtils.isBlank(value) ? null : Integer.parseInt(value);
    }
}
