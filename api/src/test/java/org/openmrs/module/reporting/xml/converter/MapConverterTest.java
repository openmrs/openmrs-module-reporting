package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

public class MapConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/map.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.parameters.size(), is(2));
        Assert.assertThat(sample.parameters.get("p1").getName(), is("startDate"));
        Assert.assertThat(sample.parameters.get("p2").getName(), is("endDate"));
    }

    class Sample {
        public Map<String, Parameter> parameters;
    }
}
