package org.openmrs.module.reporting.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class DataSetDescriptor {

    @JsonProperty
    private String key;

    @JsonProperty
    private String type;

    @JsonProperty
    private String config;


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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

}
