/*******************************************************************************
* Copyright (c) 2018 Ericsson
*
* All rights reserved. This program and the accompanying materials are
* made available under the terms of the Eclipse Public License v1.0 which
* accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.eclipse.tracecompass.internal.provisional.tmf.core.model.filter.parser;

import java.util.function.BiPredicate;

import org.antlr.runtime.tree.CommonTree;

/**
 * Compilation unit for a simple filter expression negation
 *
 * @author Jean-Christian Kouame
 *
 */
public class FilterSimpleExpressionNotCu extends FilterSimpleExpressionCu {

    /**
     * Constructor
     *
     * @param field
     *            The field to look for
     * @param op
     *            The operator to use for the test
     * @param value
     *            The value to to test
     */
    public FilterSimpleExpressionNotCu(String field, BiPredicate<String, String> op, String value) {
        super(field, op, value);
    }

    public static FilterSimpleExpressionNotCu compile(CommonTree tree) {
        FilterSimpleExpressionCu cu = FilterSimpleExpressionCu.compile(tree);
        return new FilterSimpleExpressionNotCu(cu.getField(), cu.getOperator(), cu.getValue());
    }

    @Override
    public FilterSimpleExpression generate() {
        return new FilterSimpleExpressionNot(getField(), getOperator(), getValue());
    }
}
