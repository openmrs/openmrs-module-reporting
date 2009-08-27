/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.evaluation.parameter;

import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.util.DateUtil;

/**
 * Pre-defined parameters that are available for evaluation
 */
public enum ParameterConstants {
	
	NOW ("now") {
		public Object getParameterValue(EvaluationContext context) {
			return context.getEvaluationDate();
		}
	},
	
	START_OF_TODAY ("start_of_today") {
		public Object getParameterValue(EvaluationContext context) {
			return DateUtil.getStartOfDay(context.getEvaluationDate());
		}
	},
	
	END_OF_TODAY ("end_of_today") {
		public Object getParameterValue(EvaluationContext context) {
			return DateUtil.getEndOfDay(context.getEvaluationDate());
		}
	},
	
	START_OF_LAST_MONTH ("start_of_last_month") {
		public Object getParameterValue(EvaluationContext context) {
			return DateUtil.getStartOfMonth(context.getEvaluationDate(), -1);
		}
	},
	
	END_OF_LAST_MONTH ("end_of_last_month") {
		public Object getParameterValue(EvaluationContext context) {
			return DateUtil.getEndOfMonth(context.getEvaluationDate(), -1);
		}
	};
	
	/**
	 * Constructor
	 * @param parameterName
	 */
	ParameterConstants(String parameterName) {
		this.parameterName = parameterName;
	}
	
	/**
	 * Property representing the parameter name
	 */
	private final String parameterName;
	
	/**
	 * Return the property name
	 * @return
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Method which returns the value for the given property name, based on the passed evaluation context
	 * @param context
	 * @return parameter value
	 */
	public abstract Object getParameterValue(EvaluationContext context);
}
