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

package org.apache.ignite.internal.processors.platform.client.binary;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.binary.BinaryRawReader;
import org.apache.ignite.internal.processors.platform.client.ClientBooleanResponse;
import org.apache.ignite.internal.processors.platform.client.ClientConnectionContext;
import org.apache.ignite.internal.processors.platform.client.ClientRequest;
import org.apache.ignite.internal.processors.platform.client.ClientResponse;

/**
 * Gets binary type name by id.
 */
public class ClientBinaryTypeNamePutRequest extends ClientRequest {
    /** Platform ID, see org.apache.ignite.internal.MarshallerPlatformIds. */
    private final byte platformId;

    /** Type id. */
    private final int typeId;

    /** Type name. */
    private final String typeName;

    /**
     * Constructor.
     *
     * @param reader Reader.
     */
    public ClientBinaryTypeNamePutRequest(BinaryRawReader reader) {
        super(reader);

        platformId = reader.readByte();
        typeId = reader.readInt();
        typeName = reader.readString();
    }

    /** {@inheritDoc} */
    @Override public ClientResponse process(ClientConnectionContext ctx) {
        try {
            boolean res = ctx.kernalContext().marshallerContext()
                .registerClassName(platformId, typeId, typeName, false);

            return new ClientBooleanResponse(requestId(), res);
        }
        catch (IgniteCheckedException e) {
            throw new IgniteException(e);
        }
    }
}
