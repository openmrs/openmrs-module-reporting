package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import static org.hamcrest.CoreMatchers.is;

public class BooleanConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/boolean.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

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