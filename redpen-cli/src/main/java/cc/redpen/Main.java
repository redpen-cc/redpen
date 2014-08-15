/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen;

import cc.redpen.config.Configuration;
import cc.redpen.distributor.ResultDistributor;
import cc.redpen.distributor.ResultDistributorFactory;
import cc.redpen.formatter.Formatter;
import cc.redpen.model.DocumentCollection;
import cc.redpen.parser.Parser;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing main method called from command line.
 */
public final class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {
        super();
    }

    public static void main(String[] args) throws RedPenException {
        Options options = new Options();
        options.addOption("h", "help", false, "help");

        OptionBuilder.withLongOpt("format");
        OptionBuilder.withDescription("input data format");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("FORMAT");
        options.addOption(OptionBuilder.create("f"));

        OptionBuilder.withLongOpt("input");
        OptionBuilder.withDescription("input file");
        OptionBuilder.hasOptionalArgs();
        OptionBuilder.withArgName("INPUT FILE");
        options.addOption(OptionBuilder.create("i"));

        OptionBuilder.withLongOpt("conf");
        OptionBuilder.withDescription("configuration file");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("c"));

        OptionBuilder.withLongOpt("result-format");
        OptionBuilder.withDescription("output result format");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("RESULT FORMAT");
        options.addOption(OptionBuilder.create("r"));

        options.addOption("v", "version", false,
                "print the version information and exit");

        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = null;

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("Error occurred in parsing command line options ");
            printHelp(options);
            System.exit(-1);
        }

        String inputFormat = "plain";
        String[] inputFileNames = null;
        String configFileName = "";
        String resultFormat = "plain";
        Parser.Type parserType;
        Formatter.Type outputFormat;

        if (commandLine.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }
        if (commandLine.hasOption("v")) {
            System.out.println("1.0");
            System.exit(0);
        }
        if (commandLine.hasOption("f")) {
            inputFormat = commandLine.getOptionValue("f");
        }
        if (commandLine.hasOption("i")) {
            inputFileNames = commandLine.getOptionValues("i");
        }
        if (commandLine.hasOption("c")) {
            configFileName = commandLine.getOptionValue("c");
        }
        if (commandLine.hasOption("r")) {
            resultFormat = commandLine.getOptionValue("r");
        }

        ConfigurationLoader configLoader = new ConfigurationLoader();
        Configuration conf = configLoader.loadConfiguration(configFileName);
        if (conf == null) {
            LOG.error("Failed to initialize the RedPen configuration.");
            System.exit(-1);
        }

        parserType = Parser.Type.valueOf(inputFormat.toUpperCase());
        outputFormat = Formatter.Type.valueOf(resultFormat.toUpperCase());

        DocumentCollection documentCollection =
                DocumentGenerator.generate(inputFileNames, conf, parserType);

        if (documentCollection == null) {
            LOG.error("Failed to create a DocumentCollection object");
            System.exit(-1);
        }
        ResultDistributor distributor =
                ResultDistributorFactory.createDistributor(outputFormat, System.out);

        RedPen redPen = new RedPen.Builder()
                .setConfiguration(conf)
                .setResultDistributor(distributor)
                .build();

        redPen.check(documentCollection);

        System.exit(0);
    }

    private static void printHelp(Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ParseArgs", opt);
    }
}
