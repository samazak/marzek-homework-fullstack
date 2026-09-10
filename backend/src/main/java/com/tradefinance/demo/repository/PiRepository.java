package com.tradefinance.demo.repository;

import java.math.BigDecimal;
import com.tradefinance.demo.dto.Pi;

public interface PiRepository {
    Pi calculatePi(BigDecimal precision);
}
