package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * @see JoinDataSetDefinition
 */
@Handler(supports={JoinDataSetDefinition.class})
public class JoinDataSetDefinitionEvaluator implements DataSetEvaluator {

    /**
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     * @should join two plain {@link DataSet}s correctly
     */
    public DataSet evaluate(DataSetDefinition joinDataSetDefinition, EvaluationContext evalContext) {
        JoinDataSetDefinition dsd = (JoinDataSetDefinition) joinDataSetDefinition;
        SimpleDataSet ret = new SimpleDataSet(dsd, evalContext);
        
        // first we evaluate the dataset on the right side of the join, 
        // and we build an index from the join column to all rows with that value
        DataSet right = Context.getService(DataSetDefinitionService.class).evaluate(dsd.getRight(), evalContext);
        DataSetColumn rightJoinColumn = dsd.getRight().getColumn(dsd.getJoinColumnOnRight());
        Map<Object, List<DataSetRow>> index = new HashMap<Object, List<DataSetRow>>();
        for (DataSetRow row : right) {
            Object joinValue = row.getColumnValue(rightJoinColumn);
            if (joinValue != null) {
                List<DataSetRow> holder = index.get(joinValue);
                if (holder == null) {
                    holder = new ArrayList<DataSetRow>();
                    index.put(joinValue, holder);
                }
                holder.add(row);
            }
        }
        
        // next we evaluate the dataset on the left side of the join, and iterate over it, 
        // joining against the other dataset using the index we just created
        DataSet left = Context.getService(DataSetDefinitionService.class).evaluate(dsd.getLeft(), evalContext);
        DataSetColumn leftJoinColumn = dsd.getLeft().getColumn(dsd.getJoinColumnOnLeft());
        for (DataSetRow row : left) {
            Object joinValue = row.getColumnValue(leftJoinColumn);
            if (joinValue != null) {
	            List<DataSetRow> rowsInOtherDataset = index.get(joinValue);
	            if (rowsInOtherDataset != null) {
	                for (DataSetRow otherRow : rowsInOtherDataset) {
	                    DataSetRow outputRow = new DataSetRow();
	                    for (Map.Entry<DataSetColumn, ?> inLeft : row.getColumnValues().entrySet()) {
	                        outputRow.addColumnValue(prefixDataSetColumn(dsd.getPrefixForLeft(), inLeft.getKey()), inLeft.getValue());
	                    }
	                    for (Map.Entry<DataSetColumn, ?> inRight : otherRow.getColumnValues().entrySet()) {
	                        outputRow.addColumnValue(prefixDataSetColumn(dsd.getPrefixForRight(), inRight.getKey()), inRight.getValue());
	                    }
	                    ret.addRow(outputRow);
	                }
	            }
            }
        }
        
        return ret;
    }

    private DataSetColumn prefixDataSetColumn(String prefixForLeft, DataSetColumn c) {
        return new SimpleDataSetColumn(prefixForLeft + c.getColumnKey(), c.getDisplayName(), c.getDescription(), c.getDataType());
    }
}
