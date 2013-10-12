package org.unigram.docvalidator;

import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
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
    String format = "t";
    String[] inputFileNames = null;
    String configFileName = "";
    String charTableFileName = "";

    Options options = new Options();
    options.addOption("h", "help", false, "help");
    OptionBuilder.withLongOpt("format");
    OptionBuilder.withDescription("date format");
    OptionBuilder.hasArg();
    OptionBuilder.withArgName("FORMAT");
    options.addOption(OptionBuilder.create("f"));
    OptionBuilder.withLongOpt("input");
    OptionBuilder.withDescription("input file");
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withArgName("INPUT FILE");
    options.addOption(OptionBuilder.create("i"));
    OptionBuilder.withLongOpt("conf");
    OptionBuilder.withDescription("configuraiton file");
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("c"));
    OptionBuilder.withLongOpt("char");
    OptionBuilder.withDescription("character table file");
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("C"));
    options.addOption("v", "version", false,
        "print the version information and exit");

    CommandLineParser parser = new BasicParser();
    CommandLine commandLine;

    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
        LOG.error("Error occured in parsing command line options ");
        printHelp(options);
        return;
    }
    if (commandLine.hasOption("h")) {
        printHelp(options);
        return;
    }
    if (commandLine.hasOption("v")) {
        System.out.println("1.0");
        return;
    }
    if (commandLine.hasOption("f")) {
      format = commandLine.getOptionValue("f");
    }
    if (commandLine.hasOption("i")) {
      inputFileNames = commandLine.getOptionValues("i");
    }
    if (commandLine.hasOption("c")) {
      configFileName = commandLine.getOptionValue("c");
    }
    if (commandLine.hasOption("C")) {
     charTableFileName = commandLine.getOptionValue("C");
    }
    ConfigurationLoader configLoder = new ConfigurationLoader();
    Configuration conf = configLoder.loadConfiguraiton(configFileName);

    DVResource resource = null;
    if (charTableFileName != null) {
      LOG.info("loading character table file: " + charTableFileName);
      CharacterTable characterTable = new CharacterTable(charTableFileName);
      resource = new DVResource(conf, characterTable);
    } else {
      LOG.warn("NO character table file is specified");
      resource = new DVResource(conf);
    }
    Document document =
        DocumentGenerator.generate(inputFileNames, resource, format);

    // validate document
    DocumentValidator validator = new DocumentValidator(resource);
    validator.check(document);

    return;
  }

  private static void printHelp(Options opt) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("ParseArgs", opt);
  }

  private static Logger LOG = LoggerFactory.getLogger(Main.class);

  private Main() {
    super();
  }
}
