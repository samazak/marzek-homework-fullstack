package com.tradefinance.demo.controller;

import java.math.BigDecimal;
import com.tradefinance.demo.dto.Age;
import com.tradefinance.demo.service.AgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgeController {
    public static final String PATH = "/api/age";

    private final AgeService service;

    @GetMapping(PATH)
    public Age describeAge(@RequestParam BigDecimal age) { return service.describeAge(age); }
}
