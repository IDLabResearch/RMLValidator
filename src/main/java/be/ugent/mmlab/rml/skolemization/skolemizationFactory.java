/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.skolemization;

import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.List;

/**
 *
 * @author andimou
 */

public class skolemizationFactory {
    // Log
    private static final Logger log = LogManager.getLogger(skolemizationFactory.class);
    
    private static ValueFactory vf = new ValueFactoryImpl();

    public static RMLSesameDataSet skolemization(Value resource, RMLSesameDataSet rmlMappingGraph) {
        
        List<Statement> triples;
        
        Resource skolemizedResource; 
        
        triples = rmlMappingGraph.tuplePattern((Resource) resource, null, null);
        
        for (Statement triple : triples) {
            //subject is Blank Node
            if(triple.getSubject().getClass() == org.openrdf.sail.memory.model.MemBNode.class){
                //skolemize subject
                skolemizedResource = skolemizationFactory.skolemizeBlankNode(resource);
                
                replaceSubjectSkolem(rmlMappingGraph, triple, skolemizedResource);
                
                skolemizeAssociated(rmlMappingGraph, triple.getSubject(), skolemizedResource);

                }
                //Object is Blank Node
                else if (triple.getObject().getClass() == org.openrdf.sail.memory.model.MemBNode.class) {
                    
                    //skolemize object
                    skolemizedResource = skolemizationFactory.skolemizeBlankNode(triple.getObject());
                
                    replaceObjectSkolem(rmlMappingGraph, triple, skolemizedResource);
                
                    skolemizeAssociated(rmlMappingGraph, (Resource) triple.getObject(), skolemizedResource);
            
                    }
                
            }
        
        return rmlMappingGraph;
    }
    
    private static void replaceSubjectSkolem(
        RMLSesameDataSet rmlMappingGraph, Statement triple, Resource skolemizedResource) {
        //replace cuurent triple with skolemized value
        rmlMappingGraph.remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
        //and replace it with the skolemized value
        rmlMappingGraph.add(skolemizedResource, triple.getPredicate(), triple.getObject());
    }
    
    private static void replaceObjectSkolem(
        RMLSesameDataSet rmlMappingGraph, Statement triple, Resource skolemizedResource) {
        //replace cuurent triple with skolemized value
        rmlMappingGraph.remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
        //and replace it with the skolemized value
        rmlMappingGraph.add(triple.getSubject(), triple.getPredicate(), skolemizedResource);
    }
    
    private static void skolemizeAssociated(
            RMLSesameDataSet rmlMappingGraph, Resource resource, Resource skolemizedResource) {
        
        //retrieve all triples that the resource appears as subject
        List<Statement> triples = rmlMappingGraph.tuplePattern((Resource) resource, null, null);

        for (Statement triple : triples) {
            //remove triple
            rmlMappingGraph.remove(
                    (Resource) resource, triple.getPredicate(), triple.getObject());
            //and replace it with the skolemized value
            rmlMappingGraph.add(skolemizedResource, triple.getPredicate(), triple.getObject());
        }

        //retrieve all triples that the resource appears as object
        triples = rmlMappingGraph.tuplePattern(null, null, resource);

        for (Statement triple : triples) {
            
            //remove existing triple
            rmlMappingGraph.remove(
                    triple.getSubject(), triple.getPredicate(), resource);
            //and replace it with the skolemized value
            rmlMappingGraph.add(triple.getSubject(), triple.getPredicate(), skolemizedResource);
        }
    }

    private static Resource skolemizeBlankNode(Value re) {
        if (re != null && re.stringValue().contains(".well-known/genid/")) {
            return (Resource) re;
        }
        if (re == null) {
            re = vf.createBNode();
            Resource ree =
                    vf.createURI("http://example.com/.well-known/genid/" + re.stringValue().substring(0));
            return ree;
        }
        return vf.createURI("http://example.com/.well-known/genid/" + re.stringValue().substring(0));
    }
}
