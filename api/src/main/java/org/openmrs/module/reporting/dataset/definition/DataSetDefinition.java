/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Implementations of this interface describe the metadata that can be evaluated to produce a {@link DataSet}. 
 * This is one of three interfaces that work together to define and evaluate an OpenMRS DataSet. 
 * An implementation of {@link DataSetEvaluator} transforms one or more implementations of {@link DataSetDefinition}
 * to produce a specific type of {@link DataSet}.
 * @see DataSetEvaluator
 * @see DataSet
 */
public interface DataSetDefinition extends Definition {

}	
