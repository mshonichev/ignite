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

package org.apache.ignite.tensorflow.submitter.command;

import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.tensorflow.cluster.TensorFlowClusterGatewayManager;
import picocli.CommandLine;

/**
 * Command "attach" that is used to attach to running TensorFlow cluster and receive output of the user script.
 */
@CommandLine.Command(
    name = "attach",
    description = "Attaches to running TensorFlow cluster (user script process).",
    mixinStandardHelpOptions = true
)
public class AttachCommand extends AbstractCommand {
    /** TensorFlow cluster identifier. */
    @CommandLine.Parameters(paramLabel = "CLUSTER_ID", description = "Cluster identifier.")
    private UUID clusterId;

    /** {@inheritDoc} */
    @Override public void run() {
        try (Ignite ignite = getIgnite()) {
            TensorFlowClusterGatewayManager mgr = new TensorFlowClusterGatewayManager(ignite);

            mgr.listenToClusterUserScript(clusterId, System.out::println, System.err::println);
        }
    }

    /** */
    public void setClusterId(UUID clusterId) {
        this.clusterId = clusterId;
    }
}
