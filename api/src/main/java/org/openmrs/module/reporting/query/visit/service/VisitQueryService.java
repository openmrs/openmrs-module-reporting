package org.openmrs.module.reporting.query.visit.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate Visit Queries
 */
@Transactional
public interface VisitQueryService extends DefinitionService<VisitQuery> {

    /**
     * @see DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @Transactional(readOnly = true)
    public VisitQueryResult evaluate(VisitQuery query, EvaluationContext context) throws EvaluationException;

    /**
     * @see DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.parameter.Mapped, EvaluationContext)
     */
    @Transactional(readOnly = true)
    public VisitQueryResult evaluate(Mapped<? extends VisitQuery> mappedQuery, EvaluationContext context) throws EvaluationException;


}
