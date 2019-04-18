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
 *
 */

package org.apache.ignite.internal.processors.cache;

import java.util.Collections;
import java.util.Set;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridDhtCacheEntry;
import org.jetbrains.annotations.Nullable;

/**
 * Empty cache map that will never store any entries.
 */
public class GridNoStorageCacheMap implements GridCacheConcurrentMap {
    /** {@inheritDoc} */
    @Nullable @Override public GridCacheMapEntry getEntry(GridCacheContext ctx, KeyCacheObject key) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public GridCacheMapEntry putEntryIfObsoleteOrAbsent(GridCacheContext ctx, AffinityTopologyVersion topVer,
        KeyCacheObject key,
        boolean create,
        boolean touch) {
        if (create)
            return new GridDhtCacheEntry(ctx, topVer, key);
        else
            return null;
    }

    /** {@inheritDoc} */
    @Override public boolean removeEntry(GridCacheEntryEx entry) {
        throw new AssertionError();
    }

    /** {@inheritDoc} */
    @Override public int internalSize() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public int publicSize(int cacheId) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public void incrementPublicSize(CacheMapHolder hld, GridCacheEntryEx e) {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void decrementPublicSize(CacheMapHolder hld, GridCacheEntryEx e) {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public Iterable<GridCacheMapEntry> entries(int cacheId, CacheEntryPredicate... filter) {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override public Set<GridCacheMapEntry> entrySet(int cacheId, CacheEntryPredicate... filter) {
        return Collections.emptySet();
    }
}
