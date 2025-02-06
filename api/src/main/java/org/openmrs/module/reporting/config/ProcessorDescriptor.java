package org.openmrs.module.reporting.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class ProcessorDescriptor {

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private Boolean runOnSuccess;

    @JsonProperty
    private Boolean runOnError;

    @JsonProperty
    private String processorMode;

    @JsonProperty
    private Map<String, String> configuration;

    public ProcessorDescriptor() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRunOnSuccess() {
        return runOnSuccess;
    }

    public void setRunOnSuccess(Boolean runOnSuccess) {
        this.runOnSuccess = runOnSuccess;
    }

    public Boolean getRunOnError() {
        return runOnError;
    }

    public void setRunOnError(Boolean runOnError) {
        this.runOnError = runOnError;
    }

    public String getProcessorMode() {
        return processorMode;
    }

    public void setProcessorMode(String processorMode) {
        this.processorMode = processorMode;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }
}
