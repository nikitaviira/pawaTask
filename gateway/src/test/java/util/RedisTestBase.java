package util;

import com.pawatask.gateway.GatewayMain;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static util.SingletonRedisContainer.getInstance;

@Testcontainers
@SpringBootTest(classes = GatewayMain.class)
@ActiveProfiles({"integration"})
public abstract class RedisTestBase {
    @Container
    private final static GenericContainer<SingletonRedisContainer> redisContainer = getInstance();

    @Autowired
    protected ReactiveRedisTemplate<String, Long> redisTemplate;

    @AfterEach
    public void eraseCache() {
        redisTemplate.execute(con -> con.serverCommands().flushAll())
            .subscribe(System.out::println);
    }
}