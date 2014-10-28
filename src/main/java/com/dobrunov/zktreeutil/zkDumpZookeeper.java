package com.dobrunov.zktreeutil;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ctapmex on 26.10.2014.
 */
public class zkDumpZookeeper implements Watcher {
    private String zkServer;
    private String start_znode;
    ArrayList<zNode> zktree;
    ZooKeeper zk = null;
    private final org.slf4j.Logger logger;


    public zkDumpZookeeper(String zkServer, String znode) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.start_znode = znode;
        zktree = new ArrayList<>();
    }

    public ArrayList<zNode> getZktree() {
        try {
            connect();
            dump();
            disconnect();
        } catch (IOException e) {
            logger.error("Error connecting to " + zkServer);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return zktree;
    }

    private void connect() throws IOException, InterruptedException {
        logger.debug("connect to zookeeper server");
        zk = new ZooKeeper(zkServer + start_znode, 10000, this);
        while (!zk.getState().isConnected()) {
            logger.info("Connecting to " + zkServer + " with chroot " + start_znode);
            Thread.sleep(500L);
        }
    }

    private void disconnect() throws InterruptedException {
        logger.debug("disconnect from zookeeper server");
        zk.close();
    }

    public void dump() throws Exception {
        logger.info("start dump tree from zookeeper server");
        dumpChild("", "");
        logger.info("end dump tree from zookeeper server");
    }

    private void dumpChild(String znodeParent, String znode) throws Exception {
        String znodePath = (znodeParent.equals("/") ? "" : znodeParent) + "/" + znode;
        List<String> children = zk.getChildren(znodePath, false);

        Stat stat = new Stat();
        byte[] data = zk.getData(znodePath, false, stat);
        zNode z = new zNode(znode, znodePath, data, stat, !children.isEmpty());
        zktree.add(z);

        logger.debug("read znode path: " + znodePath );
        for (String c : children) {
            dumpChild(znodePath, c);
        }
    }

    public void process(WatchedEvent watchedEvent) {
    }

}
