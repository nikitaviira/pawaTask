package com.pawatask.email.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class LoggerExtension implements BeforeEachCallback, AfterEachCallback {

    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger("com.pawatask.email");

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        listAppender.stop();
        listAppender.list.clear();
        logger.detachAppender(listAppender);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        logger.addAppender(listAppender);
        listAppender.start();
    }

    public List<String> getFormattedMessages() {
        return listAppender.list.stream().map(ILoggingEvent::getFormattedMessage).collect(toList());
    }
}