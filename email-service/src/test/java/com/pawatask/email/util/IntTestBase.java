package com.pawatask.email.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pawatask.email.EmailMain;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = EmailMain.class)
@ActiveProfiles({"integration"})
public abstract class IntTestBase {
    @RegisterExtension
    protected final LoggerExtension loggerExtension = new LoggerExtension();

    protected String convertObjectToJsonString(Object object) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(object);
    }
}