/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.binary;

import org.apache.ignite.binary.BinaryBasicNameMapper;
import org.apache.ignite.internal.binary.test.GridBinaryTestClass1;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 */
@RunWith(JUnit4.class)
public class BinaryBasicNameMapperSelfTest extends GridCommonAbstractTest {
    /**
     * @throws Exception If failed.
     */
    @Test
    public void testSimpleName() throws Exception {
        BinaryBasicNameMapper mapper = new BinaryBasicNameMapper(true);

        assertEquals("GridBinaryTestClass1", mapper.typeName(GridBinaryTestClass1.class.getName()));

        assertEquals("InnerClass", mapper.typeName(GridBinaryTestClass1.class.getName() + "$InnerClass"));
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testFullName() throws Exception {
        BinaryBasicNameMapper mapper = new BinaryBasicNameMapper(false);

        assertEquals(GridBinaryTestClass1.class.getName(), mapper.typeName(GridBinaryTestClass1.class.getName()));

        assertEquals(GridBinaryTestClass1.class.getName() + "$InnerClass",
            mapper.typeName(GridBinaryTestClass1.class.getName() + "$InnerClass"));
    }
}
