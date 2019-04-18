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

package org.apache.ignite.internal.processors.platform.client;

import org.apache.ignite.internal.binary.BinaryRawWriterEx;

/**
 * Boolean response.
 */
public class ClientBooleanResponse extends ClientResponse {
    /** */
    private final boolean val;

    /**
     * Constructor.
     *
     * @param reqId Request id.
     */
    public ClientBooleanResponse(long reqId, boolean val) {
        super(reqId);

        this.val = val;
    }

    /** {@inheritDoc} */
    @Override public void encode(BinaryRawWriterEx writer) {
        super.encode(writer);

        writer.writeBoolean(val);
    }
}
