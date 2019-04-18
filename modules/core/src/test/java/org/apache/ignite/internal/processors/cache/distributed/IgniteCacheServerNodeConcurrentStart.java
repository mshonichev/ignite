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

package org.apache.ignite.internal.processors.cache.distributed;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheMode.REPLICATED;
import static org.apache.ignite.cache.CacheRebalanceMode.SYNC;

/**
 *
 */
@RunWith(JUnit4.class)
public class IgniteCacheServerNodeConcurrentStart extends GridCommonAbstractTest {
    /** */
    private static final int ITERATIONS = 2;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        ((TcpDiscoverySpi)cfg.getDiscoverySpi()).setIpFinderCleanFrequency(getTestTimeout() * 2);

        ((TcpCommunicationSpi)cfg.getCommunicationSpi()).setSharedMemoryPort(-1);

        CacheConfiguration ccfg1 = new CacheConfiguration(DEFAULT_CACHE_NAME);

        ccfg1.setName("cache-1");
        ccfg1.setCacheMode(REPLICATED);
        ccfg1.setRebalanceMode(SYNC);

        CacheConfiguration ccfg2 = new CacheConfiguration(DEFAULT_CACHE_NAME);

        ccfg2.setName("cache-2");
        ccfg2.setCacheMode(PARTITIONED);
        ccfg2.setRebalanceMode(SYNC);
        ccfg2.setBackups(2);

        CacheConfiguration ccfg3 = new CacheConfiguration(DEFAULT_CACHE_NAME);

        ccfg3.setName("cache-3");
        ccfg3.setCacheMode(PARTITIONED);
        ccfg3.setRebalanceMode(SYNC);
        ccfg3.setBackups(0);

        cfg.setCacheConfiguration(ccfg1, ccfg2, ccfg3);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected long getTestTimeout() {
        return ITERATIONS * 3 * 60_000;
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testConcurrentStart() throws Exception {
        for (int i = 0; i < ITERATIONS; i++) {
            log.info("Iteration: " + i);

            long start = System.currentTimeMillis();

            startGridsMultiThreaded(10, false);

            startGridsMultiThreaded(10, 10);

            awaitPartitionMapExchange();

            stopAllGrids();

            log.info("Iteration finished, time: " + (System.currentTimeMillis() - start) / 1000f);
        }
    }
}
