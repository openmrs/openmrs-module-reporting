package org.openmrs.module.reporting.report;


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class ReportRequestTest extends BaseModuleContextSensitiveTest {
	
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
		PriorityComparator comparator = new PriorityComparator();
		Assert.assertTrue(comparator.compare(first, second) > 0);
		Assert.assertTrue(comparator.compare(second, first) < 0);
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
		PriorityComparator comparator = new PriorityComparator();
		Assert.assertTrue(comparator.compare(first, second) < 0);
		Assert.assertTrue(comparator.compare(second, first) > 0);
	}
}