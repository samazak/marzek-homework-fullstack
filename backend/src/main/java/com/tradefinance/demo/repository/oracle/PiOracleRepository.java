package com.tradefinance.demo.repository.oracle;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.tradefinance.demo.dto.Pi;
import com.tradefinance.demo.repository.PiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PiOracleRepository implements PiRepository {
    private final PiMapper piMapper;

    @Override
    public Pi calculatePi(BigDecimal precision) {
        Map<String, Object> params = new HashMap<>();
        params.put("precision", precision);
        piMapper.calculatePi(params);
        String value = (String) params.get("value");
        return new Pi(value, precision.toPlainString(), piMapper.findIntermediateSteps());
    }
}
