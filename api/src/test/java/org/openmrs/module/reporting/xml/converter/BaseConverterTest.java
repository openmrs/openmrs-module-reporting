package org.openmrs.module.reporting.xml.converter;

import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
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
                l.add(PatientDataSetDefinition.class);
                l.add(AgeCohortDefinition.class);
                l.add(GenderCohortDefinition.class);
                return l;
            }
        };
    }
}
