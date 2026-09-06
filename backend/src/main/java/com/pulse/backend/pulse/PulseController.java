package com.pulse.backend.pulse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pulse")
public class PulseController {
    private final PulseService pulseService;

    public PulseController(PulseService pulseService) {
        this.pulseService = pulseService;
    }

    @GetMapping
    public PulseResponse getPulse() {
        return pulseService.getPulse();
    }

    @PostMapping("/check")
    public PulseResponse checkPulse() {
        return pulseService.checkPulse();
    }

    @PostMapping("/reset")
    public PulseResponse resetPulse() {
        return pulseService.resetPulse();
    }
}