package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.rmlvalidator.config.RMLValidatorConfiguration;
import be.ugent.mmlab.rml.rmlvalidator.RMLValidatorMappingFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * *************************************************************************
 *
 * RML - Validator : MainTest
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public class MainTest {
    
    // Log
    static final Logger log = LoggerFactory.getLogger(MainTest.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        String map_doc = "/home/andimou/Desktop/offer.rml.ttl";
        String outputFile = "/home/andimou/Desktop/offerProcessed.rml.ttl";
        BasicConfigurator.configure();
        CommandLine commandLine = RMLValidatorConfiguration.parseArguments(args);

        if (commandLine.hasOption("h")) 
            RMLValidatorConfiguration.displayHelp();
        if (commandLine.hasOption("o")) 
            outputFile = commandLine.getOptionValue("o", null);
        
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("RML Validator");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("Usage: mvn exec:java -Dexec.args=\"<mapping_file> \"");
        System.out.println("");
        System.out.println("With");
        System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
        System.out.println("");
        System.out.println("--------------------------------------------------------------------------------");

        RMLValidatorMappingFactory mappingFactory = new RMLValidatorMappingFactory(true);
        mappingFactory.extractRMLMapping(map_doc);
    }
}
