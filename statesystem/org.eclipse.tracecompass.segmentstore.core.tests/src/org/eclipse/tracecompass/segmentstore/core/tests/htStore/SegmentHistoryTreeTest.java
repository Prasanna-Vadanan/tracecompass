/*******************************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.segmentstore.core.tests.htStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.internal.provisional.datastore.core.historytree.AbstractHistoryTree;
import org.eclipse.tracecompass.internal.provisional.datastore.core.historytree.overlapping.AbstractOverlappingHistoryTreeTestBase;
import org.eclipse.tracecompass.internal.provisional.segmentstore.core.BasicSegment2;
import org.eclipse.tracecompass.internal.segmentstore.core.segmentHistoryTree.SegmentHistoryTree;
import org.eclipse.tracecompass.internal.segmentstore.core.segmentHistoryTree.SegmentTreeNode;
import org.eclipse.tracecompass.segmentstore.core.tests.historytree.SegmentHistoryTreeStub;
import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 * Test the segment history tree itself, using a stub history tree with stub
 * nodes
 *
 * @author Geneviève Bastien
 */
@NonNullByDefault
public class SegmentHistoryTreeTest extends AbstractOverlappingHistoryTreeTestBase<BasicSegment2, SegmentTreeNode<BasicSegment2>> {

    private static final BasicSegment2 DEFAULT_OBJECT = new BasicSegment2(0, 0);

    @Override
    protected SegmentHistoryTreeStub<BasicSegment2> createHistoryTree(@NonNull File stateHistoryFile, int blockSize, int maxChildren, int providerVersion, long treeStart)
            throws IOException {
        return new SegmentHistoryTreeStub<>(stateHistoryFile,
                blockSize,
                maxChildren,
                providerVersion,
                treeStart,
                BasicSegment2.BASIC_SEGMENT_READ_FACTORY);
    }

    @Override
    protected SegmentHistoryTreeStub<BasicSegment2> createHistoryTree(@NonNull File existingStateFile, int expectedProviderVersion) throws IOException {
        return new SegmentHistoryTreeStub<>(existingStateFile, expectedProviderVersion,
                BasicSegment2.BASIC_SEGMENT_READ_FACTORY);
    }

    @Override
    protected BasicSegment2 createInterval(long start, long end) {
        return new BasicSegment2(start, end);
    }

    @Override
    protected long fillValues(@NonNull AbstractHistoryTree<@NonNull BasicSegment2, SegmentTreeNode<@NonNull BasicSegment2>> ht, int fillSize, long start) {
        int nbValues = fillSize / DEFAULT_OBJECT.getSizeOnDisk();
        for (int i = 0; i < nbValues; i++) {
            ht.insert(createInterval(start + i, start + i + 1));
        }
        return start + nbValues;
    }

    /**
     * Test the {@link SegmentHistoryTree#isEmpty()} method
     */
    @Test
    public void testIsEmpty() {
        long start = 10L;
        SegmentHistoryTreeStub<BasicSegment2> oht = (SegmentHistoryTreeStub<BasicSegment2>) setupSmallTree(3, start);

        assertTrue(oht.isEmpty());

        // Add one element, the tree should not be empty anymore
        oht.insert(createInterval(start, start + 1));
        assertFalse(oht.isEmpty());

        /* Fill a first node */
        SegmentTreeNode<BasicSegment2> node = oht.getLatestLeaf();
        long time = fillValues(oht, node.getNodeFreeSpace(), start);

        /*
         * Add an element that will create a sibling and a root node, the root
         * node should be empty but not the tree
         */
        oht.insert(createInterval(time, time + 1));
        assertFalse(oht.isEmpty());
    }

