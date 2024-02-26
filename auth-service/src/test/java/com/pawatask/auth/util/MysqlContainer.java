package com.pawatask.auth.util;

import org.testcontainers.containers.MySQLContainer;

import static java.lang.System.setProperty;

public class MysqlContainer extends MySQLContainer<MysqlContainer> {
    private static final String IMAGE_VERSION = "mysql:latest";

    private static MysqlContainer container;

    private MysqlContainer() {
        super(IMAGE_VERSION);
    }

    public static MysqlContainer getInstance() {
        if (container == null) {
            container = new MysqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        setProperty("DB_URL", container.getJdbcUrl());
        setProperty("DB_USERNAME", container.getUsername());
        setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }
}