package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

public class ListConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/list.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());
        Assert.assertThat(sample.parameters.size(), is(2));
        Assert.assertThat(sample.parameters.get(0).getName(), is("startDate"));
        Assert.assertThat(sample.parameters.get(1).getName(), is("endDate"));
    }

    class Sample {
        public List<Parameter> parameters;
    }
}
