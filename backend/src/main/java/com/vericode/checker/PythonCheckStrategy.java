package com.vericode.checker;

public class PythonCheckStrategy extends RemoteCheckTemplate {

    private static final String DEFAULT_URL = "http://localhost:5001/check";

    private final String checkerUrl;

    public PythonCheckStrategy() {
        this(DEFAULT_URL);
    }

    public PythonCheckStrategy(String checkerUrl) {
        this.checkerUrl = checkerUrl;
    }

    @Override
    protected String getServiceUrl() {
        return checkerUrl;
    }

    @Override
    protected String getServiceName() {
        return "Python";
    }
}
