package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MappedParametersCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty
    private Mapped<CohortDefinition> wrapped;

    public MappedParametersCohortDefinition() {
    }

    public MappedParametersCohortDefinition(CohortDefinition toWrap, Map<String, String> renamedParameters) {
        Map<String, Object> mappings = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : renamedParameters.entrySet()) {
            String originalParameterName = entry.getKey();
            String newParameterName = entry.getValue();

            mappings.put(originalParameterName, "${" + newParameterName + "}");

            Parameter originalParameter = toWrap.getParameter(originalParameterName);
            Parameter newParameter = new Parameter();
            newParameter.setName(newParameterName);
            newParameter.setLabel(originalParameter.getLabel());
            newParameter.setType(originalParameter.getType());
            newParameter.setCollectionType(originalParameter.getCollectionType());
            newParameter.setDefaultValue(originalParameter.getDefaultValue());
            newParameter.setWidgetConfiguration(originalParameter.getWidgetConfiguration());
            addParameter(newParameter);
        }
        wrapped = new Mapped<CohortDefinition>(toWrap, mappings);
    }

    public Mapped<CohortDefinition> getWrapped() {
        return wrapped;
    }

    public void setWrapped(Mapped<CohortDefinition> wrapped) {
        this.wrapped = wrapped;
    }
}
