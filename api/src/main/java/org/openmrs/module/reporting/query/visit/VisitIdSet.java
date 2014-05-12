package org.openmrs.module.reporting.query.visit;

import java.util.List;
import java.util.Set;

import org.openmrs.Visit;
import org.openmrs.module.reporting.query.BaseIdSet;

/**
 * A Set of Visit Ids
 */
public class VisitIdSet extends BaseIdSet<Visit> {

    public VisitIdSet() {
        super();
    }

    public VisitIdSet(Set<Integer> memberIds) {
        setMemberIds(memberIds);
    }

    public VisitIdSet(List<Integer> memberIds) {
        add(memberIds.toArray(new Integer[0]));
    }

    public VisitIdSet(Integer... memberIds) {
        add(memberIds);
    }

}
