package org.openmrs.module.cohort.query.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;

public class CohortQueryServiceImpl  extends BaseOpenmrsService implements CohortQueryService {

    protected final Log log = LogFactory.getLog(getClass());

    protected CohortQueryDAO dao;
    
    public void setCohortQueryDAO(CohortQueryDAO dao) {
        this.dao = dao;
    }
    
    
    public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startDate, Date endDate) {     	
    	return dao.getPatientsHavingStartedPrograms(programs, startDate, endDate);
    }
    
 
    
}
