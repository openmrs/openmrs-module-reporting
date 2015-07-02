package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class ListConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/list.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.parameters.size(), is(2));
        Assert.assertThat(sample.parameters.get(0).getName(), is("startDate"));
        Assert.assertThat(sample.parameters.get(1).getName(), is("endDate"));
    }

    class Sample {
        public List<Parameter> parameters;
    }
}
