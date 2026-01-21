package com.eacore.trace.dto;

public class TraceInfo {

    private final String msgno;
    private final String ip;

    public TraceInfo(String msgno, String ip) {
        this.msgno = msgno;
        this.ip = ip;
    }

    // No setters, making the object immutable after creation

    public String getMsgno() {
        return msgno;
    }

    public String getIp() {
        return ip;
    }
}