package org.openmrs.module.reporting.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.xml.converter.StringToObjectMapConverter;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class XmlReportUtil {

    public static XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("parameter", Parameter.class);
        xstream.alias("sqlDataSet", SqlDataSetDefinition.class);

        // Whenever possible we want to allow simple types to be specified as either attributes or nested elements for readability and simplicity
        xstream.alias("boolean", Boolean.class);
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

        // Our primary use case is to support dates in the format yyyy-MM-dd, which is not handled by default converter that is registered
        xstream.registerConverter(new DateConverter("yyyy-MM-dd hh:mm:ss", new String[]{"yyyy-MM-dd hh:mm", "yyyy-MM-dd"}, TimeZone.getDefault()));

        // Almost all of the Maps we are converting into have Strings as keys, so we'll register this as the default
        xstream.registerConverter(new StringToObjectMapConverter(xstream.getMapper()));

        return xstream;
    }
}
