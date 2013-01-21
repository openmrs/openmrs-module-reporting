package org.openmrs.module.reporting.report.service.db;


import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportDAOTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	ReportDAO dao;
	
	/**
	 * @see ReportDAO#purgeReportDesign(ReportDesign)
	 * @verifies purge if report definition cannot be deserialized
	 */
	@Test
	public void purgeReportDesign_shouldPurgeIfReportDefinitionCannotBeDeserialized() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportDAOTest.xml");
		
		ReportDesign reportDesign = dao.getReportDesign(1);
		dao.purgeReportDesign(reportDesign);
		
		assertThat(dao.getReportDesign(1), nullValue());
	}
}