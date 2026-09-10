package com.tradefinance.demo.repository.oracle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Requires install.sql and 003_demo_data.sql in a disposable schema. Never use production. */
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("oracle")
class OraclePlsqlIT {
    @Autowired
    private DataSource dataSource;
    private Connection connection;

    @DynamicPropertySource
    static void oracleTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("ORACLE_TEST_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("ORACLE_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("ORACLE_TEST_PASSWORD"));
    }

    @BeforeEach
    void connect() {
        connection = DataSourceUtils.getConnection(dataSource);
    }

    @AfterEach
    void release() {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    @Test
    void ageBoundariesAndFractionalValues() throws Exception {
        String[] ages = {"0", "6.99", "7", "17.99", "18", "39.99", "40", "54.99", "55", "150"};
        String[] text = {"You are infant", "You are schoolchild", "You are adult", "You are in middle-age", "You are aged"};

        try (var query = connection.prepareStatement("SELECT age_pkg.age_description(?) FROM dual")) {
            for (int i = 0; i < ages.length; i++) {
                query.setBigDecimal(1, new BigDecimal(ages[i]));
                try (var rows = query.executeQuery()) {
                    rows.next();
                    assertThat(rows.getString(1)).isEqualTo(text[i / 2]);
                }
            }
        }
    }

    @Test
    void ageReadsChangedDatabaseText() throws Exception {
        try (var command = connection.createStatement()) {
            command.executeUpdate("UPDATE age_text SET message = 'Changed in DB' WHERE minimum_age = 18");

            try (var rows = command.executeQuery("SELECT age_pkg.age_description(18) FROM dual")) {
                rows.next();
                assertThat(rows.getString(1)).isEqualTo("Changed in DB");
            }
        }
    }

    @Test
    void rejectsNegativeAndNullAge() throws Exception {
        try (var query = connection.prepareStatement("SELECT age_pkg.age_description(?) FROM dual")) {
            query.setBigDecimal(1, new BigDecimal("-1"));
            assertThatThrownBy(query::executeQuery).isInstanceOfSatisfying(SQLException.class,
                error -> assertThat(error.getErrorCode()).isEqualTo(20001));

            query.setNull(1, Types.NUMERIC);
            assertThatThrownBy(query::executeQuery).isInstanceOfSatisfying(SQLException.class,
                error -> assertThat(error.getErrorCode()).isEqualTo(20001));
        }
    }

    @Test
    void rollbackRestoresPriorIntermediateRows() throws Exception {
        calculate("0.1");
        var beforeReplacement = connection.setSavepoint();
        calculate("1e-12");

        assertThat(countSteps()).isGreaterThan(1);

        connection.rollback(beforeReplacement);

        assertThat(countSteps()).isEqualTo(1);
    }

    @Test
    void piMeetsToleranceAndReplacesHistory() throws Exception {
        BigDecimal reference = new BigDecimal("3.141592653589793238462643383279502884197");

        var precise = calculate("1e-12");

        assertThat(precise.subtract(reference).abs()).isLessThan(new BigDecimal("1e-12"));
        int preciseCount = countSteps();

        var loose = calculate("0.1");

        assertThat(loose.subtract(reference).abs()).isLessThan(new BigDecimal("0.1"));
        assertThat(countSteps()).isLessThan(preciseCount);
        try (var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT partial_sum FROM pi_intermediate_result ORDER BY iteration DESC FETCH FIRST 1 ROW ONLY")) {
            rows.next();
            assertThat(rows.getBigDecimal(1)).isEqualByComparingTo(loose);
        }

        assertThatThrownBy(() -> calculate("0")).isInstanceOf(SQLException.class);

        assertThat(countSteps()).isEqualTo(1);
    }

    @Test
    void rejectsPrecisionBeyondNumberCapability() throws Exception {
        assertThatThrownBy(() -> calculate("1e-45")).isInstanceOf(SQLException.class);
    }

    @Test
    void unpaidIncludesMissingAndMultiplePaymentsButExcludesSettledOverpaidAndZero() throws Exception {
        var ids = new ArrayList<Long>();

        try (var statement = connection.createStatement(); var rows = statement.executeQuery(
                "SELECT invoice_id FROM unpaid_invoice ORDER BY invoice_id")) {
            while (rows.next()) ids.add(rows.getLong(1));
        }

        assertThat(ids).isEqualTo(List.of(1L, 2L));
    }

    @Test
    void rejectsCurrencyMismatch() throws Exception {
        try (var statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.executeUpdate(
                "INSERT INTO payment VALUES (999, SYSDATE, 1, 'USD', 1)"))
                .isInstanceOf(SQLException.class);
        }
    }

    @Test
    void concurrentCalculationsDoNotInterfereAcrossSessions() throws Exception {
        calculate("0.1");

        try (var other = DriverManager.getConnection(System.getenv("ORACLE_TEST_URL"),
                System.getenv("ORACLE_TEST_USER"), System.getenv("ORACLE_TEST_PASSWORD"))) {
            try (var call = other.prepareCall("{? = call pi_pkg.calculate_pi(?)}")) {
                call.setQueryTimeout(10);
                call.registerOutParameter(1, Types.NUMERIC);
                call.setBigDecimal(2, new BigDecimal("1e-12"));
                call.execute();

                assertThat(call.getBigDecimal(1)).isNotNull();
            }
        }

        assertThat(countSteps()).isEqualTo(1);
    }

    private BigDecimal calculate(String precision) throws Exception {
        try (var call = connection.prepareCall("{? = call pi_pkg.calculate_pi(?)}")) {
            call.setQueryTimeout(15);
            call.registerOutParameter(1, Types.NUMERIC);
            call.setBigDecimal(2, new BigDecimal(precision));
            call.execute();
            return call.getBigDecimal(1);
        }
    }

    private int countSteps() throws Exception {
        try (var command = connection.createStatement(); var rows = command.executeQuery("SELECT COUNT(*) FROM pi_intermediate_result")) {
            rows.next();
            return rows.getInt(1);
        }
    }
}
