package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.rdfunit.RDFUnitValidator;
import be.ugent.mmlab.rml.rmlvalidator.RMLMappingFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author andimou
 */
public class MainTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        String map_doc = "http://rml.io/rml/data/drafts/mapping.rml.ttl";
        //String map_doc = "/home/andimou/Desktop/offer.rml.ttl";
        //String map_doc = "/home/andimou/Desktop/destelbergen.rml.ttl";
        String outputFile = "/home/andimou/Desktop/destelbergenProcessed.rml.ttl";
        BasicConfigurator.configure();
        
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

        RMLMappingFactory mappingFactory = new RMLMappingFactory(true);
        mappingFactory.extractRMLMapping(map_doc, outputFile);
        //mappingFactory.extractRMLMapping("http://rml.io/rml/data/CD_EWI/Destelbergen/destelbergen_CSV.rml.ttl");
        //mappingFactory.extractRMLMapping("http://rml.io/rml/data/csvw/events/mapping-events.rml.ttl");
        //mappingFactory.extractRMLMapping("http://rml.io/rml/data/drafts/mapping.rml.ttl");
        
        RDFUnitValidator rdfUnitValidator = new RDFUnitValidator("http://example.com", outputFile);
        String rdfunitResults = rdfUnitValidator.validate();
        RMLSesameDataSet rdfunitresults = new RMLSesameDataSet();
        try {
            rdfunitresults.loadDataFromInputStream(rdfunitResults, RDFFormat.TURTLE, (Resource) null);
        } catch (RepositoryException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
