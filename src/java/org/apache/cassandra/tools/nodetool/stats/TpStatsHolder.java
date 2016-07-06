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

package org.apache.cassandra.tools.nodetool.stats;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;

import org.apache.cassandra.tools.NodeProbe;
import org.apache.cassandra.tools.nodetool.stats.StatsHolder;

public class TpStatsHolder implements StatsHolder
{
    public NodeProbe probe;

    @Override
    public Map<String, Object> convert2Map()
    {
        HashMap<String, Object> mpRet = new HashMap<>();
        HashMap<String, Object> mpThreadPools = new HashMap<>();
        HashMap<String, Object> myDroppedMessage = new HashMap<>();

        Multimap<String, String> threadPools = probe.getThreadPools();
        for (Map.Entry<String, String> tpool : threadPools.entries())
        {
            HashMap<String, Object> myThreadPool = new HashMap<>();
            myThreadPool.put("ActiveTasks", probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "ActiveTasks"));
            myThreadPool.put("PendingTasks", probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "PendingTasks"));
            myThreadPool.put("CompletedTasks", probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "CompletedTasks"));
            myThreadPool.put("CurrentlyBlockedTasks", probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "CurrentlyBlockedTasks"));
            myThreadPool.put("TotalBlockedTasks", probe.getThreadPoolMetric(tpool.getKey(), tpool.getValue(), "TotalBlockedTasks"));
            mpThreadPools.put(tpool.getValue(), myThreadPool);
        }
        mpRet.put("ThreadPools", mpThreadPools);

        for (Map.Entry<String, Integer> entry : probe.getDroppedMessages().entrySet())
            myDroppedMessage.put(entry.getKey(), entry.getValue());
        mpRet.put("DroppedMessage", myDroppedMessage);

        return mpRet;
    }
}
