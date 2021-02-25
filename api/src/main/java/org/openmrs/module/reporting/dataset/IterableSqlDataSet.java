/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.IterableSqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.IterableDataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.ResultSetIterator;

import java.util.Iterator;

/**
 * This is a {@link DataSet} that allows you to request the rows for a subset of the
 * input cohort, and it will generate those rows on demand (by delegating to a
 * {@link IterableDataSetEvaluator}).
 */
public class IterableSqlDataSet implements DataSet {

    ResultSetIterator dataSetRowIterator;
    SimpleDataSetMetaData metadata;
    EvaluationContext context;
    IterableSqlDataSetDefinition definition;

    /**
     * @param evalContext
     * @param definition
     */
    public IterableSqlDataSet(EvaluationContext evalContext, IterableSqlDataSetDefinition definition, ResultSetIterator iterator) {
        this.context = evalContext;
        this.definition = definition;
        this.dataSetRowIterator = iterator;
        mapMetaData();
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<DataSetRow> iterator() {
        return dataSetRowIterator;
    }

    /**
     * @see DataSet#getMetaData()
     */
    public DataSetMetaData getMetaData() {
        return metadata;
    }

    /**
     * @see DataSet#getContext()
     */
    public EvaluationContext getContext() {
        return context;
    }

    /**
     * @see DataSet#getDefinition()
     */
    public DataSetDefinition getDefinition() {
        return definition;
    }

    private void mapMetaData(){
        for(DataSetColumn column: dataSetRowIterator.getColumns()){
            metadata.addColumn(new DataSetColumn(column.getName(),column.getLabel(),column.getDataType()));
        }
    }
}
