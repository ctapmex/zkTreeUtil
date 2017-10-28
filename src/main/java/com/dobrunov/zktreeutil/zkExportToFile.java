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

import java.io.*;

public class zkExportToFile implements Job {
    private String zkServer;
    private String output_file;
    private String start_znode;
    private TreeNode<zNode> zktree;
    private final org.slf4j.Logger logger;


    public zkExportToFile(String zkServer, String znode, String output_file) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.output_file = output_file;
        this.start_znode = znode;
    }

    public void go() {
        try {
            zkDumpZookeeper dump = new zkDumpZookeeper(zkServer, start_znode);
            zktree = dump.getZktree();
            writeFile();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void writeFile() {
        logger.info("begin write zookeeper tree to file " + output_file);
        Writer writer = null;
        try {
            writer = new FileWriter(output_file);
            for (TreeNode<zNode> znode : zktree) {
                writer.write("path=" + start_znode + znode.data.path);
                if (znode.data.data != null && znode.data.data.length > 0) {
                    String str = new String(znode.data.data);
                    if (!str.equals("null")) {
                        writer.write("\t");
                        writer.write("val=" + str);
                    }
                }

                if (znode.data.stat.getEphemeralOwner() != 0) {
                    writer.write("\t");
                    writer.write("type='ephemeral'");
                }
                writer.write(System.lineSeparator());
            }
            writer.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                }
            }
        }
        logger.info("end write zookeeper tree to file " + output_file);
    }

}
