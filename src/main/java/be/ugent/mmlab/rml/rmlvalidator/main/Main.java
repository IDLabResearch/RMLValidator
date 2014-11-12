package be.ugent.mmlab.rml.rmlvalidator.main;

import be.ugent.mmlab.rml.rml.RMLConfiguration;
import be.ugent.mmlab.rml.rmlvalidator.RMLMappingFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author andimou
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        BasicConfigurator.configure();
        CommandLine commandLine;
        
        try {
            commandLine = RMLConfiguration.parseArguments(args);
            
            if (commandLine.hasOption("h")) 
                RMLConfiguration.displayHelp();
            
            String map_doc = commandLine.getOptionValue("m", null);
            
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("RML Validator");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            System.out.println("Usage: mvn exec:java -Dexec.args=\"<mapping_file> -v\"");
            System.out.println("");
            System.out.println("With");
            System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
            System.out.println("add -wv not to validate the mapping document");
            System.out.println("");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("[RMLValidator:main] mapping document " + map_doc);

            RMLMappingFactory mappingFactory ;
            if (commandLine.hasOption("wv")) {
                mappingFactory = new RMLMappingFactory(false);
                mappingFactory.extractRMLMapping(map_doc);
            } else {
                mappingFactory = new RMLMappingFactory(true);
                mappingFactory.extractRMLMapping(map_doc);
            }
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }
}
