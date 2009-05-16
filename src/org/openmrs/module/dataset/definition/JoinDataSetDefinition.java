package org.openmrs.module.dataset.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Defines a dataset that you get from doing an inner join on two different dataset definitions.
 * 
 * Think of this as:
 * select
 *      left.* (prefixed with prefixForLeft),
 *      right.* (prefixed with prefixForRight)
 * from left inner join right
 *      on left.joinColumnOnLeft = right.joinColumnOnRight
 *      
 * So you might make a call like this:
 *      new JoinDataSetDefinition(patientDSD, "patient.", "patient_id", encounterDSD, "encounter.", "patient_id");
 * 
 * Given inputs like:
 *      patientDSD is a DataSetDefinition that evaluates to:
 *      patient_id, name
 *      1, Alice
 *      2, Bob
 *      
 *      encounterDSD is a DataSetDefinition that evaluates to:
 *      patient_id, encounter_id, encounter_type
 *      1, 1, Registration
 *      1, 2, LabOrder
 *      2, 3, Registration
 *      
 * When you evaluate the JoinDataSetDefinition described above, this would produce output like:
 *      patient.patient_id, patient.name, encounter.patient_id, encounter.encounter_id, encounter.encounter_type
 *      1, Alice, 1, 1, Registration
 *      1, Alice, 1, 2, LabOrder
 *      2, Bob, 2, 3, Registration
 * 
 * This class *may* eventually be extended to support outer joins, but you may rely on it defaulting to doing an inner join.
 */
public class JoinDataSetDefinition extends BaseDataSetDefinition {
	
	public static final long serialVersionUID = 1L;
    
    // ***************
    // PROPERTIES
    // ***************
	
    private DataSetDefinition left;
    private DataSetDefinition right;
    private String prefixForLeft;
    private String prefixForRight;
    private String joinColumnOnLeft;
    private String joinColumnOnRight;
    
    // ***************
    // CONSTRUCTORS
    // ***************
    
    /**
     * Default Constructor
     */
    public JoinDataSetDefinition() { }
    
    /**
     * Full Constructor to set all properties
     * @param left
     * @param prefixForLeft
     * @param joinColumnOnLeft
     * @param right
     * @param prefixForRight
     * @param joinColumnOnRight
     */
    public JoinDataSetDefinition(DataSetDefinition left, String prefixForLeft, String joinColumnOnLeft, 
    							 DataSetDefinition right, String prefixForRight, String joinColumnOnRight) {
        this.left = left;
        this.prefixForLeft = prefixForLeft;
        this.joinColumnOnLeft = joinColumnOnLeft;
        this.right = right;
        this.prefixForRight = prefixForRight;
        this.joinColumnOnRight = joinColumnOnRight;
    }
    
    // ***************
    // INSTANCE METHODS
    // ***************

    /**
     * @see DataSetDefinition#getColumns()
     */
    public List<DataSetColumn> getColumns() {
        List<DataSetColumn> ret = new ArrayList<DataSetColumn>();
        for (DataSetColumn col : left.getColumns()) {
            ret.add(new SimpleDataSetColumn(prefixForLeft + col.getKey(), col.getColumnName(), col.getDescription(), col.getDataType()));
        }
        for (DataSetColumn col : right.getColumns()) {
            ret.add(new SimpleDataSetColumn(prefixForRight + col.getKey(), col.getColumnName(), col.getDescription(), col.getDataType()));
        }
        return ret;
    }
    
    // ***************
    // PROPERTY ACCESS
    // ***************

    /**
     * @return the left
     */
    public DataSetDefinition getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(DataSetDefinition left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public DataSetDefinition getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(DataSetDefinition right) {
        this.right = right;
    }

    /**
     * @return the prefixForLeft
     */
    public String getPrefixForLeft() {
        return prefixForLeft;
    }

    /**
     * @param prefixForLeft the prefixForLeft to set
     */
    public void setPrefixForLeft(String prefixForLeft) {
        this.prefixForLeft = prefixForLeft;
    }

    /**
     * @return the prefixForRight
     */
    public String getPrefixForRight() {
        return prefixForRight;
    }

    /**
     * @param prefixForRight the prefixForRight to set
     */
    public void setPrefixForRight(String prefixForRight) {
        this.prefixForRight = prefixForRight;
    }

    /**
     * @return the joinColumnOnLeft
     */
    public String getJoinColumnOnLeft() {
        return joinColumnOnLeft;
    }

    /**
     * @param joinColumnOnLeft the joinColumnOnLeft to set
     */
    public void setJoinColumnOnLeft(String joinColumnOnLeft) {
        this.joinColumnOnLeft = joinColumnOnLeft;
    }

    /**
     * @return the joinColumnOnRight
     */
    public String getJoinColumnOnRight() {
        return joinColumnOnRight;
    }

    /**
     * @param joinColumnOnRight the joinColumnOnRight to set
     */
    public void setJoinColumnOnRight(String joinColumnOnRight) {
        this.joinColumnOnRight = joinColumnOnRight;
    }
}
