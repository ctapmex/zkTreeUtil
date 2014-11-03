==========================================
zkTreeUtil - ZooKeeper Tree Utility
Author: Dobrunov Aleksey (ctapmex)
Homepage: https://github.com/ctapmex/zkTreeUtil
==========================================

(translated by https://translate.google.ru)

zkTreeUtil application for data/tree stored in ZooKeeper. 
The current main task - to export the contents of ZooKeeper.

usage: zkTreeUtil
 -e,--export                       exports the zookeeper tree
 -od,--output-dir <dir>            output directory to which znode
                                   information should be written (must be
                                   a normal, empty directory)
 -of,--output-file <filename>      output file to which znode information
                                   should be written
 -xf,--output-xmlfile <filename>   output xml-file to which znode
                                   information should be written
 -p,--path <znodepath>             path to the zookeeper subtree rootnode.
 -z,--zookeeper <zkhosts>          zookeeper remote servers (ie
                                   "localhost:2181")

Export is available in three formats: 
  - The file structure (output-dir) 
     As specified for the output directory is created for each node or folder 
     or file. Repeating the tree structure. If the node is set 
     (value), it is stored in a file. For nodes/folders the value stored in folders 
     file '_znode'. 
     The main limitation of this format - file redundant system does not support all 
     characters in the name. For example ':'. 
  - A flat file (output-file) 
     In the set of output files created by the string of the form 
     path=/ss val=dd type='ephemeral' 
     path - the path node. 
     val - the value, if specified 
     type - the type of node. ephemeral - temporary node. 
  - Xml file 
     In the set of output files created nodes (znode), keeping all 
     attribute information

To be able to export a certain subtree ZooKeeper, is the key 'p'. 
The parameter 'z' is address of the server supports specifying multiple servers 
ZooKeeper cluster, separated by commas. For example, 127.0.0.1:3000,127.0.0.1:3001

examples:

zktreeutil -z 127.0.0.1:2181 --export -xf d:\test.xml -p /ss
zktreeutil -z 127.0.0.1:2181 --export -od d:\test

Build instructions
------------------
1. cd into this directory
2. mvn clean package
3. 'zkTreeUtil' created in target\distrib directory
