package com.vericode.checker;

public class JSCheckStrategy extends RemoteCheckTemplate {

    private static final String DEFAULT_URL = "http://localhost:5002/check";

    private final String checkerUrl;

    public JSCheckStrategy() {
        this(DEFAULT_URL);
    }

    public JSCheckStrategy(String checkerUrl) {
        this.checkerUrl = checkerUrl;
    }

    @Override
    protected String getServiceUrl() {
        return checkerUrl;
    }

    @Override
    protected String getServiceName() {
        return "JavaScript";
    }
}
