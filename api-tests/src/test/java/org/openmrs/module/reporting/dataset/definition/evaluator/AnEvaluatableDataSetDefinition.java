/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Defined in its own file because defining it as an inner class in {@link EvaluatableDataSetEvaluatorTest} throws an
 * internal reporting exception
 */
public class AnEvaluatableDataSetDefinition extends EvaluatableDataSetDefinition {
	
	@Override
	public DataSet evaluate(EvaluationContext evalContext) {
		SimpleDataSet ds = new SimpleDataSet(this, evalContext);
		ds.addColumnValue(0, new DataSetColumn("one", "One", Integer.class), 1);
		return ds;
	}

}
