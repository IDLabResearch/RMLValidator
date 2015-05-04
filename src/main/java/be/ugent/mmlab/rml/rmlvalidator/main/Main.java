package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.extractor.RMLInputExtractor;
import be.ugent.mmlab.rml.rdfunit.RDFUnitValidator;
import be.ugent.mmlab.rml.vocabulary.RMLConfiguration;
import be.ugent.mmlab.rml.rmlvalidator.RMLMappingFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.sesame.FileSesameDataset;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

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
                String baseURI = "http://example.com";
                map_doc = commandLine.getOptionValue("m", null);
                System.out.println("\n Map doc: " + map_doc);
                RMLMappingFactory mappingFactory;
                
                if (commandLine.hasOption("q")) {
                    String outputFileRDFUnit = commandLine.getOptionValue("q", null);
               log.error("outputFileRDFUnit " + outputFileRDFUnit);     
                    //Mapping Document validation
                    System.out.println("\n Started Mapping Document validation. \n ");
                    mappingFactory = new RMLMappingFactory(true);
                    mappingFactory.extractRMLMapping(map_doc, outputFile);
                    
                    //Schema validation
                    System.out.println("\n Started RDFUnit. \n ");
                    RDFUnitValidator rdfUnitValidator = new RDFUnitValidator(baseURI, outputFile);
                    
                    System.out.println("\n Started Mapping Document Schema validation with RDFUnit. \n ");
                    String rdfunitStringResults = rdfUnitValidator.validate();                   
                    //log.error("rdfunitResults " + rdfunitStringResults);
                    FileSesameDataset rdfunitresults = new FileSesameDataset(outputFileRDFUnit, "turtle");
                    
                    //rdfunitresults.printRDFtoFile(outputFileRDFUnit,RDFFormat.TURTLE);
                    rdfunitresults.addString(rdfunitStringResults, outputFileRDFUnit, RDFFormat.TURTLE);
                    System.out.println("\n RDFUnit results processing.. \n ");
                    //rdfunitresults.loadDataFromInputStream(
                    //        rdfunitResults, baseURI, RDFFormat.TURTLE, (Resource) null);
                    //rdfunitresults.printRDFtoFile(outputFileRDFUnit, RDFFormat.TURTLE);
                    //log.error("rdfunitresults " + rdfunitresults);
                    
                }else if (commandLine.hasOption("V")) {
                    mappingFactory = new RMLMappingFactory(false);
                    mappingFactory.extractRMLMapping(map_doc, outputFile);
                } else {
                    mappingFactory = new RMLMappingFactory(true);
                    mappingFactory.extractRMLMapping(map_doc, outputFile);
                }
                
            }
            else{
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
        } catch (ParseException ex) {
            log.error(ex);
        } /*catch (RepositoryException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            log.error(ex);
        } catch (RDFParseException ex) {
            log.error(ex);
        } catch (IOException ex) {
            //log.error(ex);
            log.error("error with the output file.");
        }*/

    }
}
