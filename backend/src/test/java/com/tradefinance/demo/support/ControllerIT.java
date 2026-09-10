package com.tradefinance.demo.support;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** Full Spring context against the real Oracle test database; see database setup in README under "oracleTest". */
@SpringBootTest
@AutoConfigureMockMvc
@Tag("oracle")
public abstract class ControllerIT {
    @Autowired
    protected MockMvc mvc;

    @DynamicPropertySource
    static void oracleTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("ORACLE_TEST_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("ORACLE_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("ORACLE_TEST_PASSWORD"));
    }
}
