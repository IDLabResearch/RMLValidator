package be.ugent.mmlab.rml.rmlvalidator.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author andimou
 */
public class RMLValidatorConfiguration {
    private static final Options cliOptions = generateCLIOptions();
    
    public static CommandLine parseArguments(String[] args) throws ParseException {
        
        CommandLineParser cliParser = new GnuParser();
        return cliParser.parse(getCliOptions(), args);
    }

    private static Options generateCLIOptions() {
        Options cliOptions = new Options();
        
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("m", "mapping document", true, "the URI of the mapping file (required)");
        cliOptions.addOption("o", "output document", true, "the output file that contains the skolemized and inferred mappings");
        cliOptions.addOption("g", "graph", true, "the graph to use");
        cliOptions.addOption("V", "without validation", false, "no validation");
        cliOptions.addOption("t", "tests are enabled", false, "the RDFUnit tests are called");
        return cliOptions;
    }
    
    public static Options getCliOptions() {
        return cliOptions;
    }
    
    public static void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("RML Processor", getCliOptions());
        System.exit(1);
    }
    
}
