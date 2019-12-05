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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.TestRecordingCommunicationSpi;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.transactions.Transaction;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.apache.ignite.cache.CacheWriteSynchronizationMode.FULL_SYNC;

/**
 *
 */
public class Bench extends GridCommonAbstractTest {
    /** Worst. */
    private volatile long worst;

    /** Mutex. */
    private final Object mux = new Object();

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        TestRecordingCommunicationSpi commSpi = new TestRecordingCommunicationSpi();

        cfg.setCommunicationSpi(commSpi);

        CacheConfiguration<Integer, Integer> ccfg = new CacheConfiguration<>();

        ccfg.setName(DEFAULT_CACHE_NAME);
        ccfg.setWriteSynchronizationMode(FULL_SYNC);
        ccfg.setBackups(1);
        ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        ccfg.setAffinity(new Map4PartitionsTo4NodesAffinityFunction());

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
        int nodes = 4;
        int txDuration = 10_000;

        startGridsMultiThreaded(nodes, true).cluster().active(true);

        Ignite failed = grid(3);

        int multiplicator = 64;

        AtomicInteger key_from = new AtomicInteger();

        AtomicBoolean finished = new AtomicBoolean();

        IgniteCache<Integer, Integer> failedCache = failed.getOrCreateCache(DEFAULT_CACHE_NAME);

        Random r = new Random();

        IgniteInternalFuture<?> nearThenNearFut = multithreadedAsync(() -> {
            try {
                U.sleep(r.nextInt(txDuration * 2));

                List<Integer> keys = nearKeys(failedCache, 100, key_from.addAndGet(100_000));

                int idx = 0;
                long max = 0;

                Ignite primary = primaryNode(keys.get(0), DEFAULT_CACHE_NAME);

                IgniteCache<Integer, Integer> primaryCache = primary.getOrCreateCache(DEFAULT_CACHE_NAME);

                while (!finished.get()) {
                    Integer key0 = keys.get(idx++);
                    Integer key1 = keys.get(idx++);

                    long started = System.currentTimeMillis();

                    try (Transaction tx = primary.transactions().txStart()) {
                        primaryCache.put(key0, key0);

                        U.sleep(txDuration);

                        primaryCache.put(key1, key1);

                        tx.commit();
                    }

                    long res = System.currentTimeMillis() - started;

                    log.info("Transactionh finished [duration=" + res + "]");

                    if (res > max)
                        max = res;

                    assertEquals(key0, primaryCache.get(key0));
                    assertEquals(key1, primaryCache.get(key1));
                }

                synchronized (mux) {
                    if (worst < max)
                        worst = max;
                }
            }
            catch (Exception e) {
                fail();
            }
        }, multiplicator);

        IgniteInternalFuture<?> nearThenBackupFut = multithreadedAsync(() -> {
            try {
                U.sleep(r.nextInt(txDuration * 2));

                List<Integer> keys0 = nearKeys(failedCache, 100, key_from.addAndGet(100_000));
                List<Integer> keys1 = backupKeys(failedCache, 100, key_from.addAndGet(100_000));

                int idx = 0;
                long max = 0;

                Ignite primary = primaryNode(keys0.get(0), DEFAULT_CACHE_NAME);

                IgniteCache<Integer, Integer> primaryCache = primary.getOrCreateCache(DEFAULT_CACHE_NAME);

                while (!finished.get()) {
                    Integer key0 = keys0.get(idx++);
                    Integer key1 = keys1.get(idx++);

                    long started = System.currentTimeMillis();

                    try (Transaction tx = primary.transactions().txStart()) {
                        primaryCache.put(key0, key0);

                        U.sleep(txDuration);

                        primaryCache.put(key1, key1);

                        tx.commit();
                    }

                    long res = System.currentTimeMillis() - started;

                    log.info("Transactionh finished [duration=" + res + "]");

                    if (res > max)
                        max = res;

                    assertEquals(key0, primaryCache.get(key0));
                    assertEquals(key1, primaryCache.get(key1));
                }

                synchronized (mux) {
                    if (worst < max)
                        worst = max;
                }
            }
            catch (Exception e) {
                fail();
            }
        }, multiplicator);

        U.sleep(txDuration * 2);

        failed.close(); // Stopping node.

        U.sleep(txDuration * 5);

        finished.set(true);

        nearThenNearFut.get();
        nearThenBackupFut.get();

        synchronized (mux) {
            log.info("Worst case is " + worst); // master ~ 19830 ms, ignite-9913 ~ 10294 ms
        }
    }

    /**
     *
     */
    private static class Map4PartitionsTo4NodesAffinityFunction extends RendezvousAffinityFunction {
        /**
         * Default constructor.
         */
        public Map4PartitionsTo4NodesAffinityFunction() {
            super(false, 4);
        }

        /** {@inheritDoc} */
        @Override public List<List<ClusterNode>> assignPartitions(AffinityFunctionContext affCtx) {
            List<List<ClusterNode>> res = new ArrayList<>(4);

            // Partitions by owners (node{parts}): 0{0,3}, 1{0,1}, 2{1,2}, 3{2,3}
            if (affCtx.currentTopologySnapshot().size() == 4) {
                List<ClusterNode> p0 = new ArrayList<>();

                p0.add(affCtx.currentTopologySnapshot().get(0));
                p0.add(affCtx.currentTopologySnapshot().get(1));

                List<ClusterNode> p1 = new ArrayList<>();

                p1.add(affCtx.currentTopologySnapshot().get(1));
                p1.add(affCtx.currentTopologySnapshot().get(2));

                List<ClusterNode> p2 = new ArrayList<>();

                p2.add(affCtx.currentTopologySnapshot().get(2));
                p2.add(affCtx.currentTopologySnapshot().get(3));

                List<ClusterNode> p3 = new ArrayList<>();

                p3.add(affCtx.currentTopologySnapshot().get(3));
                p3.add(affCtx.currentTopologySnapshot().get(0));

                res.add(p0);
                res.add(p1);
                res.add(p2);
                res.add(p3);
            }

            return res;
        }
    }
}
