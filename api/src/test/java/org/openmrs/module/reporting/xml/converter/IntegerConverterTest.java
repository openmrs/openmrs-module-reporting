package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class IntegerConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/integer.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.integerAttribute, is(10));
        Assert.assertThat(sample.intAttribute, is(20));
        Assert.assertThat(sample.integerElement, is(30));
        Assert.assertThat(sample.intElement, is(40));
    }

    class Sample {
        public Integer integerElement;
        public int intElement;
        public Integer integerAttribute;
        public int intAttribute;
    }
}