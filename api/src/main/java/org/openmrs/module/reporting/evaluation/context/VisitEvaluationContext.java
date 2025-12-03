/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.context;

import org.openmrs.OpenmrsData;
import org.openmrs.Visit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.visit.VisitIdSet;

import java.util.Date;
import java.util.Map;

/**
 * Extends the patient-based EvaluationContext to add an additional Visit filter for use within Visit specific queries and data extraction
 * Note that this cache is cleared whenever any changes are made to baseVisits
 */
public class VisitEvaluationContext extends EvaluationContext {

    // ***** PROPERTIES *****

    private VisitIdSet baseVisits;

    // ***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public VisitEvaluationContext() {
        super();
    }

    /**
     * Constructor which sets the Evaluation Date to a particular date
     */
    public VisitEvaluationContext(Date evaluationDate) {
        super(evaluationDate);
    }

    /**
     * Constructs a new VisitEvaluationContext given the passed EvaluationContext and VisitIdSet
     */
    public VisitEvaluationContext(EvaluationContext context, VisitIdSet baseVisits) {
        super(context);
        this.baseVisits = baseVisits;
    }

    /**
     * Constructs a new EvaluationContext given the passed EvaluationContext
     */
    public VisitEvaluationContext(VisitEvaluationContext context) {
        super(context);
        this.baseVisits = context.baseVisits;
    }

    // *******************
    // INSTANCE METHODS
    // *******************

    @Override
    public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
        Map<Class<? extends OpenmrsData>, IdSet<?>> ret = super.getAllBaseIdSets();
        if (getBaseVisits() != null) {
            ret.put(Visit.class, getBaseVisits());
        }
        return ret;
    }

    /**
     * @return a shallow copy of the current instance
     */
    @Override
    public VisitEvaluationContext shallowCopy() {
        return new VisitEvaluationContext(this);
    }

    /**
     * @return the baseVisits
     */
    public VisitIdSet getBaseVisits() {
        return baseVisits;
    }

    /**
     * @param baseVisits the baseVisits to set
     */
    public void setBaseVisits(VisitIdSet baseVisits) {
        clearCache();
        this.baseVisits = baseVisits;
    }



}
