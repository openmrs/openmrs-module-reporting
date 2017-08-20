package org.openmrs.module.reporting.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.cohort.PatientIdSet;

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
				    if (ret instanceof Cohort && sets[i] instanceof Cohort) {
                        Cohort cohortRet = (Cohort)ret;
                        Cohort cohortInt = (Cohort)sets[i];
                        Cohort union = Cohort.intersect(cohortRet, cohortInt);
                        ret = (IdSet<T>)new PatientIdSet(union.getMemberIds());
                    }
                    else {
                        ret.getMemberIds().retainAll(sets[i].getMemberIds());
                    }
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
	public static <T extends IdSet<?>> T intersectNonNull(T... sets) {
		T ret = null;
		if (sets != null && sets.length > 0) {
			for (int i=0; i<sets.length; i++) {
				if (sets[i] != null) {
					if (ret == null) {
						ret = (T) sets[i].clone();
					}
					else {
                        if (ret instanceof Cohort && sets[i] instanceof Cohort) {
                            Cohort cohortRet = (Cohort)ret;
                            Cohort cohortInt = (Cohort)sets[i];
                            Cohort union = Cohort.intersect(cohortRet, cohortInt);
                            ret = (T)new PatientIdSet(union.getMemberIds());
                        }
                        else {
                            ret.getMemberIds().retainAll(sets[i].getMemberIds());
                        }
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
                        if (ret instanceof Cohort && sets[i] instanceof Cohort) {
                            Cohort cohortRet = (Cohort)ret;
                            Cohort cohortInt = (Cohort)sets[i];
                            Cohort union = Cohort.union(cohortRet, cohortInt);
                            ret = (IdSet<T>)new PatientIdSet(union.getMemberIds());
                        }
                        else {
                            ret.getMemberIds().addAll(sets[i].getMemberIds());
                        }
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
            if (ret instanceof Cohort && s instanceof Cohort) {
                Cohort cohortRet = (Cohort)ret;
                Cohort cohortInt = (Cohort)s;
                Cohort union = Cohort.subtract(cohortRet, cohortInt);
                ret = (IdSet<T>)new PatientIdSet(union.getMemberIds());
            }
            else {
                ret.getMemberIds().removeAll(s.getMemberIds());
            }

		}
		return ret;
	}
}
