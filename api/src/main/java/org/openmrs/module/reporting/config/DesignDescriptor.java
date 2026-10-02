package org.openmrs.module.reporting.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DesignDescriptor {

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private String template;

    @JsonProperty
    private Map<String, String> resources;

    @JsonProperty
    private Map<String, String> properties;

    @JsonProperty
    private List<ProcessorDescriptor> processors;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<ProcessorDescriptor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<ProcessorDescriptor> processors) {
        this.processors = processors;
    }
}
