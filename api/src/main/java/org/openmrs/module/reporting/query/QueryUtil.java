/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;

/**
 * Utilities relevant to the Query package
 */
public class QueryUtil {
	
	protected static Log log = LogFactory.getLog(QueryUtil.class);
	
	/**
	 * @return an IdSet that contains only those members that exist in all passed sets
	 * if any of the passed sets are null, this returns null
	 */
	public static <T extends OpenmrsObject> IdSet<T> intersect(IdSet<T>... sets) {
		IdSet<T> ret = null;
		if (sets != null && sets.length > 0) {
			for (int i=0; i<sets.length; i++) {
				if (sets[i] == null) {
					return null;
				}
				if (ret == null) {
					ret = sets[0].clone();
				}
				else {
				    ret.retainAll(sets[i]);
				}
			}
		}
		return ret;
	}
	
	/**
	 * @return an IdSet that contains only those members that exist in all passed sets
	 * if any of the passed sets are null, they are ignored
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IdSet> T intersectNonNull(T... sets) {
		T ret = null;
		if (sets != null && sets.length > 0) {
			for (int i=0; i<sets.length; i++) {
				if (sets[i] != null) {
					if (ret == null) {
						ret = (T) sets[i].clone();
					}
					else {
                        ret.retainAll(sets[i]);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @return an IdSet that contains all members that exist in any of the passed sets
	 * if any of the passed sets are null, they are ignored
	 */
	public static <T extends OpenmrsObject> IdSet<T> union(IdSet<T>... sets) {
		IdSet<T> ret = null;
		if (sets != null && sets.length > 0) {
			for (int i=0; i<sets.length; i++) {
				if (sets[i] != null) {
					if (ret == null) {
						ret = sets[0].clone();
					}
					else {
                        ret.addAll(sets[i]);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * @return an IdSet that is the first idset minus the idsets that follow it
	 */
	public static <T extends OpenmrsObject> IdSet<T> subtract(IdSet<T> base, IdSet<T>... toSubtract) {
		IdSet<T> ret = base.clone();
		for (IdSet<T> s : toSubtract) {
            ret.removeAll(s);
		}
		return ret;
	}
}
