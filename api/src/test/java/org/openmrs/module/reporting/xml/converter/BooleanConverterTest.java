package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import static org.hamcrest.CoreMatchers.is;

public class BooleanConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/boolean.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());

        Assert.assertThat(sample.booleanObjectAttribute, is(true));
        Assert.assertThat(sample.booleanPrimitiveAttribute, is(true));
        Assert.assertThat(sample.booleanObjectElement, is(true));
        Assert.assertThat(sample.booleanPrimitiveElement, is(true));
    }

    class Sample {
        public Boolean booleanObjectElement;
        public boolean booleanPrimitiveElement;
        public Boolean booleanObjectAttribute;
        public boolean booleanPrimitiveAttribute;
    }
}