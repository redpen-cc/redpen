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
package org.unigram.docvalidator;

import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ResultDistributorFactory;
import org.unigram.docvalidator.validator.DocumentValidator;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing main method called from command line.
  */
public final class Main {
  public static void main(String[] args) throws DocumentValidatorException {
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
    CommandLine commandLine;

    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
        LOG.error("Error occurred in parsing command line options ");
        printHelp(options);
        return;
    }

    String inputFormat = "plain";
    String[] inputFileNames = null;
    String configFileName = "";
    String resultFormat = "plain";

    if (commandLine.hasOption("h")) {
        printHelp(options);
        return;
    }
    if (commandLine.hasOption("v")) {
        System.out.println("1.0");
        return;
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
    DVResource conf = configLoader.loadConfiguration(configFileName);
    if (conf == null) {
      LOG.error("Failed to initialize the DocumentValidator resource.");
      return;
    }

    Document document =
        DocumentGenerator.generate(inputFileNames, conf, inputFormat);
    if (document == null) {
      LOG.error("Failed to create a Document object");
      return;
    }

    // validate document
    ResultDistributor distributor =
        ResultDistributorFactory.createDistributor(resultFormat, System.out);
    DocumentValidator validator = new DocumentValidator(conf, distributor);
    validator.check(document);
  }

  private static void printHelp(Options opt) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("ParseArgs", opt);
  }

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private Main() {
    super();
  }
}
