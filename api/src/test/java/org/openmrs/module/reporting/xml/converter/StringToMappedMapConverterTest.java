package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class StringToMappedMapConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/stringToMappedMap.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        ReportDefinition d = serializer.fromXml(ReportDefinition.class, getXml());
        Assert.assertThat(d.getName(), is("Simple Test Report"));
        Assert.assertThat(d.getParameters().size(), is(0));
        Assert.assertThat(d.getDataSetDefinitions().size(), is(2));
        SqlDataSetDefinition dsd1 = (SqlDataSetDefinition)d.getDataSetDefinitions().get("Num Patients").getParameterizable();
        SqlDataSetDefinition dsd2 = (SqlDataSetDefinition)d.getDataSetDefinitions().get("Num Encounters").getParameterizable();
        Assert.assertThat(dsd1.getSqlQuery(), is("select count(*) from patient where voided = 0"));
        Assert.assertThat(dsd2.getSqlQuery(), is("select count(*) from encounter where voided = 0"));
    }


}
