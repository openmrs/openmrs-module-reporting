package org.openmrs.module.reporting.report.renderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 *
 */
public class SimpleHtmlReportRendererTest extends BaseModuleContextSensitiveTest {

    /**
     * @throws Exception 
     * @see {@link SimpleHtmlReportRenderer#render(ReportData,String,OutputStream)}
     */
    @Test
    @Verifies(value = "should render ReportData to an html file", method = "render(ReportData,String,OutputStream)")
    public void render_shouldRenderReportDataToAnHtmlFile() throws Exception {        

    	// Create and evaluate a report definition
    	EvaluationContext evalContext = new EvaluationContext();
        PatientDataSetDefinition dataSetDefinition = new PatientDataSetDefinition();
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.addDataSetDefinition(dataSetDefinition, ParameterizableUtil.createParameterMappings(""));
        ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, evalContext);
        
        // Render the report data as HTML
        String filename = "deleteMe.html";
        SimpleHtmlReportRenderer renderer = new SimpleHtmlReportRenderer();
        File file = new File(filename);
        FileOutputStream fos = null;
        try { 
	        fos = new FileOutputStream(file);
	        renderer.render(reportData, null, fos);
        } catch (IOException e) { 
        	throw new APIException("Could not write contents of report to file " + filename);
        } finally { 
	        if (fos != null) fos.flush();
	        if (fos != null) fos.close();
        }
    }
}