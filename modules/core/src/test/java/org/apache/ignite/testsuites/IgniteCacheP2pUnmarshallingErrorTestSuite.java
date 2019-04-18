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

package org.apache.ignite.testsuites;

import java.util.Set;
import junit.framework.TestSuite;
import org.apache.ignite.internal.processors.cache.IgniteCacheP2pUnmarshallingErrorTest;
import org.apache.ignite.internal.processors.cache.IgniteCacheP2pUnmarshallingNearErrorTest;
import org.apache.ignite.internal.processors.cache.IgniteCacheP2pUnmarshallingRebalanceErrorTest;
import org.apache.ignite.internal.processors.cache.IgniteCacheP2pUnmarshallingTxErrorTest;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * Checks behavior on exception while unmarshalling key.
 */
@RunWith(AllTests.class)
public class IgniteCacheP2pUnmarshallingErrorTestSuite {
    /**
     * @return Suite.
     */
    public static TestSuite suite() {
        return suite(null);
    }

    /**
     * @param ignoredTests Tests don't include in the execution.
     * @return Test suite.
     */
    public static TestSuite suite(Set<Class> ignoredTests) {
        TestSuite suite = new TestSuite("P2p Unmarshalling Test Suite");

        GridTestUtils.addTestIfNeeded(suite, IgniteCacheP2pUnmarshallingErrorTest.class, ignoredTests);
        GridTestUtils.addTestIfNeeded(suite, IgniteCacheP2pUnmarshallingNearErrorTest.class, ignoredTests);
        GridTestUtils.addTestIfNeeded(suite, IgniteCacheP2pUnmarshallingRebalanceErrorTest.class, ignoredTests);
        GridTestUtils.addTestIfNeeded(suite, IgniteCacheP2pUnmarshallingTxErrorTest.class, ignoredTests);

        return suite;
    }
}
