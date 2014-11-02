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
    private TreeNode<zNode> zktree = null;
    private ZooKeeper zk = null;
    private final org.slf4j.Logger logger;


    public zkDumpZookeeper(String zkServer, String znode) {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        this.zkServer = zkServer;
        this.start_znode = znode;
    }

    public TreeNode<zNode> getZktree() throws Exception {
        try {
            connect();
            dump();
            disconnect();
            return zktree;
        } catch (IOException e) {
            logger.error("Error connecting to " + zkServer);
            throw new Exception("tree is empty");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception("tree is empty");
        }
    }

    private void connect() throws IOException, InterruptedException {
        logger.debug("connect to zookeeper server");
        zk = new ZooKeeper(zkServer + start_znode, 100000, this);
        while (!zk.getState().isConnected()) {
            logger.info("Connecting to " + zkServer + " with chroot " + start_znode);
            Thread.sleep(500L);
        }
    }

    private void disconnect() throws InterruptedException {
        logger.debug("disconnect from zookeeper server");
        zk.close();
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
