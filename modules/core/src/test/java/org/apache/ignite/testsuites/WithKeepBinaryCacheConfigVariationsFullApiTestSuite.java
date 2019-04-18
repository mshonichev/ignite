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

package org.apache.ignite.testsuites;

import junit.framework.TestSuite;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.binary.BinaryMarshaller;
import org.apache.ignite.internal.processors.cache.WithKeepBinaryCacheFullApiTest;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.configvariations.ConfigVariationsTestSuiteBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

/**
 * Test suite for cache API.
 */
@RunWith(AllTests.class)
public class WithKeepBinaryCacheConfigVariationsFullApiTestSuite {
    /**
     * @return Cache API test suite.
     */
    @SuppressWarnings("serial")
    public static TestSuite suite() {
        TestSuite suite = new TestSuite("With Keep Binary Cache Config Variations Full API Test Suite");

        suite.addTest(new ConfigVariationsTestSuiteBuilder(
            "With Keep Binary Cache Test Suite",
            WithKeepBinaryCacheFullApiTest.class)
            .withBasicCacheParams()
            .withIgniteConfigFilters(new IgnitePredicate<IgniteConfiguration>() {
                @Override public boolean apply(IgniteConfiguration cfg) {
                    return cfg.getMarshaller() instanceof BinaryMarshaller;
                }
            })
            .gridsCount(5)
            .backups(1)
            .testedNodesCount(3).withClients()
            .build()
        );

        suite.addTest(new ConfigVariationsTestSuiteBuilder(
            "With Keep Binary Cache with Interceptor Test Suite",
            WithKeepBinaryCacheFullApiTest.class)
            .withBasicCacheParams()
            .withIgniteConfigFilters(new IgnitePredicate<IgniteConfiguration>() {
                @Override public boolean apply(IgniteConfiguration cfg) {
                    return cfg.getMarshaller() instanceof BinaryMarshaller;
                }
            })
            .gridsCount(5)
            .backups(1)
            .testedNodesCount(3).withClients()
            .build()
        );

        return suite;
    }
}
