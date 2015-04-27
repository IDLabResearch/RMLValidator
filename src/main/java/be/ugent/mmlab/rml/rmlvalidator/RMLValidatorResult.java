/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.R2RMLTerm;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.Term;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author andimou
 */
public class RMLValidatorResult {
    // Log
    private static final Logger log = LogManager.getLogger(RMLValidatorResult.class);
    
    private RMLSesameDataSet resultGraph = new RMLSesameDataSet();
    private String time;
    private Integer iterator = 0;
    
    private ValueFactory vf  = new ValueFactoryImpl();

    public RMLValidatorResult() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        this.time = ft.format(dNow).toString();
        addCreationDate();
    }
    
    private void addCreationDate(){
        URI predicate = resultGraph.URIref(
                        "http://purl.org/dc/terms/date");
        Literal object = vf.createLiteral(time);
        resultGraph.add(getSubject(), predicate, object);
    }
    
    private URI getSubject(){
        Integer iter = iterator++;
        return resultGraph.URIref(
                    "http://example.org/data/results#id" + iter.toString());
    }
    
    public String getDateTime(){
        System.out.println("Current Date: " + time);
        return time;
    }
    
    public void addViolation(
            Value object, Term term, String description, String method){
        if(object != null){
            addViolationRoot(object);
            addViolationPath(term);
        }
        addViolationDescription(method, description);
    }
    
    private void addViolationDescription(String method, String description){
        ValueFactory vf  = new ValueFactoryImpl();
        URI predicate = resultGraph.URIref("http://purl.org/dc/terms/description");
        Literal object = vf.createLiteral(description);
        resultGraph.add(getSubject(), predicate, object);
        logError(method, description);
    }
    
    private void addViolationRoot(Value object){
        URI predicate = resultGraph.URIref("http://spinrdf.org/spl#violationRoot");
        resultGraph.add(getSubject(), predicate, object);
    }
    
    private void addViolationPath(Term term){
        URI predicate = resultGraph.URIref("http://spinrdf.org/spl#violationPath");
        URI object = resultGraph.URIref(RMLVocabulary.R2RML_NAMESPACE + term);
        for(R2RMLTerm value : R2RMLTerm.values())
            if(value.equals(term))
                resultGraph.add(getSubject(), predicate, object );
    }
    
    public RMLSesameDataSet getResultGraph(){
        return resultGraph ;
    }
    
    private void logError(String method, String description){
        log.error( method + ": " + description);
    }
    
}
