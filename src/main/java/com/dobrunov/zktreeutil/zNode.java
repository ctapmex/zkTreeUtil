package com.dobrunov.zktreeutil;

/**
 * Created by ctapmex on 26.10.2014.
 */

import org.apache.zookeeper.data.Stat;

public class zNode {
    public String name;
    public String path;
    public byte[] data = null;
    public Stat stat;
    public boolean has_children;

    public zNode(String name, String path, byte[] data, Stat stat, boolean has_children) {
        this.name = name;
        this.path = path;
        if (data != null) {
            this.data = data.clone();
        }
        this.stat = stat;
        this.has_children = has_children;
    }

}
