/**
 * Copyright 2014 Aleksey Dobrunov
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dobrunov.zktreeutil;

import org.apache.commons.cli.*;

public class zkTreeUtilMain {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(zkTreeUtilMain.class);

    public static void main(String[] args) {
        Options options = initOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            usage(options);
        }
        Job job = buildJob(options, cmd);
        if (job != null) {
            job.go();
        }
    }

    public static Job buildJob(Options options, CommandLine cmd) {
        Job job = null;
        if (!cmd.hasOption("z")) {
            usage(options);
        } else {

            String server = cmd.getOptionValue("z");
            String znode = "";
            if (cmd.hasOption("p")) {
                znode = cmd.getOptionValue("p");
            }

            if (cmd.hasOption("e") && cmd.hasOption("od")) {
                String output_dir = cmd.getOptionValue("od");
                job = new zkExportToFS(server, znode, output_dir);
            } else if (cmd.hasOption("e") && cmd.hasOption("of")) {
                String output_file = cmd.getOptionValue("of");
                job = new zkExportToFile(server, znode, output_file);
            } else if (cmd.hasOption("e") && cmd.hasOption("ox")) {
                String output_xfile = cmd.getOptionValue("ox");
                job = new zkExportToXmlFile(server, znode, output_xfile);
            } else {
                usage(options);
            }
        }
        return job;
    }

    public static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("zkTreeUtil", options);
        System.exit(1);
    }

    public static Options initOptions() {
        Options options = new Options();

        options.addOption("e", "export", false, "exports the zookeeper tree");

        Option outdir = OptionBuilder.withArgName("dir").hasArg()
                .withDescription("output directory to which znode information should be written (must be a normal, empty directory)")
                .create("od");
        outdir.setLongOpt("output-dir");
        options.addOption(outdir);

        Option plainfile = OptionBuilder.withArgName("filename").hasArg()
                .withDescription("output file to which znode information should be written")
                .create("of");
        plainfile.setLongOpt("output-file");
        options.addOption(plainfile);

        Option xmlfile = OptionBuilder.withArgName("filename").hasArg()
                .withDescription("output xml-file to which znode information should be written")
                .create("ox");
        xmlfile.setLongOpt("output-xmlfile");
        options.addOption(xmlfile);

        Option znodepath = OptionBuilder.withArgName("znodepath").hasArg()
                .withDescription("path to the zookeeper subtree rootnode.")
                .create("p");
        znodepath.setLongOpt("path");
        options.addOption(znodepath);

        Option server = OptionBuilder.withArgName("zkhosts").hasArg().isRequired(true)
                .withDescription("zookeeper remote servers (ie \"localhost:2181\")")
                .create("z");
        server.setLongOpt("zookeeper");
        options.addOption(server);

        return options;
    }
}
