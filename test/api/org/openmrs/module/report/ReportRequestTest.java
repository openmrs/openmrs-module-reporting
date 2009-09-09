package org.openmrs.module.report;


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

public class ReportRequestTest {
	
	/**
	 * @see {@link ReportRequest#compareTo(ReportRequest)}
	 * 
	 */
	@Test
	@Verifies(value = "should compare by priority", method = "compareTo(ReportRequest)")
	public void compareTo_shouldCompareByPriority() throws Exception {
		ReportRequest first = new ReportRequest();
		ReportRequest second = new ReportRequest();
		second.setPriority(ReportRequest.Priority.HIGH);
		Assert.assertTrue(first.compareTo(second) > 0);
		Assert.assertTrue(second.compareTo(first) < 0);
	}
	
	/**
	 * @see {@link ReportRequest#compareTo(ReportRequest)}
	 * 
	 */
	@Test
	@Verifies(value = "should compare by request date when priority is the same", method = "compareTo(ReportRequest)")
	public void compareTo_shouldCompareByRequestDateWhenPriorityIsTheSame() throws Exception {
		Date sooner = new Date();
		Date later = new Date(sooner.getTime() + 1000);
		ReportRequest first = new ReportRequest();
		first.setRequestDate(sooner);
		ReportRequest second = new ReportRequest();
		second.setRequestDate(later);
		Assert.assertTrue(first.compareTo(second) < 0);
		Assert.assertTrue(second.compareTo(first) > 0);
	}
}