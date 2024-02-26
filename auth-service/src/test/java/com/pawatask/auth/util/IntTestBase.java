package com.pawatask.auth.util;

import com.pawatask.auth.AuthMain;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.pawatask.auth.util.MysqlContainer.getInstance;

@Testcontainers
@SpringBootTest(classes = AuthMain.class)
@ActiveProfiles({"integration"})
public abstract class IntTestBase {
    @Autowired
    EraseDbHelper eraseDbHelper;

    @Container
    public static MySQLContainer<MysqlContainer> mysqlContainer = getInstance();

    @AfterEach
    void afterEach() {
        eraseDbHelper.eraseDb();
    }
}