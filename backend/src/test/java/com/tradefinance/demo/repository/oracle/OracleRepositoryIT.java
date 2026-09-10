package com.tradefinance.demo.repository.oracle;

import java.math.BigDecimal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("oracle")
class OracleRepositoryIT {
    @Autowired
    private AgeMapper age;
    @Autowired
    private PiOracleRepository pi;
    @Autowired
    private InvoiceMapper invoices;

    @DynamicPropertySource
    static void oracleTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("ORACLE_TEST_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("ORACLE_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("ORACLE_TEST_PASSWORD"));
    }

    @Test
    void mapsDatabaseResultsInsideOneTransaction() {
        assertThat(age.describeAge(new BigDecimal("55"))).isEqualTo("You are aged");

        var result = pi.calculatePi(new BigDecimal("0.000001"));

        assertThat(new BigDecimal(result.value()).subtract(new BigDecimal("3.141592653589793")).abs())
            .isLessThan(new BigDecimal("0.000001"));
        assertThat(result.steps().getLast().partialSum()).isEqualTo(result.value());
        assertThat(invoices.findUnpaidInvoices(0, 1, "invoice_id", false)).singleElement().satisfies(invoice -> {
            assertThat(invoice.invoiceId()).isEqualTo("1");
            assertThat(invoice.invoiceDate()).isEqualTo("2026-01-10");
            assertThat(new BigDecimal(invoice.outstandingAmount())).isEqualByComparingTo("120");
        });
        assertThat(invoices.findUnpaidInvoices(1, 25, "invoice_id", false)).singleElement().satisfies(invoice ->
            assertThat(invoice.invoiceId()).isEqualTo("2"));
        assertThat(invoices.findUnpaidInvoices(0, 1, "invoice_id", true)).singleElement().satisfies(invoice ->
            assertThat(invoice.invoiceId()).isEqualTo("2"));
    }
}
