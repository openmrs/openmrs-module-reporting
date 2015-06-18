package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import static org.hamcrest.CoreMatchers.is;

public class IntegerConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/integer.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());

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