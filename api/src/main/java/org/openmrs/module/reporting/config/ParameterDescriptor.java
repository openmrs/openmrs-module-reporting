package org.openmrs.module.reporting.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Properties;

public class ParameterDescriptor {

    @JsonProperty
    private String key;

    @JsonProperty
    private String type;

    @JsonProperty
    private String label;

    @JsonProperty
    private String value;

    @JsonProperty
    private Boolean required;

    @JsonProperty
    private Properties widgetConfiguration;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Properties getWidgetConfiguration() {
        return widgetConfiguration;
    }

    public void setWidgetConfiguration(Properties widgetConfiguration) {
        this.widgetConfiguration = widgetConfiguration;
    }
}