    /**
     * Test the {@link SegmentHistoryTree#size()} method
     *
     * @throws IOException
     *             Exceptions during the test
     */
    @Test
    public void testSizeAndIterator() throws IOException {
        long start = 10L;
        SegmentHistoryTreeStub<BasicSegment2> oht = (SegmentHistoryTreeStub<BasicSegment2>) setupSmallTree(3, start);

        int nbSegments = 0;
        assertEquals(nbSegments, oht.size());

        // Add one element, the tree have one element
        oht.insert(createInterval(start, start + 1));
        nbSegments++;
        assertEquals(nbSegments, oht.size());

        /* Fill a first node */
        SegmentTreeNode<BasicSegment2> node = oht.getLatestLeaf();
        nbSegments += node.getNodeFreeSpace() / DEFAULT_OBJECT.getSizeOnDisk();
        long time = fillValues(oht, node.getNodeFreeSpace(), start);
        assertEquals(nbSegments, oht.size());

        /*
         * Add an element that will create a sibling and a root node, the root
         * node should be empty but not the tree
         */
        oht.insert(createInterval(time, time + 1));
        nbSegments++;
        // Add some intervals that will go in the top node
        oht.insert(createInterval(start, time));
        oht.insert(createInterval(start, start + 10));
        nbSegments += 2;
        assertEquals(nbSegments, oht.size());

        /*
         * Fill the latest node
         */
        node = oht.getLatestLeaf();
        nbSegments += node.getNodeFreeSpace() / DEFAULT_OBJECT.getSizeOnDisk();
        time = fillValues(oht, node.getNodeFreeSpace(), start);
        assertEquals(nbSegments, oht.size());

        // Close the tree
        oht.closeTree(oht.getTreeEnd());

        // Create a reader history tree
        oht = (SegmentHistoryTreeStub<BasicSegment2>) createHistoryTreeReader();
        assertEquals(nbSegments, oht.size());

        // Test the iterator on that tree
        Iterator<BasicSegment2> iterator = oht.iterator();
        assertNotNull(iterator);
        int count = Iterators.size(iterator);
        assertEquals(nbSegments, count);

        // Test the intersecting elements
        Iterable<BasicSegment2> intersectingElements = oht.getIntersectingElements(start, time);
        count = 0;
        // While doing this iterator, count the number of segments of a smaller
        // time range, it will be needed later
        long rangeStart = (long) (start + (time - start) * 0.4);
        long rangeEnd = (long) (time - (time - start) * 0.4);
        int nbInRange = 0;
        for (BasicSegment2 segment : intersectingElements) {
            count++;
            if (segment.getStart() <= rangeEnd && rangeStart <= segment.getEnd()) {
                nbInRange++;
            }
        }
        assertEquals(nbSegments, count);

        // Test intersecting elements for a time range
        count = Iterators.size(oht.getIntersectingElements(rangeStart, rangeEnd).iterator());
        assertEquals(nbInRange, count);

        // Test intersecting elements with start time ordering
        Comparator<BasicSegment2> cmp = NonNullUtils.checkNotNull(Comparator.comparing(BasicSegment2::getStart));
        assertSortedIteration(oht, rangeStart, rangeEnd, cmp, nbInRange);

        // Test intersecting elements with end time ordering
        cmp = NonNullUtils.checkNotNull(Comparator.comparing(BasicSegment2::getEnd));
        assertSortedIteration(oht, rangeStart, rangeEnd, cmp, nbInRange);

        // Test intersecting elements with duration ordering
        cmp = NonNullUtils.checkNotNull(Comparator.comparing(BasicSegment2::getLength));
        assertSortedIteration(oht, rangeStart, rangeEnd, cmp, nbInRange);
    }

    private static void assertSortedIteration(SegmentHistoryTreeStub<@NonNull BasicSegment2> oht, long rangeStart, long rangeEnd, Comparator<@NonNull BasicSegment2> cmp, int nbInRange) {
        int count = 0;
        BasicSegment2 prev = DEFAULT_OBJECT;
        for (BasicSegment2 segment : oht.getIntersectingElements(rangeStart, rangeEnd, cmp)) {
            count++;
            assertTrue("Segment comparison at " + count, cmp.compare(prev, segment) <= 0);
        }
        assertEquals(nbInRange, count);
    }

}
