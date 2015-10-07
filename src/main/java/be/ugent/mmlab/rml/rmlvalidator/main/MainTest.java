package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.rmlvalidator.config.RMLValidatorConfiguration;
import be.ugent.mmlab.rml.rmlvalidator.RMLValidatorMappingFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
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
        Repository repository = null;
        StdRMLMappingFactory mappingFactory;
        //BasicConfigurator.configure();
        CommandLine commandLine = 
                RMLValidatorConfiguration.parseArguments(args);

        if (commandLine.hasOption("h")) 
            RMLValidatorConfiguration.displayHelp();
        if (commandLine.hasOption("o")) 
            outputFile = commandLine.getOptionValue("o", null);
        
        log.error("output file for RDFUnit " + outputFile);
        
        //Retrieve the Mapping Document
        log.info("========================================");
        log.info("Retrieving the RML Mapping Document...");
        log.info("========================================");
        RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
        repository =
                mapDocRetrieval.getMappingDoc(map_doc, RDFFormat.TURTLE);
        
        log.info("========================================");
        log.info("Extracting the RML Mapping Definitions..");
        log.info("========================================");
        //Mapping Document validation
                mappingFactory = new RMLValidatorMappingFactory(
                        repository, true);
        RMLMapping mapping =
                mappingFactory.extractRMLMapping(repository);

    }
}
