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
package org.openmrs.module.reporting.encounter.query.db.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.encounter.query.db.EncounterQueryDAO;

/**
 * Hibernate specific dao for the {@link EncounterQueryService} All calls should
 * be made on the EncounterQueryService object:
 * Context.getService(EncounterQueryService.class)
 * 
 * @implements EncounterQueryDAO
 * @see EncounterQueryService
 */

public class HibernateEncounterQueryDAO implements EncounterQueryDAO {

	protected static final Log log = LogFactory.getLog(HibernateEncounterQueryDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.reporting.encounter.query.db.EncounterQueryDAO#getEncounters(org.openmrs.Cohort,
	 *      java.util.List, java.util.List, java.util.Date, java.util.Date,
	 *      org.openmrs.module.reporting.common.TimeQualifier)
	 * 
	 *      Caveat for this implementation: For @param
	 *      encounterDatetimeOnOrBefore if time is not set (00:00:00), then time
	 *      is changed to 23:59:59
	 */
	public List<Encounter> getEncounters(Cohort cohort, List<EncounterType> encounterTypes, List<Form> forms,
			Date encounterDatetimeOnOrAfter, Date encounterDatetimeOnOrBefore, TimeQualifier whichEncounterQualifier) {

		if (cohort != null && cohort.size() == 0) {
			return new ArrayList<Encounter>();
		} else {

			// default query
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);

			// this "where clause" is only necessary if patients were passed in
			if (cohort != null)
				criteria.add(Restrictions.in("patientId", cohort.getMemberIds()));

			criteria.add(Restrictions.eq("voided", false));

			if (encounterTypes != null && encounterTypes.size() > 0)
				criteria.add(Restrictions.in("encounterType", encounterTypes));

			if (forms != null && forms.size() > 0)
				criteria.add(Restrictions.in("form", forms));

			if (encounterDatetimeOnOrAfter != null)
				criteria.add(Expression.ge("encounterDatetime", encounterDatetimeOnOrAfter));
			if (encounterDatetimeOnOrBefore != null)
				criteria.add(Expression.le("encounterDatetime", DateUtil.getEndOfDayIfTimeExcluded(encounterDatetimeOnOrBefore)));

			criteria.addOrder(org.hibernate.criterion.Order.asc("patientId"));

			if (whichEncounterQualifier != null && whichEncounterQualifier.equals(TimeQualifier.LAST))
				criteria.addOrder(org.hibernate.criterion.Order.desc("encounterDatetime"));
			else
				criteria.addOrder(org.hibernate.criterion.Order.asc("encounterDatetime"));

			return criteria.list();
		}
	}
}
