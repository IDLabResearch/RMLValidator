/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.skolemization;

import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;

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
            rmlMappingGraph.remove(
                    (Resource) resource,
                    tri.getPredicate(),
                    tri.getObject());
            if(tri.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                rmlMappingGraph.remove(
                    (Resource) resource,
                    vf.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                    tri.getObject());
            }
            rmlMappingGraph.add(skolemizedMap, tri.getPredicate(), tri.getObject());
        }
        List<Statement> triplesObject = rmlMappingGraph.tuplePattern(
                null, null, resource);
        for (Statement tri : triplesObject) {
            rmlMappingGraph.remove(
                    tri.getSubject(),
                    tri.getPredicate(),
                    tri.getObject());
            rmlMappingGraph.add(tri.getSubject(), tri.getPredicate(), skolemizedMap);
        }
        rmlMappingGraph.printRDFtoFile("/home/andimou/Desktop/test_after.nt", RDFFormat.NTRIPLES);
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
