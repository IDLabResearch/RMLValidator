package be.ugent.mmlab.rml.rmlvalidator.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andimou
 */
public class RMLValidatorConfiguration {
    // Log
    static Logger log = 
            LoggerFactory.getLogger(RMLValidatorConfiguration.class);
    
    private static final Options cliOptions = generateCLIOptions();
    
    public static CommandLine parseArguments(String[] args) {
        try {
            CommandLineParser cliParser = new GnuParser();
            return cliParser.parse(getCliOptions(), args);
        } catch (ParseException ex) {
            log.error("Parse Exception " + ex);
        }
        return null;
    }

    private static Options generateCLIOptions() {
        Options cliOptions = new Options();
        
        cliOptions.addOption(
                "h", "help", false, "show this help message");
        cliOptions.addOption(
                "m", "mapping document", true, "the URI of the mapping file (required)");
        cliOptions.addOption(
                "o", "output document", true, 
                "the output file that contains the skolemized and inferred mappings");
        cliOptions.addOption(
                "V", "without validation", false, "no validation");
        cliOptions.addOption(
                "t", "tests are enabled", false, "the RDFUnit tests are called");
        return cliOptions;
    }
    
    public static Options getCliOptions() {
        return cliOptions;
    }
    
    public static void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("RML Validator", getCliOptions());
        System.exit(1);
    }
    
    public static String[] processTriplesMap (String parameters, String map_doc){
        if (parameters != null) {
            String[] exeTriplesMap = parameters.split(",");
            for(int i=0 ; i < exeTriplesMap.length ; i++){
                //TODO:remove hardcoded file:
                exeTriplesMap[i] = "file:" + map_doc + "#" + exeTriplesMap[i];
            log.info("TriplesMap to be processed " + exeTriplesMap[i]);
            }
            return exeTriplesMap;
        } else {
            return null;
        }
    }
    
}
