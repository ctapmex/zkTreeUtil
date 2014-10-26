package com.dobrunov.zktreeutil;

/**
 * Created by ctapmex on 24.10.2014.
 */

import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class zkTreeExport implements Watcher, Job {

    private String zkServer;
    private String outputDir;
    private String znode;
    private final org.slf4j.Logger logger;

    public zkTreeExport(String zkServer, String znode, String outputDir) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.outputDir = outputDir;
        this.znode = znode;
    }

    public void go() {
        logger.info("Export data from zookeeper");
        logger.info("Zookeeper server: " + zkServer);
        logger.info("Reading from zookeeper path: " + znode);
        logger.info("Export to local directory: " + outputDir);

        try {
            ZooKeeper zk = new ZooKeeper(zkServer + znode, 10000, this);
            while (!zk.getState().isConnected()) {
                logger.info("Connecting to " + zkServer + " with chroot " + znode);
                Thread.sleep(500L);
            }
            exportChild(zk);
        } catch (IOException e) {
            logger.error("Error connecting to " + zkServer);
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void exportChild(ZooKeeper zk) {
        try {
            dumpChild(zk, outputDir + znode, "", "");
            zk.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void dumpChild(ZooKeeper zk, String outputPath, String znodeParent, String znode) throws Exception {

        String znodePath = znodeParent + znode;

        logger.debug("znodePath: " + znodePath);
        logger.debug("outputPath: " + outputPath);
        String currznode = znodePath.length() == 0 ? "/" : znodePath;
        List<String> children = zk.getChildren(currznode, false);
        if (!children.isEmpty()) {

            // ensure parent dir is created
            File f = new File(outputPath);
            boolean s = f.mkdirs();

            // this znode is a dir, so ensure the directory is created and build a __znode value in its dir
            writeZnode(zk, outputPath + "/_znode", currznode);

            for (String c : children) {
                logger.debug("c: " + c);
                dumpChild(zk, outputPath + "/" + c, znodePath + "/", c);
            }
        } else {
            // this znode has no contents to write a plane file with the znode contents here
            writeZnode(zk, outputPath, currznode);
        }
    }

    private void writeZnode(ZooKeeper zk, String outFile, String znode) throws Exception {
        Stat stat = new Stat();
        byte[] data = zk.getData(znode, false, stat);
        if (data != null && data.length > 0 && stat.getEphemeralOwner() == 0) {
            String str = new String(data);
            if (!str.equals("null")) {
                FileOutputStream out = new FileOutputStream(outFile);
                out.write(data);
                out.flush();
                out.close();
            }
        }
    }

    public void process(WatchedEvent watchedEvent) {
    }

}
