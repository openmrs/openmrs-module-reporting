/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.ExcelUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Supports rendering a report to Excel
 */
public class ExcelTemplateRendererTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRenderToExcelTemplate() throws Exception {
		
		// We first set up a report with 2 indicators, numbered 1 and 2
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("Females");
		females.setFemaleIncluded(true);
		
		ReportDefinition report = new ReportDefinition();
		report.setName("Test Report");
		report.addParameter(new Parameter("programState", "Which program state?", ProgramWorkflowState.class));
		
		CohortCrossTabDataSetDefinition genderDsd = new CohortCrossTabDataSetDefinition();
		genderDsd.addColumn("males", males, null);
		genderDsd.addColumn("females", females, null);
		report.addDataSetDefinition("genders", genderDsd, null);
		
		SimplePatientDataSetDefinition allPatients = new SimplePatientDataSetDefinition("allPatients", "");
		allPatients.addPatientProperty("patientId");
		allPatients.addPatientProperty("gender");
		allPatients.addPatientProperty("birthdate");
		report.addDataSetDefinition("allPatients", allPatients, null);
		
		SqlDataSetDefinition femaleDetails = new SqlDataSetDefinition();
		femaleDetails.setName("femaleDetails");
		femaleDetails.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = 'F'");
		report.addDataSetDefinition("femalePatients", femaleDetails, null);
		
		SqlDataSetDefinition maleDetails = new SqlDataSetDefinition();
		maleDetails.setName("maleDetails");
		maleDetails.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = 'M'");
		report.addDataSetDefinition("malePatients", maleDetails, null);
		
		// Next, we set up the ReportDesign and ReportDesignResource files for the renderer
		
		final ReportDesign design = new ReportDesign();
		design.setName("TestDesign");
		design.setReportDefinition(report);
		design.setRendererType(ExcelTemplateRenderer.class);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:6-8,dataset:allPatients | sheet:2,column:4,dataset:malePatients | sheet:3,dataset:allPatients");

		design.setProperties(props);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/reporting/report/renderer/ExcelTemplateRendererTest.xls");
		resource.setContents(IOUtils.toByteArray(is));
		IOUtils.closeQuietly(is);
		design.addResource(resource);

		// For now, we need this little magic to simulate what would happen if this were all stored in the database via the UI
		
		ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		
		// We construct an EvaluationContext (in this case the parameters aren't used, but included here for reference)
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("programState", Context.getProgramWorkflowService().getStateByUuid("92584cdc-6a20-4c84-a659-e035e45d36b0"));
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "excelTemplateRendererTest.xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		renderer.render(data, "xxx:xls", fos);
		fos.close();
	}

    @Test
    public void shouldLocalizeColumnHeaders() throws Exception {
        testLocalization("reporting.test.", "en", "EMR ID", "GENDER", "Date of Birth");
	    testLocalization("reporting.test.", "fr", "ID DE EMR", "Sexe", "Date de naissance");
        testLocalization("reporting.test.", "", "EMR ID", "GENDER", "Date of Birth");
        testLocalization("", "", "PID", "GENDER", "DOB");
    }

    public void testLocalization(String prefix, String locale, String emrIdVal, String genderVal, String dobVal) throws Exception {

	    ReportDefinition rd = new ReportDefinition();
        SqlDataSetDefinition testDataSet = new SqlDataSetDefinition();
        testDataSet.setSqlQuery("select p.patient_id as PID, n.gender as GENDER, n.birthdate as DOB from patient p, person n where p.patient_id = n.person_id and n.gender = 'M'");
        rd.addDataSetDefinition("dataset", testDataSet, null);

        // Next, we set up the ReportDesign and ReportDesignResource files for the renderer

        final ReportDesign design = new ReportDesign();
        design.setName("TestDesign");
        design.setReportDefinition(rd);
        design.setRendererType(ExcelTemplateRenderer.class);

        ReportDesignResource resource = new ReportDesignResource();
        resource.setName("template.xls");
        InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/reporting/report/renderer/ExcelTemplateLocalizeLabelsTest.xls");
        resource.setContents(IOUtils.toByteArray(is));
        IOUtils.closeQuietly(is);
        design.addResource(resource);

        Properties props = new Properties();
        props.put("columnTranslationPrefix", prefix);
        props.put("columnTranslationLocale", locale);
        design.setProperties(props);

        // For now, we need this little magic to simulate what would happen if this were all stored in the database via the UI

        ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
            public ReportDesign getDesign(String argument) {
                return design;
            }
        };

        // We construct an EvaluationContext (in this case the parameters aren't used, but included here for reference)

        EvaluationContext context = new EvaluationContext();
        ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

        MutableMessageSource messageSource = Context.getMessageSourceService().getActiveMessageSource();
        messageSource.addPresentation(new PresentationMessage("reporting.test.PID", Locale.ENGLISH, "EMR ID", ""));
        messageSource.addPresentation(new PresentationMessage("reporting.test.dataset.DOB", Locale.ENGLISH, "Date of Birth", ""));
        messageSource.addPresentation(new PresentationMessage("reporting.test.PID", Locale.FRENCH, "ID DE EMR", ""));
        messageSource.addPresentation(new PresentationMessage("reporting.test.GENDER", Locale.FRENCH, "Sexe", ""));
        messageSource.addPresentation(new PresentationMessage("reporting.test.dataset.DOB", Locale.FRENCH, "Date de naissance", ""));

        ByteArrayOutputStream reportBaos = new ByteArrayOutputStream(1024);
        renderer.render(data, "xxx:xls", reportBaos);
        IOUtils.closeQuietly(reportBaos);

        Workbook wb = ExcelUtil.loadWorkbookFromInputStream(new ByteArrayInputStream(reportBaos.toByteArray()));
        Sheet sheet = wb.getSheet("TestLabels");

        List<String> cellsFound = new ArrayList<String>();

        for (Iterator<Row> ri = sheet.rowIterator(); ri.hasNext();) {
            Row row = ri.next();
            for (Iterator<Cell> ci = row.cellIterator(); ci.hasNext();) {
                Cell cell = ci.next();
                Object contents = ExcelUtil.getCellContents(cell);
                if (!ObjectUtil.isNull(contents)) {
                    cellsFound.add(contents.toString());
                }
            }
        }

        Assert.assertEquals(3, cellsFound.size());
        Assert.assertEquals(emrIdVal, cellsFound.get(0));
        Assert.assertEquals(genderVal, cellsFound.get(1));
        Assert.assertEquals(dobVal, cellsFound.get(2));
    }
}