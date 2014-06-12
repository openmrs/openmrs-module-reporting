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
package org.openmrs.module.reporting.evaluation;

import org.apache.commons.codec.digest.DigestUtils;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Encapsulates a Set of Integer ids, along with an evaluationId
 */
public class EvaluationIdSet extends TreeSet<Integer> {

	// *******************
	// PROPERTIES
	// *******************

    private String evaluationId;

	// *******************
	// CONSTRUCTORS
	// *******************

	public EvaluationIdSet(String evaluationId, Collection<Integer> ids) {
		super(ids);
		this.evaluationId = evaluationId;
	}

	// *******************
	// INSTANCE METHODS
	// *******************

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(evaluationId).append("[");
		for (Iterator<Integer> i = iterator(); i.hasNext();) {
			sb.append(i.next());
			sb.append(i.hasNext() ? "," : "]");
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int result = 23;
		result = 37 * result + super.hashCode();
		result = 37 * result + evaluationId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EvaluationIdSet) {
			EvaluationIdSet that = (EvaluationIdSet)o;
			return super.equals(o) && OpenmrsUtil.nullSafeEquals(this.getEvaluationId(), that.getEvaluationId());
		}
		return false;
	}

	/**
	 * @return an evaluation key that consistently represents this combination of evaluationId and member ids
	 */
	public String getEvaluationKey() {
		return DigestUtils.shaHex(this.toString());
	}

	// *******************
	// PROPERTY ACCESS
	// *******************

	public String getEvaluationId() {
		return evaluationId;
	}

	public void setEvaluationId(String evaluationId) {
		this.evaluationId = evaluationId;
	}
}
