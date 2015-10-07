package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.rdfunit.RDFUnitValidator;

import be.ugent.mmlab.rml.rmlvalidator.RMLValidatorMappingFactory;
import be.ugent.mmlab.rml.rmlvalidator.config.RMLValidatorConfiguration;
import org.apache.commons.cli.CommandLine;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * *************************************************************************
 *
 * RML - Validator : Main
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public class Main {
    
    // Log
    static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Log
        Logger log = LoggerFactory.getLogger(Main.class);

        StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
        String map_doc, triplesMap;
        String[] exeTriplesMap = null;
        //BasicConfigurator.configure();
        CommandLine commandLine;
        Repository repository = null;

        commandLine = RMLValidatorConfiguration.parseArguments(args);
        String outputFile = null;

        if (commandLine.hasOption("h")) {
            RMLValidatorConfiguration.displayHelp();
        }
        /*if (commandLine.hasOption("o")) {
            outputFile = commandLine.getOptionValue("o", null);
        }*/
        if (commandLine.hasOption("m")) {
            String baseURI = "http://example.com";
            map_doc = commandLine.getOptionValue("m", null);
            System.out.println("\n Map doc: " + map_doc);

            if (commandLine.hasOption("q")) {
                String outputFileRDFUnit = commandLine.getOptionValue("q", null);
                log.error("outputFileRDFUnit " + outputFileRDFUnit);

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
                //RMLMapping mapping =
                //        mappingFactory.extractRMLMapping(repository);

                if (commandLine.hasOption("tm")) {
                    triplesMap = commandLine.getOptionValue("tm", null);
                    if (triplesMap != null) {
                        exeTriplesMap =
                                RMLValidatorConfiguration.
                                processTriplesMap(triplesMap, map_doc);
                    }
                }

                log.info("========================================");
                log.info("Validating the RML Mapping Definitions..");
                log.info("========================================");

                //Mapping Document validation
                mappingFactory = new RMLValidatorMappingFactory(
                        repository, true);

                log.info("========================================");
                log.info("Instantiating RDFUnit Test Cases..");
                log.info("========================================");

                //Schema validation
                System.out.println("\n Started RDFUnit. \n ");
                RDFUnitValidator rdfUnitValidator =
                        new RDFUnitValidator(baseURI, outputFile);

                log.info("==================================================");
                log.info("Validating the RML Mapping Document with RDFUnit..");
                log.info("==================================================");
                String rdfunitStringResults = rdfUnitValidator.validate();
                //FileSesameDataset rdfunitresults = new FileSesameDataset(outputFileRDFUnit, "turtle");

                //rdfunitresults.printRDFtoFile(outputFileRDFUnit,RDFFormat.TURTLE);
                //rdfunitresults.addString(rdfunitStringResults, outputFileRDFUnit, RDFFormat.TURTLE);
                log.info("==================================================");
                log.info("Processing the RDFUnit results..");
                log.info("==================================================");
                //rdfunitresults.loadDataFromInputStream(
                //        rdfunitResults, baseURI, RDFFormat.TURTLE, (Resource) null);
                //rdfunitresults.printRDFtoFile(outputFileRDFUnit, RDFFormat.TURTLE);
                //log.error("rdfunitresults " + rdfunitresults);

            } else if (commandLine.hasOption("V")) {
                mappingFactory = new RMLValidatorMappingFactory(repository, false);
                //RMLMapping finalDataset = mappingFactory.extractRMLMapping(map_doc);
            } /*else {
             mappingFactory = new RMLValidatorMappingFactory(true);
             mappingFactory.extractRMLMapping(map_doc);
             }*/
            /*if (commandLine.hasOption("V")) {
             log.info("call RDFUnit");
             //call RDFUnit and pass either the original file or the generated one
             }*/
        } else {
            System.out.println("\n No input mapping document was provided. \n ");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("RML Validator");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            System.out.println("Usage: mvn exec:java -Dexec.args=\"-m <mapping_file> -o <output_file> -V -t\"");
            System.out.println("");
            System.out.println("With");
            System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
            System.out.println("    <output_file> = The RML mapping document conform with skolemized and inferred statements.");
            System.out.println("add -V not to validate the mapping document");
            System.out.println("add -t to pass the quality tests");
            System.out.println("");
            System.out.println("--------------------------------------------------------------------------------");
        }

    }
}
