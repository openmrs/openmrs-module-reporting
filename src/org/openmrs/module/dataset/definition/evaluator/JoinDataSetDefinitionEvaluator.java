package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.SimpleDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * @see JoinDataSetDefinition
 */
@Handler(supports={JoinDataSetDefinition.class})
public class JoinDataSetDefinitionEvaluator implements DataSetEvaluator {

    /**
     * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
     * 
     * @should join two plain {@link DataSet}s correctly
     */
    public DataSet<?> evaluate(DataSetDefinition joinDataSetDefinition, EvaluationContext evalContext) {
        JoinDataSetDefinition dsd = (JoinDataSetDefinition) joinDataSetDefinition;
        SimpleDataSet ret = new SimpleDataSet(dsd, evalContext);
        
        // first we evaluate the dataset on the right side of the join, 
        // and we build an index from the join column to all rows with that value
        DataSet<?> right = Context.getService(DataSetDefinitionService.class).evaluate(dsd.getRight(), evalContext);
        DataSetColumn rightJoinColumn = findColumnDefinition(dsd.getRight(), dsd.getJoinColumnOnRight());
        Map<Object, List<Map<DataSetColumn, ?>>> index = new HashMap<Object, List<Map<DataSetColumn, ?>>>();
        for (Map<DataSetColumn, ?> row : right) {
            Object joinValue = row.get(rightJoinColumn);
            if (joinValue != null) {
                List<Map<DataSetColumn, ?>> holder = index.get(joinValue);
                if (holder == null) {
                    holder = new ArrayList<Map<DataSetColumn, ?>>();
                    index.put(joinValue, holder);
                }
                holder.add(row);
            }
        }
        
        // next we evaluate the dataset on the left side of the join, and iterate over it, 
        // joining against the other dataset using the index we just created
        DataSet<?> left = Context.getService(DataSetDefinitionService.class).evaluate(dsd.getLeft(), evalContext);
        DataSetColumn leftJoinColumn = findColumnDefinition(dsd.getLeft(), dsd.getJoinColumnOnLeft());
        for (Map<DataSetColumn, ?> row : left) {
            Object joinValue = row.get(leftJoinColumn);
            if (joinValue == null)
                continue;
            List<Map<DataSetColumn, ?>> rowsInOtherDataset = index.get(joinValue);
            if (rowsInOtherDataset != null) {
                for (Map<DataSetColumn, ?> otherRow : rowsInOtherDataset) {
                    Map<DataSetColumn, Object> outputRow = new HashMap<DataSetColumn, Object>();
                    for (Map.Entry<DataSetColumn, ?> inLeft : row.entrySet()) {
                        outputRow.put(prefixDataSetColumn(dsd.getPrefixForLeft(), inLeft.getKey()), inLeft.getValue());
                    }
                    for (Map.Entry<DataSetColumn, ?> inRight : otherRow.entrySet()) {
                        outputRow.put(prefixDataSetColumn(dsd.getPrefixForRight(), inRight.getKey()), inRight.getValue());
                    }
                    ret.addRow(outputRow);
                }
            }
        }
        
        return ret;
    }

    /**
     * Helper method that inspects a dataSetDefinition, and finds the first DataSetColumn
     * that has the given key. 
     * 
     * @param dataSet
     * @param columnKey
     * @return
     */
    private DataSetColumn findColumnDefinition(DataSetDefinition definition, String columnKey) {
        for (DataSetColumn test : definition.getColumns()) {
            if (test.getColumnKey().equals(columnKey)) {
                return test;
            }
        }
        return null;
    }

    private DataSetColumn prefixDataSetColumn(String prefixForLeft, DataSetColumn c) {
        return new SimpleDataSetColumn(prefixForLeft + c.getColumnKey(), c.getDisplayName(), c.getDescription(), c.getDataType());
    }
}
