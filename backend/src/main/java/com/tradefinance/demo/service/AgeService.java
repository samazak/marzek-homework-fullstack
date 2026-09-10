package com.tradefinance.demo.service;

import java.math.BigDecimal;
import com.tradefinance.demo.config.TransactionTimeouts;
import com.tradefinance.demo.dto.Age;
import com.tradefinance.demo.repository.AgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgeService {
    private static final int MAX_AGE_DECIMAL_SCALE = 12;
    private static final int MAX_AGE_SIGNIFICANT_DIGITS = 33;

    private final AgeRepository ages;

    @Transactional(readOnly = true, timeout = TransactionTimeouts.DEFAULT_SECONDS)
    public Age describeAge(BigDecimal age) {
        if (age == null || age.signum() < 0 || age.compareTo(new BigDecimal("1e20")) > 0
                || age.scale() > MAX_AGE_DECIMAL_SCALE || age.precision() > MAX_AGE_SIGNIFICANT_DIGITS) {
            throw new IllegalArgumentException("Age must be between 0 and 1e20 with at most 12 decimal places.");
        }
        return new Age(age.toPlainString(), ages.describeAge(age));
    }
}
