package com.dobrunov.zktreeutil;

/**
 * Created by ctapmex on 24.10.2014.
 */

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

            if (cmd.hasOption("e") && cmd.hasOption("o")) {
                String outputDir = cmd.getOptionValue("o");
                String znode = "";
                if (cmd.hasOption("p")) {
                    znode = cmd.getOptionValue("p");
                }
                job = new zkTreeExport(server, znode, outputDir);
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

        Option xmlfile = OptionBuilder.withArgName("dir").hasArg()
                .withDescription("output directory to which znode information should be written (must be a normal, empty directory)")
                .create("o");
        xmlfile.setLongOpt("output-dir");
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
