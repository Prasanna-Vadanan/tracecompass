/*******************************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.statesystem.core.tests.statevalue;

import static org.junit.Assert.assertEquals;

import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue.Type;
import org.eclipse.tracecompass.statesystem.core.statevalue.TmfStateValue;
import org.junit.Test;

/**
 * Test the string state value class
 *
 * @author Geneviève Bastien
 */
public class StringStateValueTest extends StateValueTestBase {

    private static final String UNBOXED_VALUE = "MyString";
    private static final TmfStateValue STATE_VALUE = TmfStateValue.newValueString(UNBOXED_VALUE);

    @Override
    protected ITmfStateValue getStateValueFixture() {
        return STATE_VALUE;
    }

    @Override
    protected Type getStateValueType() {
        return ITmfStateValue.Type.STRING;
    }

    @Override
    @Test
    public void testUnboxStr() {
        String unboxed = STATE_VALUE.unboxStr();
        assertEquals(UNBOXED_VALUE, unboxed);
    }
}
