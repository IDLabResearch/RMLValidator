package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.termMap.ReferenceMap;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import be.ugent.mmlab.rml.vocabularies.R2RMLVocabulary;
import be.ugent.mmlab.rml.vocabularies.R2RMLVocabulary.R2RMLTerm;
import be.ugent.mmlab.rml.vocabularies.RMLVocabulary.RMLTerm;
import be.ugent.mmlab.rml.vocabularies.Term;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * *************************************************************************
 *
 * RML - Mapping Document Handler : RMLValidator
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public class RMLValidator implements RMLMappingValidator {
    
    // Log
    static final Logger log = LoggerFactory.getLogger(RMLValidator.class);
    
    private RMLValidatorResult validres = new RMLValidatorResult();   
        
    /**
     *
     * @param triplesMapSubject
     * @param statements
     * @param triplesMap
     */
    @Override
    public void checkLogicalSource(
            Resource triplesMapSubject, 
            List<Statement> statements, TriplesMap triplesMap){
        Value object; 
        ValueFactory vf  = new ValueFactoryImpl();
        
        if (statements.isEmpty()) {
            String objectValue = triplesMapSubject.toString() 
                    + " has no logical source defined.";
            object = vf.createURI(triplesMapSubject.toString());
            
            validres.addViolation(
                    object, RMLTerm.LOGICAL_SOURCE, 
                    objectValue, Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        else if (statements.size() > 1) {
            String objectValue = triplesMapSubject.toString() 
                    + " has too many logical source defined.";
            object = vf.createURI(triplesMapSubject.toString());
            validres.addViolation(
                    object, RMLTerm.LOGICAL_SOURCE, 
                    objectValue, Thread.currentThread().getStackTrace()[1].getMethodName());
        } 
    }
    
    /**
     *
     * @param triplesMapSubject
     * @param statements
     */
    @Override
    public void checkReferenceFormulation(
            Resource triplesMapSubject, List<Statement> statements) { 
        Value object ;
        ValueFactory vf  = new ValueFactoryImpl();
        String objectValue;

        if (statements.size() > 1) {
            object = statements.get(0).getSubject();
            objectValue = triplesMapSubject.toString() 
                    + " has too many reference formulations defined.";
            validres.addViolation(
                    object, RMLTerm.REFERENCE_FORMULATION, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        } else if (statements.isEmpty()) {
            object = triplesMapSubject;
            objectValue = "There is no reference formulation.";
            validres.addViolation(
                    object, RMLTerm.REFERENCE_FORMULATION,
                    objectValue, Thread.currentThread().getStackTrace()[1].getMethodName());
        } else if (QLVocabulary.getQLTerms(statements.get(0).getObject().stringValue()) == null) {
            object = statements.get(0).getSubject();
            objectValue = triplesMapSubject.toString() 
                    + " has unknown reference formulation.";
            validres.addViolation(object, RMLTerm.REFERENCE_FORMULATION,
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
    
    /**
     *
     * @param triplesMapSubject
     * @param statements
     */
    @Override
    public void checkSource(
            Resource triplesMapSubject, List<Statement> statements) {
        Value object;
        ValueFactory vf  = new ValueFactoryImpl();
        String objectValue;
        
        if (statements.isEmpty()) {
            if(triplesMapSubject == null)
                object = null;
            else
                object = vf.createURI(triplesMapSubject.toString());
            objectValue = "There is no source for the Logical Source.";
            validres.addViolation(object, RMLTerm.SOURCE, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
    
    /**
     *
     * @param triplesMapSubject
     * @param statements
     * @param referenceFormulation
     */
    @Override
    public void checkIterator(
            Resource triplesMapSubject, List<Statement> statements,
            QLVocabulary.QLTerm referenceFormulation) {
        Value object;
        String objectValue;
        
        if (statements.isEmpty() && referenceFormulation != QLVocabulary.QLTerm.CSV_CLASS) {
            object = triplesMapSubject;
            objectValue = "There is no iterator.";
            validres.addViolation(object, RMLTerm.ITERATOR, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        } else if (!statements.isEmpty() && referenceFormulation == QLVocabulary.QLTerm.CSV_CLASS) {
            object = triplesMapSubject;
            objectValue = triplesMapSubject.toString() + " no iterator is required.";
            validres.addViolation(object, RMLTerm.ITERATOR, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
    
    @Override
    public void checkTriplesMapResources( Map<Resource, TriplesMap> triplesMapResources){ 
        Value object;
        String objectValue;
        if (triplesMapResources.isEmpty()) {
            object = null;
            objectValue = "The mapping document has no Triples Maps. ";
            validres.addViolation(object, R2RMLTerm.TRIPLES_MAP_CLASS, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        } 
    }
    
    /**
     *
     * @param subjMap
     */
    @Override
    public void checkSubjectMap(SubjectMap subjMap){ 
        Value object;
        ValueFactory vf  = new ValueFactoryImpl();
        String objectValue;
        if (subjMap == null) {
            object = null;
            objectValue = "The Triples Map has no Subject Map. ";
            validres.addViolation(
                    object, R2RMLTerm.SUBJECT_MAP, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        } 
    }
    
    /**
     *
     * @param statements
     * @param term
     */
    @Override
    public void checkStatements(Resource resource, List<Statement> statements, Term term){
        ValueFactory vf  = new ValueFactoryImpl();
        Value object;
        String objectValue;
        if (statements.isEmpty()) {
            object = vf.createURI(resource.toString());
            objectValue = "No " + term + " statement found for " + object.stringValue();
            validres.addViolation(
                    object, term, 
                    objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        } else if (statements.size() > 1 && term != R2RMLVocabulary.R2RMLTerm.PREDICATE_MAP) {
            object = vf.createURI(resource.toString());
            objectValue = resource.stringValue()
                    + " has many " + term  
                    + " but only one is required.";
            validres.addViolation(
                    object, term, objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
    
    /**
     *
     * @param triplesMap
     * @param statements
     * @param term
     * @param type
     */
    @Override
    public void checkEmptyStatements(
            TriplesMap triplesMap, List<Statement> statements, URI term, Resource resource){
        ValueFactory vf  = new ValueFactoryImpl();
        Value object;
        if (statements.isEmpty()) {
            /*log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "The "
                    + statements.toString()
                    + " of "  
                    + resource.stringValue() 
                    + " has no " 
                    + term.getLocalName()); */
            //predicate = validres.getResultGraph().URIref("http://purl.org/dc/terms/description");
            object = vf.createLiteral("The "
                    //+ statements.get(0).getObject().toString()
                    //+ " of "  
                    + triplesMap.getName().toString()
                    + " has no " 
                    + term.getLocalName());
            //validres.addViolation(triplesMap.getName(), object);
        }
    }
    
    /**
     *
     * @param triplesMap
     * @param statements
     * @param term
     * @param type
     */
    @Override
    public void checkMultipleStatements(
            TriplesMap triplesMap, List<Statement> statements, URI term, String type){
        ValueFactory vf  = new ValueFactoryImpl();
        Value object;
        if (statements.size() > 1) {
            /*log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMap.getName()
                    + " has many " + term.getLocalName()
                    + " at " + type
                    + " but only one is required.");*/
            //predicate = validres.getResultGraph().URIref("http://purl.org/dc/terms/description");
            object = vf.createLiteral(triplesMap.getName()
                    + " has many " + term.getLocalName()
                    + " at " + type
                    + " but only one is required.");
            //validres.addViolation(object);
        }
    }
    
    @Override
    public void checkTermMap(
            Value constantValue, String stringTemplate, 
            ReferenceMap referenceValue, Resource resource, Term term){
        ValueFactory vf  = new ValueFactoryImpl();
        Value object;
        String objectValue;
        if(constantValue != null && stringTemplate != null){
            object = vf.createLiteral(resource.stringValue());
            objectValue = resource + " is a Term Map that has both constant and template.";
            validres.addViolation(
                    object, term, objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        else if(constantValue != null && referenceValue != null){
            object = vf.createLiteral(resource.stringValue());
            objectValue = resource
                    + " is a " 
                    + term.toString()
                    + " Term Map that has"
                    + " both constant and reference.";
            validres.addViolation(
                    object, term,  objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        else if(stringTemplate != null && referenceValue != null){
            object = vf.createLiteral(resource.stringValue());
            objectValue = resource
                    + " is a " 
                    + term.toString()
                    + " Term Map that has"
                    + " both template and reference.";
            validres.addViolation(
                    object,  term, objectValue,Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        else if(stringTemplate == null && referenceValue == null && constantValue == null & resource == null){
            object = vf.createLiteral(resource.stringValue());
            objectValue = resource.toString()
                    + " is a " 
                    + term.toString()
                    + " Term Map that should have"
                    + " a constant, a string template or a reference.";
            validres.addViolation(
                    object, term, objectValue, Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
}
