package com.tradefinance.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Trade Finance Demo API",
    description = "Age lookup, pi calculation and unpaid invoice endpoints backed by Oracle PL/SQL.",
    version = "1.0.0"))
public class TradeFinanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeFinanceApplication.class, args);
    }
}
