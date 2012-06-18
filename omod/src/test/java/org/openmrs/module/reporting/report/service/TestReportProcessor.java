package org.openmrs.module.reporting.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.processor.ReportProcessor;
import org.springframework.stereotype.Component;

@Ignore
@Component
public class TestReportProcessor implements ReportProcessor {

	
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	public List<String> getConfigurationPropertyNames() {
		return new ArrayList<String>();
	}
	
	/**
	 * This adds an error message to the report -- I just want the processor to do something, so i know that it ran
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration) {
		//using error message as a very simple way to pass something back to ReportServiceTest test cases.
		report.setErrorMessage("TestReportProcessor.process was called corretly.");
	}
}

