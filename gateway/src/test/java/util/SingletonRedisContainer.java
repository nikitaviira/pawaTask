package util;

import org.testcontainers.containers.GenericContainer;

import static java.lang.System.setProperty;

public class SingletonRedisContainer extends GenericContainer<SingletonRedisContainer> {
    private static final String IMAGE_VERSION = "redis:latest";

    private static SingletonRedisContainer container;

    private SingletonRedisContainer() {
        super(IMAGE_VERSION);
    }

    public static SingletonRedisContainer getInstance() {
        if (container == null) {
            container = new SingletonRedisContainer().withExposedPorts(6379);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        setProperty("REDIS_HOST", container.getHost());
        setProperty("REDIS_PORT", container.getFirstMappedPort().toString());
    }

    @Override
    public void stop() {
    }
}