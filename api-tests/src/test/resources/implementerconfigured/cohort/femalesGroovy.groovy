package implementerconfigured.cohort


import org.openmrs.module.reporting.cohort.EvaluatedCohort
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition
import org.openmrs.module.reporting.evaluation.EvaluationContext

class FemaleCohortDefinition extends EvaluatableCohortDefinition {

    @Override
    EvaluatedCohort evaluate(EvaluationContext evalContext) {
        def cohort = new EvaluatedCohort(this, evalContext)
        return cohort
    }

}
