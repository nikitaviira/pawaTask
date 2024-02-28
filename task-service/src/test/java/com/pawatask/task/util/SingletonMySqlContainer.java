package com.pawatask.task.util;

import org.testcontainers.containers.MySQLContainer;

import static java.lang.System.setProperty;

public class SingletonMySqlContainer extends MySQLContainer<SingletonMySqlContainer> {
    private static final String IMAGE_VERSION = "mysql:latest";

    private static SingletonMySqlContainer container;

    private SingletonMySqlContainer() {
        super(IMAGE_VERSION);
    }

    public static SingletonMySqlContainer getInstance() {
        if (container == null) {
            container = new SingletonMySqlContainer();
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