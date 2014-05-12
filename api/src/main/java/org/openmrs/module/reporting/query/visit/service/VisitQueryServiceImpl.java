package org.openmrs.module.reporting.query.visit.service;

import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.springframework.transaction.annotation.Transactional;

public class VisitQueryServiceImpl extends BaseDefinitionService<VisitQuery> implements VisitQueryService {

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#getDefinitionType()
     */
    @Transactional(readOnly = true)
    public Class<VisitQuery> getDefinitionType() {
        return VisitQuery.class;
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * @should evaluate an encounter query
     */
    @Transactional(readOnly = true)
    public VisitQueryResult evaluate(VisitQuery query, EvaluationContext context) throws EvaluationException {
        return (VisitQueryResult)super.evaluate(query, context);
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.parameter.Mapped, EvaluationContext)
     */
    @Transactional(readOnly = true)
    public VisitQueryResult evaluate(Mapped<? extends VisitQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
        return (VisitQueryResult)super.evaluate(mappedQuery, context);
    }
}
