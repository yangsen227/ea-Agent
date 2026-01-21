package com.eacore.trace.service;

import com.eacore.trace.dto.TraceInfo;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TraceInfoRegistry {

    private final ConcurrentHashMap<String, TraceInfo> registry = new ConcurrentHashMap<>();

    public void updateTraceInfo(String interfaceName, TraceInfo traceInfo) {
        registry.put(interfaceName, traceInfo);
    }

    public Optional<TraceInfo> getTraceInfo(String interfaceName) {
        return Optional.ofNullable(registry.get(interfaceName));
    }

}