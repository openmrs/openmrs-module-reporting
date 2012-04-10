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
package org.openmrs.module.reporting.db.hibernate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.reporting.cohort.definition.DefinitionTag;
import org.openmrs.module.reporting.db.DefinitionDAO;

/**
 * Hibernate implementations of {@link DefinitionDAO}
 */
public class HibernateDefinitionDAO implements DefinitionDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateDefinitionDAO.class);
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * @return the sessionFactory
	 */
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.reporting.db.DefinitionDAO#getDefinitionTags(String, String)
	 */
	@SuppressWarnings("unchecked")
	public List<DefinitionTag> getDefinitionTags(String tag, String definitionUuid) {
		Criteria criteria = getSession().createCriteria(DefinitionTag.class);
		if (!StringUtils.isBlank(tag))
			criteria.add(Restrictions.ilike("tag", tag));
		if (!StringUtils.isBlank(definitionUuid))
			criteria.add(Restrictions.ilike("definitionUuid", definitionUuid));
		
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.module.reporting.db.DefinitionDAO#saveDefinitionTag(org.openmrs.module.reporting.cohort.definition.DefinitionTag)
	 */
	public DefinitionTag saveDefinitionTag(DefinitionTag definitionTag) {
		getSession().save(definitionTag);//shouldn't really be updating
		return definitionTag;
	}
	
	/**
	 * @see org.openmrs.module.reporting.db.DefinitionDAO#deleteDefinitionTag(java.lang.String,
	 *      java.lang.String)
	 */
	public void deleteDefinitionTag(String uuid, String tag) {
		getSession().createQuery("DELETE DefinitionTag dt WHERE dt.definitionUuid =:definitionUuid AND dt.tag =:tag")
		        .setString("definitionUuid", uuid).setString("tag", tag).executeUpdate();
	}
	
	/**
	 * Convenience method that gets the current hibernate session
	 * 
	 * @return hibernate session
	 */
	protected Session getSession() {
		return getSessionFactory().getCurrentSession();
	}
	
	/**
	 * @see org.openmrs.module.reporting.db.DefinitionDAO#checkIfTagExists(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean checkIfTagExists(String definitionUuid, String tag) {
		Criteria criteria = getSession().createCriteria(DefinitionTag.class);
		criteria.add(Restrictions.ilike("tag", tag));
		criteria.add(Restrictions.like("definitionUuid", definitionUuid));
		
		return criteria.list().size() > 0;
	}
}
