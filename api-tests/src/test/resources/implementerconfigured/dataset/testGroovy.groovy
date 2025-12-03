package implementerconfigured.dataset

import org.openmrs.api.LocationService
import org.openmrs.module.reporting.dataset.DataSet
import org.openmrs.module.reporting.dataset.DataSetColumn
import org.openmrs.module.reporting.dataset.MapDataSet
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty
import org.openmrs.module.reporting.evaluation.EvaluationContext
import org.springframework.beans.factory.annotation.Autowired

class MyDataSetDefinition extends EvaluatableDataSetDefinition {

    @Autowired
    @ConfigurationProperty
    LocationService locationService

    @Override
    DataSet evaluate(EvaluationContext evalContext) {
        def dataSet = new MapDataSet(this, evalContext)
        dataSet.addData(new DataSetColumn("groovy", "Groovy", String.class), locationService.getLocation("Xanadu").getName())
        return dataSet
    }

}
