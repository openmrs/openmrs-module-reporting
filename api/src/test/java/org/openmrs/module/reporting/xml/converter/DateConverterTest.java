package org.openmrs.module.reporting.xml.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

public class DateConverterTest extends BaseConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/date.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XmlReportSerializer serializer = getSerializer();
        serializer.alias("sample", Sample.class);
        Sample sample = serializer.fromXml(Sample.class, getXml());

        Assert.assertThat(sample.dateAttribute, is(DateUtil.getDateTime(2015,2,27)));
        Assert.assertThat(sample.dateElement, is(DateUtil.getDateTime(2013,10,22,10,23,11,0)));
    }

    class Sample {
        public Date dateAttribute;
        public Date dateElement;
    }
}
