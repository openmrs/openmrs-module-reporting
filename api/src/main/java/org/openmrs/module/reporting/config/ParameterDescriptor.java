package org.openmrs.module.reporting.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class ParameterDescriptor {

    @JsonProperty
    private String key;

    @JsonProperty
    private String type;

    @JsonProperty
    private String label;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
