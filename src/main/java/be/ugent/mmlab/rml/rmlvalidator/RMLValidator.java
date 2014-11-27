/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.extractor.RMLValidatedMappingExtractor;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import be.ugent.mmlab.rml.rml.RMLVocabulary;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author andimou
 */
public class RMLValidator implements RMLMappingValidator {
    
    // Log
    private static final Logger log = LogManager.getLogger(RMLValidatedMappingExtractor.class);
    
    private static void launchPreChecks(RMLSesameDataSet rmlMappingGraph){
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + RMLVocabulary.R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                    + RMLVocabulary.R2RMLTerm.SUBJECT_MAP);
            List<Statement> otherStatements = rmlMappingGraph.tuplePattern(
                    s.getSubject(), p, null);
            if (otherStatements.isEmpty()) {
                log.error( 
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Triples map without subject map : "
                        + s.getSubject().stringValue() + ".");
            }
        }
    }
    
        
    /**
     *
     * @param triplesMapSubject
     * @param statements
     * @param triplesMap
     */
    @Override
    public void checkLogicalSource(
            Resource triplesMapSubject, List<Statement> statements, TriplesMap triplesMap){
        if (statements.isEmpty()) {
            log.error( 
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no logical source defined.");
        }
        else if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has too many logical source defined.");
        }        
    }
    
    /**
     *
     * @param triplesMapSubject
     * @param statements
     */
    @Override
    public void checkReferenceFormulation(
            Resource triplesMapSubject, List<Statement> statements) { // RMLVocabulary.QLTerm referenceFormulation) {
        if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has too many reference formulations defined.");
        } else if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no reference formulation.");
        } else if (RMLVocabulary.getQLTerms(statements.get(0).getObject().stringValue()) == null) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has unknown reference formulation.");
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
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no source for the Logicla Source.");
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
            RMLVocabulary.QLTerm referenceFormulation) {
        if (statements.isEmpty() && referenceFormulation != RMLVocabulary.QLTerm.CSV_CLASS) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no iterator.");
        } else if (!statements.isEmpty() && referenceFormulation == RMLVocabulary.QLTerm.CSV_CLASS) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " no iterator is required.");
        }
    }
    
    @Override
    public void checkTriplesMapResources(List<Statement> statements){
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    +"No subject statement found. ");
        } else if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + statements.get(0).getSubject()
                    + " has many subjectMap "
                    + "(or subject) but only one is required.");
        }
    }
    
    /**
     *
     * @param statements
     * @param term
     */
    @Override
    public void checkStatements(TriplesMap triplesMap, List<Statement> statements, URI term){
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "No " + term.getClass().toString()
                    +" statement found. ");
        } else if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMap.getName()
                    + " has many " + term.getLocalName() 
                    + " but only one is required.");
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
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "The "
                    + statements.toString()
                    + " of "  
                    + resource.stringValue() 
                    + " has no " 
                    + term.getLocalName()); 
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
        
        if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMap.getName()
                    + " has many " + term.getLocalName()
                    + " at " + type
                    + " but only one is required.");
        }
    }
    
    @Override
    public void checkTermMap(
            Value constantValue, String stringTemplate, 
            ReferenceIdentifier referenceValue, String resource){
        if(constantValue != null && stringTemplate != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + " contains a Term Map that has"
                    + " both constant and template.");
        else if(constantValue != null && referenceValue != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + " contains a Term Map that has"
                    + " both constant and reference.");
        else if(stringTemplate != null && referenceValue != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + " contains a Term Map that has"
                    + " both template and reference.");
        else if(stringTemplate == null && referenceValue == null && constantValue == null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource.toString()
                    + " contains a Term Map that should have"
                    + " a constant, a string template or a reference.");           
    }
}
