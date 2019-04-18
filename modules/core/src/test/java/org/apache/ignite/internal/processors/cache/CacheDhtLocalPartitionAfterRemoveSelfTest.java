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

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.processors.cache.distributed.dht.topology.GridDhtLocalPartition;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.ignite.cache.CacheAtomicityMode.TRANSACTIONAL;

/**
 * Test for remove operation.
 */
@RunWith(JUnit4.class)
public class CacheDhtLocalPartitionAfterRemoveSelfTest extends GridCommonAbstractTest {
    /** Grid count. */
    private static final int GRID_CNT = 3;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        CacheConfiguration ccfg = new CacheConfiguration(DEFAULT_CACHE_NAME);

        ccfg.setAtomicityMode(TRANSACTIONAL);
        ccfg.setNearConfiguration(null);

        cfg.setCacheConfiguration(ccfg);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrids(GRID_CNT);
    }

    /**
     * @throws Exception If failed.
     */
    @Test
    public void testMemoryUsage() throws Exception {
        assertEquals(10_000, GridDhtLocalPartition.MAX_DELETE_QUEUE_SIZE);

        IgniteCache<TestKey, Integer> cache = grid(0).cache(DEFAULT_CACHE_NAME);

        for (int i = 0; i < 20_000; ++i)
            cache.put(new TestKey(String.valueOf(i)), i);

        for (int i = 0; i < 20_000; ++i)
            assertEquals((Object)i, cache.getAndRemove(new TestKey(String.valueOf(i))));

        assertEquals(0, cache.size());

        for (int g = 0; g < GRID_CNT; g++) {
            cache = grid(g).cache(DEFAULT_CACHE_NAME);

            for (GridDhtLocalPartition p : dht(cache).topology().localPartitions()) {
                long size = p.dataStore().fullSize();

                assertTrue("Unexpected size: " + size, size <= 32);
            }
        }
    }

    /**
     * Test key.
     */
    private static class TestKey {
        /** Key. */
        private String key;

        /**
         * @param key Key.
         */
        public TestKey(String key) {
            this.key = key;
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return key.hashCode();
        }

        /** {@inheritDoc} */
        @Override public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof TestKey))
                return false;

            return key.equals(((TestKey)obj).key);
        }
    }
}
