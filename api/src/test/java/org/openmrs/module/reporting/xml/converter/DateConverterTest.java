package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.xml.XmlReportUtil;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

public class DateConverterTest {

    protected String getXml() {
        return ReportUtil.readStringFromResource("org/openmrs/module/reporting/xml/converter/date.xml");
    }

    @Test
    public void testMarshall() throws Exception {
        XStream xstream = XmlReportUtil.getXStream();
        xstream.alias("sample", Sample.class);
        Sample sample = (Sample)xstream.fromXML(getXml());

        Assert.assertThat(sample.dateAttribute, is(DateUtil.getDateTime(2015,2,27)));
        Assert.assertThat(sample.dateElement, is(DateUtil.getDateTime(2013,10,22,10,23,11,0)));
    }

    class Sample {
        public Date dateAttribute;
        public Date dateElement;
    }
}
