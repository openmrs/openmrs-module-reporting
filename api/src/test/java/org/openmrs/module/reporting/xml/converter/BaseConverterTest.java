package org.openmrs.module.reporting.xml.converter;

import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.xml.XmlReportSerializer;

import java.util.ArrayList;
import java.util.List;

public class BaseConverterTest {

    /**
     * @return a mocked serializer to avoid needing to have a context sensitive test
     */
    public XmlReportSerializer getSerializer() {
        return new XmlReportSerializer() {
            @Override
            public List<Class<? extends Definition>> getSupportedDefinitionTypes() {
                List<Class<? extends Definition>> l = new ArrayList<Class<? extends Definition>>();
                l.add(ReportDefinition.class);
                l.add(SqlDataSetDefinition.class);
                return l;
            }
        };
    }
}
