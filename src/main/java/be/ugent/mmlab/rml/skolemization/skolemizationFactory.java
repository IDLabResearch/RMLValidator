/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.skolemization;

import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */

public class skolemizationFactory {
    // Log
    private static final Logger log = LogManager.getLogger(skolemizationFactory.class);
    
    private static ValueFactory vf = new ValueFactoryImpl();

    public static void skolemSubstitution(
            Value resource, Resource skolemizedMap, RMLSesameDataSet rmlMappingGraph) {
        
        List<Statement> triplesSubject = rmlMappingGraph.tuplePattern(
                (Resource) resource, null, null);

        for (Statement tri : triplesSubject) {
            int size1 = rmlMappingGraph.getSize();
            if (resource instanceof BNode){
                rmlMappingGraph.remove(
                    (BNode) tri.getSubject(),
                    (URI) tri.getPredicate(),
                    (Value) tri.getObject());
            }
            else
                rmlMappingGraph.remove(
                    (Resource) tri.getSubject(),
                    (URI) tri.getPredicate(),
                    (Value) tri.getObject());

            rmlMappingGraph.add(skolemizedMap, tri.getPredicate(), tri.getObject());

            int size2 = rmlMappingGraph.getSize();
            /*if (size1 != size2) {
                log.error("didn't delete again..");
            }*/
        }
        List<Statement> triplesObject = rmlMappingGraph.tuplePattern(
                null, null, resource);
        for (Statement tri : triplesObject) {
            rmlMappingGraph.remove(
                    (Resource) tri.getSubject(),
                    (URI) tri.getPredicate(),
                    (Value) tri.getObject());
            //rmlMappingGraph.remove(tri);
            rmlMappingGraph.add(tri.getSubject(), tri.getPredicate(), skolemizedMap);
        }
    }

    public static Resource skolemizeBlankNode(Value re) {
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
