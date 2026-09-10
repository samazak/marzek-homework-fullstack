package com.tradefinance.demo.service;

import java.math.BigDecimal;
import com.tradefinance.demo.repository.AgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AgeServiceTest {
    private final AgeRepository ages = mock(AgeRepository.class);
    private AgeService service;

    @BeforeEach
    void setUp() {
        service = new AgeService(ages);
    }

    @Test
    void returnsDatabaseTextRatherThanDuplicatingRules() {
        when(ages.describeAge(new BigDecimal("7"))).thenReturn("Configurable text");

        var result = service.describeAge(new BigDecimal("7"));

        assertThat(result.text()).isEqualTo("Configurable text");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "1e21"})
    void rejectsInvalidAgeBeforeDatabaseAccess(String value) {
        assertThatThrownBy(() -> service.describeAge(new BigDecimal(value))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(ages);
    }

    @Test
    void rejectsNullInput() {
        assertThatThrownBy(() -> service.describeAge(null)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(ages);
    }
}
