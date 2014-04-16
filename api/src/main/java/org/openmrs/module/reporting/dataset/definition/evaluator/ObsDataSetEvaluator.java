package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.QueryUtil;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.definition.AllObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports=ObsDataSetDefinition.class)
public class ObsDataSetEvaluator implements DataSetEvaluator {

    @Autowired
    ObsQueryService obsQueryService;

    @Autowired
    ObsDataService obsDataService;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
        ObsDataSetDefinition dsd = (ObsDataSetDefinition) dataSetDefinition;
        context = ObjectUtil.nvl(context, new EvaluationContext());

		SimpleDataSet dataSet = new SimpleDataSet(dsd, context);
		dataSet.setSortCriteria(dsd.getSortCriteria());

        // Construct an ObsEvaluationContext based on the obs filter
        ObsIdSet r = null;
        if (dsd.getRowFilters() != null) {
            for (Mapped<? extends ObsQuery> q : dsd.getRowFilters()) {
                ObsIdSet s = obsQueryService.evaluate(q, context);
                r = QueryUtil.intersectNonNull(r, s);
            }
        }
        if (r == null) {
            r = obsQueryService.evaluate(new AllObsQuery(), context);
        }
        ObsEvaluationContext eec = new ObsEvaluationContext(context, r);
		eec.setBaseCohort(null); // We can do this because the obsIdSet is already limited by these

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
        for (RowPerObjectColumnDefinition cd : dsd.getColumnDefinitions()) {

            MappedData<? extends ObsDataDefinition> dataDef = (MappedData<? extends ObsDataDefinition>) cd.getDataDefinition();
            EvaluatedObsData data = obsDataService.evaluate(dataDef, eec);

            DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), dataDef.getParameterizable().getDataType()); // TODO: Support One-Many column definition to column

            for (Integer id : r.getMemberIds()) {
                Object val = data.getData().get(id);
                val = DataUtil.convertData(val, dataDef.getConverters());
                dataSet.addColumnValue(id, column, val);
            }
        }

        return dataSet;
    }
}
