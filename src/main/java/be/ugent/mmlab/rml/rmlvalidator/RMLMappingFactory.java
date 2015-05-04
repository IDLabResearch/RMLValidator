/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.extractor.RMLInputExtractor;
import be.ugent.mmlab.rml.extractor.RMLMappingExtractor;
import be.ugent.mmlab.rml.extractor.RMLUnValidatedMappingExtractor;
import be.ugent.mmlab.rml.extractor.RMLValidatedMappingExtractor;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author andimou
 */
public final class RMLMappingFactory {
    
    // Log
    private static final Logger log = LogManager.getLogger(RMLValidatedMappingExtractor.class);
    
    private RMLMappingExtractor extractor;
    private RMLMappingValidator validator;

    //extraction and validation
    public RMLMappingFactory(boolean validate){
        setRMLMappingFactory(validate);
    }
   
    public void setRMLMappingFactory(boolean validate){
        this.validator = new RMLValidator();

        if(validate){
            this.extractor = new RMLValidatedMappingExtractor(validator);
        }
        else
            this.extractor = new RMLUnValidatedMappingExtractor();
    }
    
    public RMLMapping extractRMLMapping(String fileToRMLFile, String outputFile) {
        
        // Load RDF data from R2RML Mapping document
        RMLSesameDataSet rmlMappingGraph, newRmlMappingGraph ;
        newRmlMappingGraph = new RMLSesameDataSet() ;
        RMLInputExtractor InputExtractor = new RMLInputExtractor() ;
        rmlMappingGraph = InputExtractor.getMappingDoc(fileToRMLFile, outputFile, RDFFormat.TURTLE);
        
        // Transform RDF with replacement shortcuts
        extractor.replaceShortcuts(rmlMappingGraph);
        
        //skolemize blank node statements
        rmlMappingGraph = extractor.skolemizeStatements(rmlMappingGraph, newRmlMappingGraph);
        
        List<Statement> triples = rmlMappingGraph.tuplePattern(null, null, null);
        for (Statement triple : triples) {
            if (triple.getSubject().getClass() != org.openrdf.sail.memory.model.MemBNode.class
                    && triple.getObject().getClass() != org.openrdf.sail.memory.model.MemBNode.class)
                newRmlMappingGraph.add(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }
        
        rmlMappingGraph = newRmlMappingGraph;
        
        // Construct R2RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = 
                extractor.extractTriplesMapResources(rmlMappingGraph);
        
        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Number of RML triples with "
                + " type "
                + RMLVocabulary.R2RMLTerm.TRIPLES_MAP_CLASS
                + " in file "
                + fileToRMLFile + " : " + triplesMapResources.size());
        
        validator.checkTriplesMapResources(triplesMapResources);

        // Fill each TriplesMap object
        for (Resource triplesMapResource : triplesMapResources.keySet())  // Extract each triplesMap
            extractor.extractTriplesMap(
                    rmlMappingGraph, triplesMapResource, triplesMapResources);

        rmlMappingGraph.printRDFtoFile(outputFile, RDFFormat.TURTLE);
        // Generate RMLMapping object
        RMLMapping result = new RMLMapping(triplesMapResources.values());
        return result;
    }
    
}
