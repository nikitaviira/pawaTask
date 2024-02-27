package com.pawatask.task.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

import static com.pawatask.task.util.DateUtil.formatDate;

public class LocalDateSerializer extends StdSerializer<LocalDate> {
    public LocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (localDate != null) {
            jsonGenerator.writeString(formatDate(localDate));
        }
    }
}