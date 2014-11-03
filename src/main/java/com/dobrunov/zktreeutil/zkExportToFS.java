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

import java.io.File;
import java.io.FileOutputStream;

/**
 * Export zookeeper tree to file system
 */
public class zkExportToFS implements Job {

    private String zkServer;
    private String outputDir;
    private String start_znode;
    private final org.slf4j.Logger logger;


    public zkExportToFS(String zkServer, String znode, String outputDir) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.outputDir = outputDir;
        this.start_znode = znode;
    }

    public void go() {
        zkDumpZookeeper dump = new zkDumpZookeeper(zkServer, start_znode);
        try {
            TreeNode<zNode> zktree = dump.getZktree();
            logger.info("begin write zookeeper tree to folder " + outputDir);
            for (TreeNode<zNode> znode : zktree) {
                if (znode.data.has_children) {
                    File f = new File(outputDir + znode.data.path);
                    boolean s = f.mkdirs();
                }
                writeZnode(znode.data);
            }
            logger.info("end write zookeeper tree to folder " + outputDir);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void writeZnode(zNode znode) {
        if (znode.data != null && znode.data.length > 0) {
            String str = new String(znode.data);
            if (!str.equals("null")) {
                String outFile = znode.has_children ? "_znode" : znode.path;
                try {
                    FileOutputStream out = new FileOutputStream(outputDir + "\\" + outFile);
                    out.write(znode.data);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

}
