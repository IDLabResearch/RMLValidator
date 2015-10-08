package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.vocabularies.R2RMLVocabulary;
import be.ugent.mmlab.rml.vocabularies.R2RMLVocabulary.R2RMLTerm;
import be.ugent.mmlab.rml.vocabularies.Term;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * *************************************************************************
 *
 * RML - Validator : RMLValidatorResult
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public class RMLValidatorResult {
    // Log
    static final Logger log = 
            LoggerFactory.getLogger(RMLValidatorResult.class);
    
    private RMLDataset resultGraph = new StdRMLDataset();
    private String time;
    private Integer iterator = 0;
    
    private ValueFactory vf  = new ValueFactoryImpl();

    /**
     *
     */
    public RMLValidatorResult() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        this.time = ft.format(dNow).toString();
        addCreationDate();
    }
    
    private void addCreationDate(){
        URI predicate = vf.createURI(
                        "http://purl.org/dc/terms/date");
        Literal object = vf.createLiteral(time);
        resultGraph.add(getSubject(), predicate, object);
    }
    
    private URI getSubject(){
        Integer iter = iterator++;
        return vf.createURI(
                    "http://example.org/data/results#id" + iter.toString());
    }
    
    /**
     *
     * @return
     */
    public String getDateTime(){
        System.out.println("Current Date: " + time);
        return time;
    }
    
    /**
     *
     * @param object
     * @param term
     * @param description
     * @param method
     */
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
        URI predicate = vf.createURI("http://purl.org/dc/terms/description");
        Literal object = vf.createLiteral(description);
        resultGraph.add(getSubject(), predicate, object);
        logError(method, description);
    }
    
    private void addViolationRoot(Value object){
        URI predicate = vf.createURI("http://spinrdf.org/spl#violationRoot");
        resultGraph.add(getSubject(), predicate, object);
    }
    
    private void addViolationPath(Term term){
        URI predicate = vf.createURI("http://spinrdf.org/spl#violationPath");
        URI object = vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE + term);
        for(R2RMLTerm value : R2RMLTerm.values())
            if(value.equals(term))
                resultGraph.add(getSubject(), predicate, object );
    }
    
    /**
     *
     * @return
     */
    public RMLDataset getResultGraph(){
        return resultGraph ;
    }
    
    private void logError(String method, String description){
        log.error( method + ": " + description);
    }
    
}
