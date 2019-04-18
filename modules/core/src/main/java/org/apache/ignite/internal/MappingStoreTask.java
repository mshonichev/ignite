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

package org.apache.ignite.internal;

import org.apache.ignite.internal.processors.marshaller.MarshallerMappingItem;
import org.apache.ignite.internal.util.lang.GridPlainRunnable;

/**
 * Task is used in {@link MarshallerContextImpl#onMappingAccepted(MarshallerMappingItem)}
 * to offload storing mapping data into file system from discovery thread.
 */
class MappingStoreTask implements GridPlainRunnable {
    /** Store to put item to. */
    private final MarshallerMappingFileStore fileStore;

    /** */
    private final byte platformId;

    /** */
    private final int typeId;

    /** */
    private final String clsName;

    /**
     * @param fileStore File store.
     * @param platformId Platform id.
     * @param typeId Type id.
     * @param clsName Class name.
     */
    MappingStoreTask(MarshallerMappingFileStore fileStore, byte platformId, int typeId, String clsName) {
        assert clsName != null;

        this.fileStore = fileStore;
        this.platformId = platformId;
        this.typeId = typeId;
        this.clsName = clsName;
    }

    /** {@inheritDoc} */
    @Override public void run() {
        fileStore.writeMapping(platformId, typeId, clsName);
    }
}
