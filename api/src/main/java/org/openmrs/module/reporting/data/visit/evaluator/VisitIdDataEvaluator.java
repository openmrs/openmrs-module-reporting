/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;

/**
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=VisitIdDataDefinition.class, order=50)
public class VisitIdDataEvaluator extends VisitPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "visitId";
    }
}
