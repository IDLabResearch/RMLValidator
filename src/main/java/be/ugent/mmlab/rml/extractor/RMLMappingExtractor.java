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
     * @param r2rmlMappingGraph
     */
    public void replaceShortcuts(RMLSesameDataSet r2rmlMappingGraph);
    
    /**
     *
     * @param r2rmlMappingGraph
     * @return
     */
    public Map<Resource, TriplesMap> extractTriplesMapResources(
            RMLSesameDataSet r2rmlMappingGraph);
    
    public void extractTriplesMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource triplesMapSubject, 
            Map<Resource, TriplesMap> triplesMapResources);
        
}
