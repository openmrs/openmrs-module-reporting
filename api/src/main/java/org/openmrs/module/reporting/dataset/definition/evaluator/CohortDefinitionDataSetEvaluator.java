package org.openmrs.module.reporting.dataset.definition.evaluator;

/**
 *
 */

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortDefinitionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = CohortDefinitionDataSetDefinition.class)
public class CohortDefinitionDataSetEvaluator implements DataSetEvaluator {

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        CohortDefinitionDataSetDefinition dsd = (CohortDefinitionDataSetDefinition) dataSetDefinition;
        MapDataSet results = new MapDataSet(dsd, evalContext);

        for (CohortDefinitionDataSetDefinition.CohortDefinitionColumn col : dsd.getColumns()) {
            Mapped<? extends CohortDefinition> mcd = col.getCohortDefinition();
            EvaluatedCohort cohort = cohortDefinitionService.evaluate(mcd, evalContext);
            results.addData(col, cohort);
        }
        return results;
    }

}
