/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor;

import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.Map;
import java.util.Set;
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
    
    public RMLSesameDataSet skolemizeStatements(RMLSesameDataSet rmlMappingGraph);
    
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
    
    public PredicateObjectMap extractPredicateObjectMap(
            RMLSesameDataSet rmlMappingGraph,
            Resource triplesMapSubject,
            Resource predicateObject,
            Set<GraphMap> savedGraphMaps,
            Map<Resource, TriplesMap> triplesMapResources,
            TriplesMap triplesMap);
    
    /**
     *
     * @param rmlMappingGraph
     * @param object
     * @param graphMaps
     * @param triplesMap
     * @return
     */
    public PredicateMap extractPredicateMap(
            RMLSesameDataSet rmlMappingGraph, Resource object,
            Set<GraphMap> graphMaps, TriplesMap triplesMap);
    
    /**
     *
     * @param rmlMappingGraph
     * @param object
     * @param graphMaps
     * @param triplesMap
     * @return
     */
    public ObjectMap extractObjectMap(RMLSesameDataSet rmlMappingGraph,
            Resource object, Set<GraphMap> graphMaps, TriplesMap triplesMap);
    
    /**
     *
     * @param rmlMappingGraph
     * @param triplesMapSubject
     * @param graphMaps
     * @param result
     * @param triplesMapResources
     * @return
     */
    public Set<PredicateObjectMap> extractPredicateObjectMaps(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> graphMaps, TriplesMap result,
            Map<Resource, TriplesMap> triplesMapResources);
        
}
