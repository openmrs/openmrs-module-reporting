package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
//Handler(supports={JoinDataSetDefinition.class})
public class NewJoinDataSetDefinitionEvaluator implements DataSetEvaluator {

	private Log log = LogFactory.getLog(getClass());

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
        DataSet<?> righthandDataset =
        	Context.getService(DataSetDefinitionService.class).evaluate(dsd.getRight(), evalContext);
        
        DataSetIndex index = new DataSetIndex();
        
        for (Map<DataSetColumn, ?> row : righthandDataset) {
            
        	log.info("Right-side row: " + row);
        	log.info("Right-side columns: " + righthandDataset.getDataSetDefinition().getColumns());
        	log.info("Join column on right: " + dsd.getJoinColumnOnRight());
        	log.info("Row keys: " + row.keySet());
        	//Object joinValue = row.get(dsd.getJoinColumnOnRight());
        	Object joinValue = null;
        	for (DataSetColumn column : row.keySet()) { 
            	if (column.getColumnName().equals(dsd.getJoinColumnOnLeft())) { 
            		joinValue = row.get(column);
            	}            	
            }
        	log.info("joinvalue: " + joinValue);
            
            if (joinValue != null) {
                List<DataSetRow> holder = index.getIndexEntry(joinValue).getRowSet();
                if (holder == null) {
                    holder = new ArrayList<DataSetRow>();
                    //index.putIndexEntry(joinValue, holder);
                }
                //holder.add(row);
            }
        }
        
        // next we evaluate the dataset on the left side of the join, and iterate over it, 
        // joining against the other dataset using the index we just created
        DataSet<?> lefthandDataset = Context.getService(DataSetDefinitionService.class).evaluate(dsd.getLeft(), evalContext);
        for (Map<DataSetColumn, ?> row : lefthandDataset) {
 
        	log.info("Left-side row: " + row);
        	log.info("Left-side columns: " + lefthandDataset.getDataSetDefinition().getColumns());
        	log.info("Join column on right: " + dsd.getJoinColumnOnRight());
        	log.info("Row keys: " + row.keySet());
        	//Object joinValue = row.get(dsd.getJoinColumnOnRight());
            Object joinValue = null;
        	for (DataSetColumn column : row.keySet()) { 
            	if (column.getColumnName().equals(dsd.getJoinColumnOnRight())){ 
            		joinValue = row.get(column);
            	}            	
            }
            log.info("joinValue: " + joinValue);
            if (joinValue == null)
                continue;
                        
            log.info("Index: " + index);
            
            DataSetIndexEntry indexEntry = index.getIndexEntry(joinValue);
            for (DataSetRow otherRow : indexEntry.getRowSet()) {
                Map<DataSetColumn, Object> outputRow = new HashMap<DataSetColumn, Object>();
                for (Map.Entry<DataSetColumn, ?> inLeft : row.entrySet()) {
                    outputRow.put(prefixDataSetColumn(dsd.getPrefixForLeft(), inLeft.getKey()), inLeft.getValue());
                }
                for (Map.Entry<DataSetColumn, ?> inRight : otherRow.getEntrySet()) {
                    outputRow.put(prefixDataSetColumn(dsd.getPrefixForLeft(), inRight.getKey()), inRight.getValue());
                }
                ret.addRow(outputRow);
            }
        }
        
        return ret;
    }

    
    
    
    private DataSetColumn prefixDataSetColumn(String prefixForLeft, DataSetColumn c) {
        return new SimpleDataSetColumn(prefixForLeft + c.getKey(), c.getColumnName(), c.getDescription(), c.getDataType());
    }
}


class DataSetIndex { 
	
	Map<Object, DataSetIndexEntry> index = 
    	new HashMap<Object, DataSetIndexEntry>();
	
	public DataSetIndexEntry getIndexEntry(Object key) { 
		return index.get(key);
	}
	
}

class DataSetIndexEntry { 
	
	List<DataSetRow> entrySet = new ArrayList<DataSetRow>();

	public List<DataSetRow> getRowSet() { 
		return entrySet;
	}
	
	
}

class DataSetRow { 

	private Map<DataSetColumn, Object> columnValueMap = 
		new HashMap<DataSetColumn, Object>();

	
	public void add(DataSetColumn column, Object value) { 
		columnValueMap.put(column, value);
	}
	
	
	public Set<Entry<DataSetColumn, Object>> getEntrySet() { 
		return columnValueMap.entrySet();
	}
	
	
}
