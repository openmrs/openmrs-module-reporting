package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class StringConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/string.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.stringElement.trim(), is("select count(*) from patient where voided = 0"));
        Assert.assertThat(sample.stringAttribute, is("My Query"));
    }

    class Sample {
        public String stringElement;
        public String stringAttribute;
    }
}