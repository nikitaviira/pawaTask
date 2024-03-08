package com.pawatask.email.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pawatask.email.EmailMain;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EmailMain.class)
public abstract class IntTestBase {
    protected String convertObjectToJsonString(Object object) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(object);
    }
}