package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class SqlDataSetConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/sqlDataSet.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        SqlDataSetDefinition p = (SqlDataSetDefinition)xstream.fromXML(getXml());
        Assert.assertThat(p.getName(), is("Patients Created During Period"));
        Assert.assertTrue(p.getSqlQuery().contains("pn.given_name, pn.family_name"));
        Assert.assertThat(p.getParameters().size(), is(2));
        Assert.assertThat(p.getParameters().get(0).getLabel(), is("Start Date"));
        Assert.assertThat(p.getParameters().get(1).getLabel(), is("End Date"));
    }
}
