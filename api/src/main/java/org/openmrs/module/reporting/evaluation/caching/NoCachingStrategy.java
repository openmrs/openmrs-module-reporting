/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.caching;

import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Strategy that is intended to represent no caching enabled
 * for a particular class
 * @see Caching
 */
public class NoCachingStrategy implements CachingStrategy  {
	
	/**
	 * Implementation that returns a null cache key
	 */
	public String getCacheKey(Definition definition, EvaluationContext context) {
		return null;
	}
}
