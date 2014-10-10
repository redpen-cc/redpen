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
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class containing main method called from command line.
 */
public final class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String PROGRAM = "redpen-cli";

    private static final String VERSION = "0.6";

    private static final int EDEFAULT_LIMIT = 1;

    private Main() {
        super();
    }

    /**
     * When the errors reported by RedPen is more than the specified limit, this method returns 1 otherwise return 0.
     * @param args arguments
     * @throws RedPenException
     */
    public static void main(String[] args) throws RedPenException {
        Options options = new Options();
        options.addOption("h", "help", false, "Displays this help information and exits");

        OptionBuilder.withLongOpt("format");
        OptionBuilder.withDescription("Input file format");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("FORMAT");
        options.addOption(OptionBuilder.create("f"));

        OptionBuilder.withLongOpt("conf");
        OptionBuilder.withDescription("Configuration file (Required)");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("CONF FILE");
        options.addOption(OptionBuilder.create("c"));

        OptionBuilder.withLongOpt("result-format");
        OptionBuilder.withDescription("Output result format");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("RESULT FORMAT");
        options.addOption(OptionBuilder.create("r"));

        OptionBuilder.withLongOpt("limit");
        OptionBuilder.withDescription("error limit number");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("LIMIT NUMBER");
        options.addOption(OptionBuilder.create("l"));

        options.addOption("v", "version", false,
            "Displays version information and exits");

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
        String configFileName = "";
        String resultFormat = "plain";
        int limit = EDEFAULT_LIMIT;
        DocumentParser.Type parserType;
        Formatter.Type outputFormat;

        if (commandLine.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }
        if (commandLine.hasOption("v")) {
            System.out.println(VERSION);
            System.exit(0);
        }
        if (commandLine.hasOption("f")) {
            inputFormat = commandLine.getOptionValue("f");
        }
        if (commandLine.hasOption("c")) {
            configFileName = commandLine.getOptionValue("c");
        }
        if (commandLine.hasOption("r")) {
            resultFormat = commandLine.getOptionValue("r");
        }
        if (commandLine.hasOption("l")) {
            limit = Integer.valueOf(commandLine.getOptionValue("l"));
        }

        String[] inputFileNames = commandLine.getArgs();

        ConfigurationLoader configLoader = new ConfigurationLoader();
        Configuration conf = configLoader.loadConfiguration(configFileName);
        if (conf == null) {
            LOG.error("Failed to initialize the RedPen configuration.");
            System.exit(-1);
        }

        parserType = DocumentParser.Type.valueOf(inputFormat.toUpperCase());
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

        List<ValidationError> errors = redPen.check(documentCollection);
        if (errors.size() > limit) {
            LOG.error("The number of errors \"{}\" is larger than specified (limit is \"{}\").", errors.size(), limit);
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static void printHelp(Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        formatter.printHelp(PROGRAM + " -c <CONF FILE> <INPUT FILE> [<INPUT FILE>]", opt);
    }
}
