package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import static org.hamcrest.CoreMatchers.is;

public class DoubleConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/double.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());

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