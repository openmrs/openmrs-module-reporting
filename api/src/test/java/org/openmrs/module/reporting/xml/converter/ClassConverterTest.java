package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import java.util.Date;

public class ClassConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/class.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertTrue(sample.classAttribute == Date.class);
        Assert.assertTrue(sample.classElement == String.class);
    }

    class Sample {
        public Class classAttribute;
        public Class classElement;
    }
}