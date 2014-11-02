package com.dobrunov.zktreeutil;

/**
 * Created by ctapmex on 24.10.2014.
 */

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
            logger.info("write zookeeper tree to folder " + outputDir);
            for (TreeNode<zNode> znode : zktree) {
                if (znode.data.has_children) {
                    File f = new File(outputDir + znode.data.path);
                    boolean s = f.mkdirs();
                }
                writeZnode(znode.data);
            }
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
