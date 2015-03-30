/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
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

import cc.redpen.formatter.*;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Class containing main method called from command line.
 */
public final class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String PROGRAM = "redpen-cli";

    private static final int EDEFAULT_LIMIT = 1;

    private Main() {
        super();
    }

    /**
     * When the errors reported by RedPen is more than the specified limit, this method returns 1 otherwise return 0.
     *
     * @param args arguments
     * @throws RedPenException
     */
    public static void main(String... args) throws RedPenException {
        System.exit(run(args));
    }

    public static int run(String... args) throws RedPenException {
        Options options = new Options();
        options.addOption("h", "help", false, "Displays this help information and exits");

        OptionBuilder.withLongOpt("format");
        OptionBuilder.withDescription("Input file format");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("FORMAT");
        options.addOption(OptionBuilder.create("f"));

        OptionBuilder.withLongOpt("conf");
        OptionBuilder.withDescription("Configuration file (REQUIRED)");
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

        CommandLineParser commandLineParser = new BasicParser();
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("Error occurred in parsing command line options ");
            printHelp(options);
            return -1;
        }

        String inputFormat = "plain";
        String configFileName = "";
        String resultFormat = "plain";
        int limit = EDEFAULT_LIMIT;

        if (commandLine.hasOption("h")) {
            printHelp(options);
            return 0;
        }
        if (commandLine.hasOption("v")) {
            System.out.println(RedPen.VERSION);
            return 0;
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
        File[] inputFiles = new File[inputFileNames.length];
        for (int i = 0; i < inputFileNames.length; i++) {
            inputFiles[i] = new File(inputFileNames[i]);
        }

        DocumentParser parser = DocumentParser.of(inputFormat);

        Formatter formatter;
        switch (resultFormat.toLowerCase()) {
            case "xml":
                formatter = new XMLFormatter();
                break;
            case "plain":
                formatter = new PlainFormatter();
                break;
            case "plain2":
                formatter = new PlainBySentenceFormatter();
                break;
            case "json":
                formatter = new JSONFormatter();
                break;
            case "json2":
                formatter = new JSONBySentenceFormatter();
                break;
            default:
                LOG.error("Unsupported format: " + resultFormat + " - please use xml, plain, plain2, json or json2");
                return -1;
        }

        if (configFileName == null
                || configFileName.length() == 0) {
            LOG.error("Configuration file is not specified.");
            printHelp(options);
            return 1;
        } else if (inputFileNames.length == 0) {
            LOG.error("Input file is not given");
            printHelp(options);
            return 1;
        }

        RedPen redPen;
        List<Document> documents;
        try {
            redPen = new RedPen(new File(configFileName));
            documents = redPen.parse(parser, inputFiles);
        } catch (RedPenException e) {
            LOG.error("Failed to parse input files: " + e);
            return -1;
        }

        Map<Document, List<ValidationError>> documentListMap = redPen.validate(documents);
        String result = formatter.format(documentListMap);
        System.out.println(result);

        long errorCount = documentListMap.values().stream().mapToLong(List::size).sum();

        if (errorCount > limit) {
            LOG.error("The number of errors \"{}\" is larger than specified (limit is \"{}\").", errorCount, limit);
            return 1;
        } else {
            return 0;
        }
    }

    private static void printHelp(Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        PrintWriter pw = new PrintWriter(System.err);
        formatter.printHelp(pw, 80, PROGRAM + " [Options] [<INPUT FILE>]", null, opt, 1, 3, "");
        pw.flush();
    }

}
