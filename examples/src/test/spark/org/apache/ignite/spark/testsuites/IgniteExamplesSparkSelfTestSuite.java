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

package org.apache.ignite.spark.testsuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import org.apache.ignite.spark.examples.IgniteDataFrameSelfTest;
import org.apache.ignite.spark.examples.JavaIgniteDataFrameSelfTest;
import org.apache.ignite.spark.examples.SharedRDDExampleSelfTest;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_OVERRIDE_MCAST_GRP;

/**
 * Examples test suite.
 * <p>
 * Contains only Spring ignite examples tests.
 */
@RunWith(AllTests.class)
public class IgniteExamplesSparkSelfTestSuite {
    /**
     * @return Suite.
     */
    public static TestSuite suite() {
        System.setProperty(IGNITE_OVERRIDE_MCAST_GRP,
            GridTestUtils.getNextMulticastGroup(IgniteExamplesSparkSelfTestSuite.class));

        TestSuite suite = new TestSuite("Ignite Spark Examples Test Suite");

        suite.addTest(new JUnit4TestAdapter(SharedRDDExampleSelfTest.class));
        suite.addTest(new JUnit4TestAdapter(IgniteDataFrameSelfTest.class));
        suite.addTest(new JUnit4TestAdapter(JavaIgniteDataFrameSelfTest.class));

        return suite;
    }
}
