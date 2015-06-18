package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import static org.hamcrest.CoreMatchers.is;

public class StringConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/string.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());

        Assert.assertThat(sample.stringElement.trim(), is("select count(*) from patient where voided = 0"));
        Assert.assertThat(sample.stringAttribute, is("My Query"));
    }

    class Sample {
        public String stringElement;
        public String stringAttribute;
    }
}