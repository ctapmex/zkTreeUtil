package com.dobrunov.zktreeutil;

/**
 * Created by ctapmex on 24.10.2014.
 */

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

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
            zk.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void process(WatchedEvent watchedEvent) {
    }

}
