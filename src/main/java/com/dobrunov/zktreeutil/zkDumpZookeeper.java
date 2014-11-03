/**
 * Copyright 2014 Aleksey Dobrunov
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

package com.dobrunov.zktreeutil;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import java.util.List;

public class zkDumpZookeeper implements Watcher {
    private String zkServer;
    private String start_znode;
    private TreeNode<zNode> zktree = null;
    private ZooKeeper zk = null;
    private final org.slf4j.Logger logger;


    public zkDumpZookeeper(String zkServer, String znode) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.start_znode = znode;
    }

    public TreeNode<zNode> getZktree() throws Exception {
        connect();
        dump();
        disconnect();
        return zktree;
    }

    private void connect() throws Exception {
        logger.debug("connect to zookeeper server");
        try {
            zk = new ZooKeeper(zkServer + start_znode, 10000, this);
            int i = 0;
            while (!zk.getState().isConnected() && i < 10) {
                logger.info("Connecting to " + zkServer + " with chroot " + start_znode);
                Thread.sleep(1000L);
                i++;
            }
            if (i >= 10 && !zk.getState().isConnected()) {
                throw new Exception();
            }

        } catch (Exception e) {
            logger.error("Error connecting to " + zkServer);
            logger.error(e.getMessage());
            throw new Exception();
        }
    }

    private void disconnect() throws Exception {
        logger.debug("disconnect from zookeeper server");
        try {
            zk.close();
        } catch (Exception e) {
            logger.error("Error disconnecting from " + zkServer);
            logger.error(e.getMessage());
            throw new Exception();
        }
    }

    private void dump() throws Exception {
        logger.info("start dump tree from zookeeper server");
        dumpChild("", "", null);
        logger.info("end dump tree from zookeeper server");
    }

    private void dumpChild(String znodeParent, String znode, TreeNode<zNode> tree_node) throws Exception {
        String znodePath = (znodeParent.equals("/") ? "" : znodeParent) + "/" + znode;
        List<String> children = zk.getChildren(znodePath, false);

        Stat stat = new Stat();
        byte[] data = zk.getData(znodePath, false, stat);
        zNode z = new zNode(znode, znodePath, data, stat, !children.isEmpty());
        TreeNode<zNode> tnode;
        if (tree_node != null) {
            tnode = tree_node.addChild(z);
        } else {
            zktree = new TreeNode<>(z);
            tnode = zktree;
        }

        logger.debug("read znode path: " + znodePath);
        for (String c : children) {
            dumpChild(znodePath, c, tnode);
        }
    }

    public void process(WatchedEvent watchedEvent) {
    }

}
