package org.openmrs.module.reporting.config;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ReportDescriptor {

    @JsonProperty
    private String key;

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<ParameterDescriptor> parameters;

    @JsonProperty
    private List<DataSetDescriptor> datasets;

    @JsonProperty
    private List<DesignDescriptor> designs;

    @JsonProperty
    private Map<String, Object> config;

    // utility property for storing the path to this descriptor on the file system so we know where to look for related files
    private File path;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParameterDescriptor> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDescriptor> parameters) {
        this.parameters = parameters;
    }

    public List<DataSetDescriptor> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DataSetDescriptor> datasets) {
        this.datasets = datasets;
    }

    public List<DesignDescriptor> getDesigns() {
        return designs;
    }

    public void setDesigns(List<DesignDescriptor> designs) {
        this.designs = designs;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }
}
