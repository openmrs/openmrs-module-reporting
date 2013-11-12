package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class CohortDefinitionDataSetDefinition extends BaseDataSetDefinition {

    @ConfigurationProperty
    List<CohortDefinitionColumn> columns;

    public List<CohortDefinitionColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<CohortDefinitionColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(String name, String label, CohortDefinition cohortDefinition) {
        addColumn(name, label, noMappings(cohortDefinition));
    }

    public void addColumn(String name, String label, Mapped<? extends CohortDefinition> mappedCohortDefinition) {
        if (columns == null) {
            columns = new ArrayList<CohortDefinitionColumn>();
        }
        columns.add(new CohortDefinitionColumn(name, label, mappedCohortDefinition));
    }

    public class CohortDefinitionColumn extends DataSetColumn {

        public static final long serialVersionUID = 1L;

        private Mapped<? extends CohortDefinition> cohortDefinition;

        public CohortDefinitionColumn(String name, String label, Mapped<? extends CohortDefinition> cohortDefinition) {
            super(name, label, EvaluatedCohort.class);
            this.cohortDefinition = cohortDefinition;
        }

        public Mapped<? extends CohortDefinition> getCohortDefinition() {
            return cohortDefinition;
        }

    }

    private Mapped<CohortDefinition> noMappings(CohortDefinition cohortDefinition) {
        return new Mapped<CohortDefinition>(cohortDefinition, new HashMap<String, Object>());
    }

}
