package org.openmrs.module.cohort.query.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CohortQueryService extends OpenmrsService {
    
	
	public void setCohortQueryDAO(CohortQueryDAO dao);
	
	/**
	 * Supports the follwoing queries:
	 * 
	 * Ever enrolled in program(s) 
	 * Enrolled in program(s) between dates
	 * Enrolled in program(s) on or after given startDate.
	 * Enrolled in program(s) on or before given endDate.
	 * 
	 * @param program
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startDate, Date endDate);
    
}