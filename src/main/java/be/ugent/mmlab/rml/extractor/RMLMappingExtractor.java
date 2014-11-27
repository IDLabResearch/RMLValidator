/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.Map;
import org.openrdf.model.Resource;

/**
 *
 * @author andimou
 */
public interface RMLMappingExtractor {
    
    /**
     *
     * @param rmlMappingGraph
     */
    public void replaceShortcuts(RMLSesameDataSet rmlMappingGraph);
    
    public void skolemizeStatements(RMLSesameDataSet rmlMappingGraph);
    
    /**
     *
     * @param rmlMappingGraph
     * @return
     */
    public Map<Resource, TriplesMap> extractTriplesMapResources(
            RMLSesameDataSet rmlMappingGraph);
    
    public void extractTriplesMap(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, 
            Map<Resource, TriplesMap> triplesMapResources);
        
}
