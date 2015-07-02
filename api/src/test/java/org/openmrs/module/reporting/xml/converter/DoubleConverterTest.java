package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class DoubleConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/double.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.doubleObjectAttribute, is(10.1));
        Assert.assertThat(sample.doublePrimitiveAttribute, is(20.2));
        Assert.assertThat(sample.doubleObjectElement, is(30.3));
        Assert.assertThat(sample.doublePrimitiveElement, is(40.4));
    }

    class Sample {
        public Double doubleObjectElement;
        public double doublePrimitiveElement;
        public Double doubleObjectAttribute;
        public double doublePrimitiveAttribute;
    }
}