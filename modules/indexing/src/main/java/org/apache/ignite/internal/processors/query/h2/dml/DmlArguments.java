/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * (you may not use this file except in compliance with the License.
 * (You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * (distributed under the License is distributed on an "AS IS" BASIS,
 * (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * (See the License for the specific language governing permissions and
 * (limitations under the License.
 */

package org.apache.ignite.internal.processors.query.h2.dml;

import org.apache.ignite.internal.processors.query.h2.sql.GridSqlConst;
import org.apache.ignite.internal.processors.query.h2.sql.GridSqlElement;
import org.apache.ignite.internal.processors.query.h2.sql.GridSqlParameter;
import org.jetbrains.annotations.Nullable;

/**
 * DML arguments factory.
 */
public class DmlArguments {
    /** Operand that always evaluates as {@code null}. */
    private final static DmlArgument NULL_ARG = new ConstantArgument(null);

    /**
     * Create argument from AST element.
     *
     * @param el Element.
     * @return DML argument.
     */
    public static DmlArgument create(@Nullable GridSqlElement el) {
        assert el == null ^ (el instanceof GridSqlConst || el instanceof GridSqlParameter);

        if (el == null)
            return NULL_ARG;

        if (el instanceof GridSqlConst)
            return new ConstantArgument(((GridSqlConst)el).value().getObject());
        else
            return new ParamArgument(((GridSqlParameter)el).index());
    }

    /**
     * Private constructor.
     */
    private DmlArguments() {
        // No-op.
    }

    /**
     * Value argument.
     */
    private static class ConstantArgument implements DmlArgument {
        /** Value to return. */
        private final Object val;

        /**
         * Constructor.
         *
         * @param val Value.
         */
        private ConstantArgument(Object val) {
            this.val = val;
        }

        /** {@inheritDoc} */
        public Object get(Object[] params) {
            return val;
        }
    }

    /**
     * Parameter argument.
     */
    private static class ParamArgument implements DmlArgument {
        /** Value to return. */
        private final int paramIdx;

        /**
         * Constructor.
         *
         * @param paramIdx Parameter index.
         */
        private ParamArgument(int paramIdx) {
            assert paramIdx >= 0;

            this.paramIdx = paramIdx;
        }

        /** {@inheritDoc} */
        @Override public Object get(Object[] params) {
            assert params.length > paramIdx;

            return params[paramIdx];
        }
    }
}
