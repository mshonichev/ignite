# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from ducktape.tests.test import Test

from ignitetest.services.ignite import IgniteService
from ignitetest.services.ignite_client_app import IgniteClientApp


class LongTxSwitchTest(Test):
    NUM_NODES = 3

    """
    Test performs rebalance tests.
    """

    def __init__(self, test_context):
        super(LongTxSwitchTest, self).__init__(test_context=test_context)
        self.ignite = IgniteService(test_context, num_nodes=LongTxSwitchTest.NUM_NODES)
        self.streamer = IgniteClientApp(self.test_context,
                                        java_class_name="org.apache.ignite.internal.test.LongTxStreamerIgniteApplication")
        self.measurer = IgniteClientApp(self.test_context,
                                        java_class_name="org.apache.ignite.internal.test.SingleKeyTxStreamerIgniteApplication")

    def setUp(self):
        # starting all nodes except last.
        for i in range(LongTxSwitchTest.NUM_NODES - 1):
            self.ignite.start_node(self.ignite.nodes[i])

    def teardown(self):
        self.ignite.stop()

    def test(self):
        """
        Test performs add node rebalance test which consists of following steps:
            * Start cluster.
            * Put data to it via IgniteClientApp.
            * Start one more node and awaits for rebalance to finish.
        """
        self.logger.info("Long tx switch benchmark start.")

        self.streamer.start()
        self.measurer.start()

        self.logger.info("Stopping node.")

        self.ignite.stop_node(self.ignite.nodes[1])

        self.logger.info("Stopping load.")

        self.streamer.stop()
        self.measurer.stop()
