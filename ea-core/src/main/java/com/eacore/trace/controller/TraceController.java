package com.eacore.trace.controller;

import com.eacore.trace.dto.TraceInfo;
import com.eacore.trace.service.TraceInfoRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public class TraceController {

    private final TraceInfoRegistry traceInfoRegistry;

    public TraceController(TraceInfoRegistry traceInfoRegistry) {
        this.traceInfoRegistry = traceInfoRegistry;
    }

    @GetMapping("/get_trace_info")
    public ResponseEntity<TraceInfo> getTraceInfo(@RequestParam("interface") String interfaceName) {
        Optional<TraceInfo> traceInfoOptional = traceInfoRegistry.getTraceInfo(interfaceName);

        return traceInfoOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}