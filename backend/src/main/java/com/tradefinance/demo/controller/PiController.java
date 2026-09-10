package com.tradefinance.demo.controller;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tradefinance.demo.dto.Pi;
import com.tradefinance.demo.service.PiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PiController {
    public static final String PATH = "/api/pi";

    private final PiService service;

    /** precision travels as a quoted JSON string so trailing zeros/scale survive intact; pin that explicitly
     * rather than relying on Jackson's default lenient string-to-BigDecimal coercion. */
    public record PiRequest(@NotNull @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal precision) {}

    @PostMapping(PATH)
    public Pi calculatePi(@Valid @RequestBody PiRequest request) {
        return service.calculatePi(request.precision());
    }
}
