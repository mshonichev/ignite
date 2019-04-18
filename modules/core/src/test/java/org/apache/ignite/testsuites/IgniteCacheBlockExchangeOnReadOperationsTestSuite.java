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
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import org.apache.ignite.internal.processors.cache.distributed.CacheBlockOnGetAllTest;
import org.apache.ignite.internal.processors.cache.distributed.CacheBlockOnScanTest;
import org.apache.ignite.internal.processors.cache.distributed.CacheBlockOnSingleGetTest;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * Test suite.
 */
@RunWith(AllTests.class)
public class IgniteCacheBlockExchangeOnReadOperationsTestSuite {
    /**
     * @return IgniteCache test suite.
     */
    public static TestSuite suite() {
        return suite(null);
    }

    /**
     * @param ignoredTests Tests to ignore.
     * @return Test suite.
     */
    public static TestSuite suite(Set<Class> ignoredTests) {
        TestSuite suite = new TestSuite("Do Not Block Read Operations Test Suite");

        suite.addTest(new JUnit4TestAdapter(CacheBlockOnSingleGetTest.class));
        suite.addTest(new JUnit4TestAdapter(CacheBlockOnGetAllTest.class));
        suite.addTest(new JUnit4TestAdapter(CacheBlockOnScanTest.class));

        return suite;
    }
}
