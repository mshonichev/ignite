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

package org.apache.ignite.spi.discovery.zk.internal;

import org.apache.ignite.internal.util.GridSpinBusyLock;

/**
 *
 */
abstract class ZkAbstractCallabck {
    /** */
    final ZkRuntimeState rtState;

    /** */
    private final ZookeeperDiscoveryImpl impl;

    /** */
    private final GridSpinBusyLock busyLock;

    /**
     * @param rtState Runtime state.
     * @param impl Discovery impl.
     */
    ZkAbstractCallabck(ZkRuntimeState rtState, ZookeeperDiscoveryImpl impl) {
        this.rtState = rtState;
        this.impl = impl;

        busyLock = impl.busyLock;
    }

    /**
     * @return {@code True} if is able to start processing.
     */
    final boolean onProcessStart() {
        boolean start = rtState.errForClose == null && busyLock.enterBusy();

        if (!start) {
            assert rtState.errForClose != null;

            onStartFailed();

            return false;
        }

        return true;
    }

    /**
     *
     */
    void onStartFailed() {
        // No-op.
    }

    /**
     *
     */
    final void onProcessEnd() {
        busyLock.leaveBusy();
    }

    /**
     * @param e Error.
     */
    final void onProcessError(Throwable e) {
        impl.onFatalError(busyLock, e);
    }
}
