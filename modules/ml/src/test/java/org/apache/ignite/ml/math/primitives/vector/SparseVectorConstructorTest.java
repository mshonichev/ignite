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

package org.apache.ignite.ml.math.primitives.vector;

import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.math.StorageConstants;
import org.apache.ignite.ml.math.primitives.vector.impl.SparseVector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** */
public class SparseVectorConstructorTest {
    /** */
    private static final int IMPOSSIBLE_SIZE = -1;

    /** */
    @Test(expected = AssertionError.class)
    public void negativeSizeTest() {
        assertEquals("Negative size.", IMPOSSIBLE_SIZE,
            new SparseVector(-1, 1).size());
    }

    /** */
    @Test(expected = AssertionError.class)
    public void zeroSizeTest() {
        assertEquals("0 size.", IMPOSSIBLE_SIZE,
            new SparseVector(0, 1).size());
    }

    /** */
    @Test
    public void primitiveTest() {
        assertEquals("1 size, random access.", 1,
            new SparseVector(1, StorageConstants.RANDOM_ACCESS_MODE).size());

        assertEquals("1 size, sequential access.", 1,
            new SparseVector(1, StorageConstants.SEQUENTIAL_ACCESS_MODE).size());

    }

    /** */
    @Test
    public void noParamsCtorTest() {
        assertNotNull(new SparseVector().nonZeroSpliterator());
    }

    /** */
    @Test
    public void mapCtorTest() {
        Map<Integer, Double> map = new HashMap<Integer, Double>() {{
            put(1, 1.);
        }};

        assertTrue("Copy true", new SparseVector(map, true).isRandomAccess());
        assertTrue("Copy false", new SparseVector(map, false).isRandomAccess());
    }
}
