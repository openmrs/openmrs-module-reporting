/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation;

/**
 * Provides a common interface for representing the result of evaluating 
 * a {@link Definition} in the context of an {@link EvaluationContext}
 */
public interface Evaluated<T extends Definition> {

	/**
	 * Return the {@link Definition} which was Evaluated
	 * @return
	 */
	public T getDefinition();

	/**
	 * @return the {@link EvaluationContext} within which the {@link Definition} was evaluated
	 */
	public EvaluationContext getContext();
}
