package com.tradefinance.demo.dto;

import java.util.List;

public record Pi(String value, String precision, List<PiStep> steps) {
    public Pi { steps = List.copyOf(steps); }
}
