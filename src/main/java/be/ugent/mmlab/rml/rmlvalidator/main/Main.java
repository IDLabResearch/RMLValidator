package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.dataset.FileDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.rdfunit.RDFUnitValidator;
import be.ugent.mmlab.rml.rmlvalidator.config.RMLValidatorConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
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
        
        CommandLine commandLine;
        

        commandLine = RMLValidatorConfiguration.parseArguments(args);
        
        if (commandLine.hasOption("h")) {
            RMLValidatorConfiguration.displayHelp();
        }

        String mappingDocument = commandLine.getOptionValue("m", null);
        if (commandLine.hasOption("mqa")) {
            log.info("Mapping Quality Assessment");
            mappingQA(mappingDocument);

        } else if (commandLine.hasOption("dqa")) {
            log.info("Dataset Quality Assessment");
            datasetQA(mappingDocument);
            //} 
        } else if (commandLine.hasOption("mdqa")) {
            log.info("Mapping and Dataset Quality Assessment");
            mappingQA(mappingDocument);
            datasetQA(mappingDocument);
        }
        else {
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
    
    private static void exportSkolemizedMappingDefinitions(
            Repository repository, String skolemizedRMLFile) {
        try {
            RMLDataset skolRMLDataset =
                    new FileDataset(skolemizedRMLFile, "turtle");

            RepositoryConnection mapDocRepoCon = repository.getConnection();
            
            RepositoryResult<Statement> asmth =
                    mapDocRepoCon.getStatements(null, null, null, true);

            while (asmth.hasNext()) {
                Statement statement = asmth.next();
                skolRMLDataset.add(
                        statement.getSubject(),
                        statement.getPredicate(),
                        statement.getObject());
            }
            
            skolRMLDataset.closeRepository();

        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }

    }
    
    private static Repository mappingQA(String mappingDocument) {
        String[] exeTriplesMap = null;
        String triplesMap;
        Repository repository = null;
        String baseURI = "http://example.com";

        //Generate the file for the skolemized mapping
        String skolemizedRMLFile =
                mappingDocument.replace("rml.ttl", "skolrml.ttl");
        log.debug("skolemized mapping document " + skolemizedRMLFile);

        //Generate the file for the validation results
        String outputFile =
                mappingDocument.replace("rml.ttl", "mqa.ttl");
        log.info("validation result " + outputFile);

        //Retrieve the Mapping Document
        log.info("========================================");
        log.info("Retrieving the RML Mapping Document...");
        log.info("========================================");
        RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
        repository =
                mapDocRetrieval.getMappingDoc(
                mappingDocument, RDFFormat.TURTLE);

        //Mapping Document extraction
        log.info("========================================");
        log.info("Extracting the RML Mapping Definitions..");
        log.info("========================================");

        StdRMLMappingFactory mappingFactory =
                new StdRMLMappingFactory(true);
        repository = mappingFactory.prepareExtractRMLMapping(repository);
        exportSkolemizedMappingDefinitions(repository, skolemizedRMLFile);

        /*if (commandLine.hasOption("tm")) {
            triplesMap = commandLine.getOptionValue("tm", null);
            if (triplesMap != null) {
                exeTriplesMap =
                        RMLValidatorConfiguration.
                        processTriplesMap(triplesMap, mappingDocument);
            }
        }*/

        //Mapping Document validation
        log.info("========================================");
        log.info("Validating the RML Mapping Definitions..");
        log.info("========================================");
        RDFUnitValidator rdfUnitValidator =
                new RDFUnitValidator(baseURI, skolemizedRMLFile);
        String rdfunitStringResults = rdfUnitValidator.validate();

        try {
            File file = new File(outputFile);
            FileWriter fw = new FileWriter(file);
            fw.write(rdfunitStringResults);
            fw.close();
        } catch (FileNotFoundException ex) {
            log.error("File Not found Excpetion " + ex);
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
        return repository;
    }
    
    private static void datasetQA(String mappingDocument) {
        String baseURI = "http://example.com";

        //Retrieve the Mapping Document
        log.info("========================================");
        log.info("Retrieving the RML Mapping Document...");
        log.info("========================================");
        RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
        
        Repository repository =
                mapDocRetrieval.getMappingDoc(mappingDocument, RDFFormat.TURTLE);

        log.info("========================================");
        log.info("Extracting the RML Mapping Definitions..");
        log.info("========================================");
        StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
        RMLMapping mapping = mappingFactory.extractRMLMapping(repository);
        RMLEngine engine = new RMLEngine();

        log.info("========================================");
        log.info("Running the RML Mapping..");
        log.info("========================================");
        String outputFile =
                mappingDocument.replace("rml.ttl", "output.ttl");
        RMLDataset dataset =
                engine.runRMLMapping(mapping, baseURI, outputFile,
                "turtle", null, null);
        dataset.closeRepository();

        log.info("========================================");
        log.info("Validating the RDF dataset..");
        log.info("========================================");
        RDFUnitValidator rdfUnitValidator =
                new RDFUnitValidator(baseURI, outputFile);
        String rdfunitStringResults = rdfUnitValidator.validate();

        try {
            //Generate the file for the validation results
            String validationFile =
                mappingDocument.replace("rml.ttl", "dqa.ttl");
            File file = new File(validationFile);
            FileWriter fw = new FileWriter(file);
            fw.write(rdfunitStringResults);
            fw.close();
        } catch (FileNotFoundException ex) {
            log.error("File Not found Excpetion " + ex);
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
    }
}
