package org.openmrs.module.reporting.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class XmlReportUtil {

    public static XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("parameter", Parameter.class);
        xstream.alias("sqlDataSet", SqlDataSetDefinition.class);

        xstream.useAttributeFor(Boolean.class);
        xstream.useAttributeFor(boolean.class);
        xstream.useAttributeFor(Integer.class);
        xstream.useAttributeFor(int.class);
        xstream.useAttributeFor(Double.class);
        xstream.useAttributeFor(double.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(Class.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(Locale.class);

        xstream.registerConverter(new DateConverter("yyyy-MM-dd hh:mm:ss", new String[]{"yyyy-MM-dd hh:mm", "yyyy-MM-dd"}, TimeZone.getDefault()));

        return xstream;
    }
}
