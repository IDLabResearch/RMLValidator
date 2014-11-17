/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.skolemization;

import be.ugent.mmlab.rml.rml.RMLVocabulary.Term;
import com.sun.org.apache.xpath.internal.operations.Variable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author andimou
 */

public class skolemizationFactory {
    //private static Set<Term> skolemTerms;
    //private static Map<Variable,Term> bindings;
    private static ValueFactory vf = new ValueFactoryImpl();

   
    public skolemizationFactory () {
        //bindings = new HashMap<Variable,Term>();
        //skolemTerms = new HashSet<Term>();
    }
    
    public static Resource skolemizeBlankNode(Value re){
        return vf.createURI(
                "http://example.com/.well-known/genid/" + re.stringValue().substring(2));
    }
    
}
