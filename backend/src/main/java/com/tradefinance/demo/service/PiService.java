package com.tradefinance.demo.service;

import java.math.BigDecimal;
import com.tradefinance.demo.config.TransactionTimeouts;
import com.tradefinance.demo.dto.Pi;
import com.tradefinance.demo.repository.PiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PiService {
    private static final int MAX_PI_PRECISION_DECIMAL_SCALE = 24;

    private final PiRepository pi;

    @Transactional(timeout = TransactionTimeouts.DEFAULT_SECONDS)
    public Pi calculatePi(BigDecimal precision) {
        if (precision == null || precision.signum() <= 0 || precision.scale() > MAX_PI_PRECISION_DECIMAL_SCALE) {
            throw new IllegalArgumentException("Precision must be a positive number.");
        }

        return pi.calculatePi(precision);
    }
}
