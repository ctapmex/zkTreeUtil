package com.dobrunov.zktreeutil;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ctapmex on 29.10.2014.
 */
public class zkExportToFile implements Job {
    private String zkServer;
    private String output_file;
    private String start_znode;
    private ArrayList<zNode> list;
    private final org.slf4j.Logger logger;


    public zkExportToFile(String zkServer, String znode, String output_file) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.output_file = output_file;
        this.start_znode = znode;
    }

    public void go() {
        zkDumpZookeeper dump = new zkDumpZookeeper(zkServer, start_znode);
        list = dump.getZktree();
        writeFile();
    }

    private void writeFile() {
        logger.info("write zookeeper tree to file " + output_file);
        Writer writer = null;
        try {
            writer = new FileWriter(output_file);
            for (zNode znode : list) {
                writer.write("path=" + start_znode + znode.path);
                writer.write("\t");
                if (znode.data != null && znode.data.length > 0) {
                    String str = new String(znode.data);
                    if (!str.equals("null")) {
                        writer.write("val=" + str);
                    }
                }
                writer.write("\t");
                if (znode.stat.getEphemeralOwner() != 0) {
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
