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

package org.apache.ignite.internal.test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.processors.resource.GridSpringResourceContext;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.transactions.Transaction;

/**
 *
 */
public class LongTxStreamerIgniteApplication {
    /** Config path. */
    public static final String CONFIG_PATH = "/mnt/client_app/ignite-client-config.xml";

    /** Terminated. */
    private static volatile boolean terminated;

    /**
     * @param args Args.
     */
    public static void main(String[] args) throws IgniteCheckedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> terminated = true));

        IgniteBiTuple<IgniteConfiguration, GridSpringResourceContext> cfgs = IgnitionEx.loadConfiguration(CONFIG_PATH);

        IgniteConfiguration cfg = cfgs.get1();

        cfg.setClientMode(true);

        System.out.println("Starting Ignite client...");

        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.println("Creating cache...");

            IgniteCache<Integer, Integer> cache = ignite.createCache("test-cache");

            try (Transaction tx = ignite.transactions().txStart()) {
                for (int i = 0; i < 1000; i++)
                    cache.put(i, i);

                while (!terminated)
                    U.sleep(100);
            }

            System.out.println("Ignite Client Finish.");
        }
    }
}
