package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.extractor.RMLInputExtractor;
import be.ugent.mmlab.rml.rml.RMLConfiguration;
import be.ugent.mmlab.rml.rmlvalidator.RMLMappingFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Log
        Logger log = LogManager.getLogger(RMLInputExtractor.class);
        String map_doc ;
        BasicConfigurator.configure();
        CommandLine commandLine;

        try {
            commandLine = RMLConfiguration.parseArguments(args);
            String outputFile = null;

            if (commandLine.hasOption("h")) {
                RMLConfiguration.displayHelp();
            }
            if (commandLine.hasOption("o")) {
                outputFile = commandLine.getOptionValue("o", null);
            } 
            if (commandLine.hasOption("m")) {
                map_doc = commandLine.getOptionValue("m", null);
                RMLMappingFactory mappingFactory;
                if (commandLine.hasOption("V")) {
                    mappingFactory = new RMLMappingFactory(false);
                    mappingFactory.extractRMLMapping(map_doc, outputFile);
                } else {
                    mappingFactory = new RMLMappingFactory(true);
                    mappingFactory.extractRMLMapping(map_doc, outputFile);
                }
                if (commandLine.hasOption("V")) {
                    log.info("call RDFUnit");
                    //call RDFUnit and pass either the original file or the generated one
                }
            }
            else{
                System.out.println("\n No input mapping document was provided. \n ");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("RML Validator");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("");
                System.out.println("Usage: mvn exec:java -Dexec.args=\"-m <mapping_file> -o <output_file> -V\"");
                System.out.println("");
                System.out.println("With");
                System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
                System.out.println("    <output_file> = The RML mapping document conform with skolemized and inferred statements.");
                System.out.println("add -V not to validate the mapping document");
                System.out.println("add -q to pass the quality tests");
                System.out.println("");
                System.out.println("--------------------------------------------------------------------------------");
            }
        } catch (ParseException ex) {
            log.error(ex);
        }

    }
}
