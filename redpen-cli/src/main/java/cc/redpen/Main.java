/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import cc.redpen.util.FormatterUtils;
import cc.redpen.validator.ValidationError;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class containing main method called from command line.
 */
public final class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String PROGRAM = "redpen-cli";

    private static final String DEFAULT_CONFIG_NAME = "redpen-conf";

    private static final int DEFAULT_LIMIT = 1;

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

    @SuppressWarnings("static-access")
    public static int run(String... args) throws RedPenException {
        Options options = new Options();
        options.addOption("h", "help", false, "Displays this help information and exits");

        options.addOption(OptionBuilder.withLongOpt("format")
                .withDescription("Input file format (markdown,plain,wiki)")
                .hasArg()
                .withArgName("FORMAT")
                .create("f"));

        options.addOption(OptionBuilder.withLongOpt("conf")
                .withDescription("Configuration file (REQUIRED)")
                .hasArg()
                .withArgName("CONF FILE")
                .create("c"));

        options.addOption(OptionBuilder.withLongOpt("result-format")
                .withDescription("Output result format (json,json2,plain,plain2,xml)")
                .hasArg()
                .withArgName("RESULT FORMAT")
                .create("r"));

        options.addOption(OptionBuilder.withLongOpt("limit")
                .withDescription("error limit number")
                .hasArg()
                .withArgName("LIMIT NUMBER")
                .create("l"));

        options.addOption(OptionBuilder.withLongOpt("version")
                .withDescription("Displays version information and exits")
                .create("v"));

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
        String configFileName = null;
        String resultFormat = "plain";
        int limit = DEFAULT_LIMIT;

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
        boolean markdownOnly = true;
        for (int i = 0; i < inputFileNames.length; i++) {
            inputFiles[i] = new File(inputFileNames[i]);
            if (!inputFileNames[i].endsWith(".md")) {
                markdownOnly = false;
            }
        }
        if (!commandLine.hasOption("f") && markdownOnly) {
            inputFormat = "markdown";
        }

        DocumentParser parser = DocumentParser.of(inputFormat);

        Formatter formatter = FormatterUtils.getFormatterByName(resultFormat);
        if (formatter == null) {
            LOG.error("Unsupported format: " + resultFormat + " - please use xml, plain, plain2, json or json2");
            return -1;
        }

        File configFile = resolveConfigLocation(configFileName);
        if (configFile == null) {
            LOG.error("Configuration file is not found.");
            printHelp(options);
            return 1;
        }

        if (inputFileNames.length == 0) {
            LOG.error("Input file is not given");
            printHelp(options);
            return 1;
        }

        RedPen redPen;
        List<Document> documents;
        try {
            redPen = new RedPen(configFile);
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

    static File resolveConfigLocation(String configFileName) {
        List<String> pathCandidates = new ArrayList<>();
        if(configFileName != null) {
            pathCandidates.add(configFileName);
        }
        pathCandidates.add(DEFAULT_CONFIG_NAME + ".xml");
        pathCandidates.add(DEFAULT_CONFIG_NAME + "-" + Locale.getDefault().getLanguage() + ".xml");
        String redpenHome = System.getenv("REDPEN_HOME");
        if (redpenHome != null) {
            pathCandidates.add(redpenHome + File.separator + DEFAULT_CONFIG_NAME + ".xml");
            pathCandidates.add(redpenHome + File.separator
                    + DEFAULT_CONFIG_NAME + "-" + Locale.getDefault().getLanguage() + ".xml");
        }
        File resolved = resolve(pathCandidates);
        if (resolved != null && resolved.exists() && resolved.isFile()) {
            LOG.info("Configuration file: {}", resolved.getAbsolutePath());
        } else {
            resolved = null;
        }
        return resolved;
    }

    static File resolve(List<String> pathCandidates) {
        File resolved;
        for (String pathCandidate : pathCandidates) {
            resolved = new File(pathCandidate);
            if (resolved.exists() && resolved.isFile()) {
                return resolved;
            }
        }
        return null;
    }
}
