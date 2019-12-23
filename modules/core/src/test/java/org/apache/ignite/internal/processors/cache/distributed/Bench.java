/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.distributed;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.TestRecordingCommunicationSpi;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.transactions.TransactionProxyImpl;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.transactions.Transaction;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.apache.ignite.cache.CacheWriteSynchronizationMode.FULL_SYNC;

/**
 *
 */
public class Bench extends GridCommonAbstractTest {
    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        TestRecordingCommunicationSpi commSpi = new TestRecordingCommunicationSpi();

        cfg.setCommunicationSpi(commSpi);

        CacheConfiguration<Integer, Integer> ccfg = new CacheConfiguration<>();

        ccfg.setName(DEFAULT_CACHE_NAME);
        ccfg.setWriteSynchronizationMode(FULL_SYNC);
        ccfg.setBackups(2);
        ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        ccfg.setAffinity(new RendezvousAffinityFunction());

        cfg.setCacheConfiguration(ccfg);

        DataStorageConfiguration cfg1 = new DataStorageConfiguration();

        DataRegionConfiguration drCfg = new DataRegionConfiguration();

        drCfg.setPersistenceEnabled(true);

        cfg.setActiveOnStart(false);
        cfg.setAutoActivationEnabled(false);

        cfg1.setDefaultDataRegionConfiguration(drCfg);

        cfg.setDataStorageConfiguration(cfg1);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        cleanPersistenceDir();
    }

    /**
     * @param parts Number of partitions.
     * @return Affinity function.
     */
    protected AffinityFunction affinityFunction(@Nullable Integer parts) {
        return new RendezvousAffinityFunction(false,
            parts == null ? RendezvousAffinityFunction.DFLT_PARTITION_COUNT : parts);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /**
     *
     */
    @Test
    public void bench() throws Exception {
        AtomicLong lotal = new AtomicLong();

        int warmup = 10;
        int checks = 10;

        for (int i = 0; i < (checks + warmup); i++) {
            int nodes = 4;

            startGridsMultiThreaded(nodes, true).cluster().active(true);

            if (i >= warmup) {
                IgniteEx crd = grid(0);

                IgniteInternalFuture<AffinityTopologyVersion> topFut =
                    crd.context().cache().context().exchange().affinityReadyFuture(new AffinityTopologyVersion(5));

                topFut.listen(future -> {
                    long duration =
                        System.currentTimeMillis() - crd.context().cache().context().exchange().lastFinishedFuture().initTime;

                    lotal.addAndGet(duration);

                    log.info("Spend = " + duration + " ms.");
                });
            }

            IgniteEx failed = grid(3);

            IgniteCache<Integer, Integer> cache = failed.getOrCreateCache(DEFAULT_CACHE_NAME);

            Transaction tx = failed.transactions().txStart();

            cache.put(i, i);

            ((TransactionProxyImpl<Integer, Integer>)tx).tx().prepare(true);

            failed.close(); // Stopping node.

            awaitPartitionMapExchange();

            stopAllGrids();
        }

        log.info("Avg is " + (lotal.get() / checks) + " ms."); // ignite-12272 ~ 16 ms, master ~ 110 ms.
    }
}
