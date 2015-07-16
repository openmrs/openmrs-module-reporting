package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class PatientDataSetDefinitionConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/patientDataSet.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        PatientDataSetDefinition d = serializer.fromXml(PatientDataSetDefinition.class, getXml());
        Assert.assertThat(d.getName(), is("Male Adults on Date"));
        Assert.assertThat(d.getParameters().size(), is(1));
        Assert.assertThat(d.getParameters().get(0).getName(), is("date"));
        Assert.assertThat(d.getRowFilters().size(), is(2));
        Assert.assertTrue(d.getRowFilters().get(0).getParameterizable().getClass() == AgeCohortDefinition.class);
        Assert.assertThat(d.getRowFilters().get(0).getParameterMappings().size(), is(1));
        Assert.assertThat(d.getRowFilters().get(0).getParameterMappings().get("effectiveDate").toString(), is("${date}"));
    }


}
