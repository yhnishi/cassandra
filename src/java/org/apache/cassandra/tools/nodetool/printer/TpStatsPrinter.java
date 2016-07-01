/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.tools.nodetool.printer;

import java.io.PrintStream;
import java.util.Map;

import com.google.common.collect.Multimap;

public class TpStatsPrinter extends AbstractPrinter
{
    public static IPrinter from(String format)
    {
        switch (format)
        {
            case "json":
                return new JsonPrinter();
            case "yaml":
                return new YamlPrinter();
            default:
                return new DefaultPrinter();
        }

    }

    public static class DefaultPrinter implements IPrinter<TpStatsHolder>
    {
        @Override
        public void print(TpStatsHolder data, PrintStream out)
        {
            out.printf("%-25s%10s%10s%15s%10s%18s%n", "Pool Name", "Active", "Pending", "Completed", "Blocked", "All time blocked");

            Multimap<String, String> threadPools = data.probe.getThreadPools();
            for (Map.Entry<String, String> tpool : threadPools.entries())
            {
                out.printf("%-25s%10s%10s%15s%10s%18s%n",
                           tpool.getValue(),
                           data.probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "ActiveTasks"),
                           data.probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "PendingTasks"),
                           data.probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "CompletedTasks"),
                           data.probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "CurrentlyBlockedTasks"),
                           data.probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "TotalBlockedTasks"));
            }

            out.printf("%n%-20s%10s%n", "Message type", "Dropped");
            for (Map.Entry<String, Integer> entry : data.probe.getDroppedMessages().entrySet())
                out.printf("%-20s%10s%n", entry.getKey(), entry.getValue());
        }
    }
}
