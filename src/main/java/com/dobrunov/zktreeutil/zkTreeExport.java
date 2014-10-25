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


    public zkTreeExport(String zkServer, String znode, String outputDir) {
        this.zkServer = zkServer;
        this.outputDir = outputDir;
        this.znode = znode;
    }

    public void go() {
        System.out.println("dumping data from zookeeper");
        System.out.println("zookeeper server: " + zkServer);
        System.out.println("reading from zookeeper path: " + znode);
        System.out.println("dumping to local directory: " + outputDir);

        try {
            ZooKeeper zk = new ZooKeeper(zkServer + znode, 10000, this);
            while (!zk.getState().isConnected()) {
                System.out.println("connecting to " + zkServer + " with chroot " + znode);
                Thread.sleep(500L);
            }
            exportChild(zk);
        } catch (IOException e) {
            System.err.println("error connecting to " + zkServer);
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

        System.out.println("znodePath: " + znodePath);
        System.out.println("outputPath: " + outputPath);
        String currznode = znodePath.length() == 0 ? "/" : znodePath;
        List<String> children = zk.getChildren(currznode, false);
        if (!children.isEmpty()) {

            // ensure parent dir is created
            File f = new File(outputPath);
            boolean s = f.mkdirs();

            // this znode is a dir, so ensure the directory is created and build a __znode value in its dir
            writeZnode(zk, outputPath + "/_znode", currznode);

            for (String c : children) {
                System.out.println("c: " + c);
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
