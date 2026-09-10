package com.tradefinance.demo.service;

import java.math.BigDecimal;
import java.util.List;
import com.tradefinance.demo.dto.Pi;
import com.tradefinance.demo.repository.PiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PiServiceTest {
    private final PiRepository pi = mock(PiRepository.class);
    private PiService service;

    @BeforeEach
    void setUp() {
        service = new PiService(pi);
    }

    @Test
    void rejectsNullInput() {
        assertThatThrownBy(() -> service.calculatePi(null)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(pi);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.1"})
    void rejectsNonPositivePrecision(String value) {
        assertThatThrownBy(() -> service.calculatePi(new BigDecimal(value))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(pi);
    }

    @Test
    void rejectsPrecisionWithExcessiveScale() {
        var precision = new BigDecimal("1e-25");

        assertThatThrownBy(() -> service.calculatePi(precision)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(pi);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e-12", "0.1", "1e-13", "0.11", "5"})
    void acceptsAnyPositivePrecisionWithinScaleLimit(String value) {
        var precision = new BigDecimal(value);
        var expected = new Pi("3.14", value, List.of());
        when(pi.calculatePi(precision)).thenReturn(expected);

        var result = service.calculatePi(precision);

        assertThat(result).isEqualTo(expected);
    }
}
