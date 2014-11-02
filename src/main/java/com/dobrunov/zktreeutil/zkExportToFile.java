package com.dobrunov.zktreeutil;

import java.io.*;

/**
 * Created by ctapmex on 29.10.2014.
 */
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
        logger.info("write zookeeper tree to file " + output_file);
        Writer writer = null;
        try {
            writer = new FileWriter(output_file);
            for (TreeNode<zNode> znode : zktree) {
                writer.write("path=" + start_znode + znode.data.path);
                writer.write("\t");
                if (znode.data.data != null && znode.data.data.length > 0) {
                    String str = new String(znode.data.data);
                    if (!str.equals("null")) {
                        writer.write("val=" + str);
                    }
                }
                writer.write("\t");
                if (znode.data.stat.getEphemeralOwner() != 0) {
                    writer.write("type='ephemeral'");
                }
                writer.write("\r\n");
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
    }

}
