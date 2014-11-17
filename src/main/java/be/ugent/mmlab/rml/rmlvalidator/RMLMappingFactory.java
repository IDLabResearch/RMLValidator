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
import be.ugent.mmlab.rml.rml.RMLVocabulary;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
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
        if(validate){
            this.validator = new RMLValidator();
            this.extractor = new RMLValidatedMappingExtractor(validator);   
        }
        else
            extractor = new RMLUnValidatedMappingExtractor();
    }
    
    public RMLMapping extractRMLMapping(String fileToRMLFile) {
        
        // Load RDF data from R2RML Mapping document
        RMLSesameDataSet r2rmlMappingGraph ; // = new RMLSesameDataSet();
        RMLInputExtractor InputExtractor = new RMLInputExtractor() ;
        r2rmlMappingGraph = InputExtractor.getMappingDoc(fileToRMLFile, RDFFormat.TURTLE);
        
        // Transform RDF with replacement shortcuts
        extractor.replaceShortcuts(r2rmlMappingGraph);

        // Construct R2RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = 
                extractor.extractTriplesMapResources(r2rmlMappingGraph);
        
        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Number of RML triples with "
                + " type "
                + RMLVocabulary.R2RMLTerm.TRIPLES_MAP_CLASS
                + " in file "
                + fileToRMLFile + " : " + triplesMapResources.size());
        
        // Fill each TriplesMap object
        for (Resource triplesMapResource : triplesMapResources.keySet())  // Extract each triplesMap
            extractor.extractTriplesMap(
                    r2rmlMappingGraph, triplesMapResource, triplesMapResources);
        
        // Generate RMLMapping object
        RMLMapping result = new RMLMapping(triplesMapResources.values());
        return result;
    }
    
}
