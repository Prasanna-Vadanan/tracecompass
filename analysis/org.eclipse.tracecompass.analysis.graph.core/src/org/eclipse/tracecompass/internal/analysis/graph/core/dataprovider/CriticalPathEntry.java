/**********************************************************************
 * Copyright (c) 2018 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.internal.analysis.graph.core.dataprovider;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.analysis.graph.core.base.IGraphWorker;
import org.eclipse.tracecompass.internal.analysis.graph.core.base.TmfGraphStatistics;
import org.eclipse.tracecompass.tmf.core.model.timegraph.TimeGraphEntryModel;

/**
 * {@link TimeGraphEntryModel} for the Critical Path
 *
 * @author Loic Prieur-Drevon
 */
public class CriticalPathEntry extends TimeGraphEntryModel {
    private final Long fSum;
    private final Double fPercent;

    /**
     * Constructor
     *
     * @param id
     *            unique entry ID
     * @param parentId
     *            entry's parent unique ID
     * @param name
     *            entry name
     * @param startTime
     *            entry's start time
     * @param endTime
     *            entry's end time
     * @param sum
     *            {@link TmfGraphStatistics} sum for the associated
     *            {@link IGraphWorker}
     * @param percent
     *            {@link TmfGraphStatistics} percentage for the associated
     *            {@link IGraphWorker}
     */
    public CriticalPathEntry(long id, long parentId, @NonNull String name,
            long startTime, long endTime, Long sum, Double percent) {
        super(id, parentId, name, startTime, endTime);
        fSum = sum;
        fPercent = percent;
    }

    /**
     * Getter for the {@link TmfGraphStatistics} sum of this entry
     *
     * @return statistics sum for this entry
     */
    public Long getSum() {
        return fSum;
    }

    /**
     * Getter for the {@link TmfGraphStatistics} percentage for this entry
     *
     * @return statistics percentage for this entry
     */
    public Double getPercent() {
        return fPercent;
    }

}
