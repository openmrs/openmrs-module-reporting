package org.openmrs.module.reporting.report.renderer;

import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReportDesignRendererTest extends BaseModuleContextSensitiveTest {

    @Test
    public void getFilenameBase_shouldHaveSensibleDefaultIfNotSpecifiedAsDesignProperty() throws Exception {
        ReportDefinition definition = new ReportDefinition();
        definition.setName("Test Report");

        ReportRequest request = new ReportRequest();
        request.setEvaluateStartDatetime(DateUtil.parseYmdhms("2014-07-01 18:30:15"));
        request.setReportDefinition(Mapped.noMappings(definition));
        request.setRenderingMode(new RenderingMode());

        String filenameBase = new TestReportDesignRenderer().getFilenameBase(request);
        assertThat(filenameBase, is("Test Report_2014-07-01_18:30:15"));
    }

    @Test
    public void getFilenameBase_shouldBeBasedOnDesignProperty() throws Exception {
        ReportDefinition definition = new ReportDefinition();
        definition.setName("Test Report");

        ReportRequest request = new ReportRequest();
        request.setEvaluateStartDatetime(DateUtil.parseYmdhms("2014-07-01 18:30:15"));
        request.setReportDefinition(Mapped.noMappings(definition));
        request.setRenderingMode(new RenderingMode());

        ReportDesign design = new ReportDesign();
        design.addPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, "{{formatDate request.evaluateStartDatetime \"yyyyMMdd\"}}-{{request.reportDefinition.parameterizable.name}}");

        TestReportDesignRenderer renderer = new TestReportDesignRenderer();
        renderer.setDesign(design);
        String filenameBase = renderer.getFilenameBase(request);
        assertThat(filenameBase, is("20140701-Test Report"));
    }

    /**
     * Since this class is abstract, we need a concrete class to test with
     */
    private class TestReportDesignRenderer extends ReportDesignRenderer {

        /**
         * For testing, we allow a specific report design to be injected
         */
        private ReportDesign design = new ReportDesign();

        public void setDesign(ReportDesign design) {
            this.design = design;
        }

        @Override
        public ReportDesign getDesign(String argument) {
            return design;
        }

        @Override
        public String getRenderedContentType(ReportRequest request) {
            return null;
        }

        @Override
        public String getFilename(ReportRequest request) {
            return null;
        }

        @Override
        public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
        }

    }

}