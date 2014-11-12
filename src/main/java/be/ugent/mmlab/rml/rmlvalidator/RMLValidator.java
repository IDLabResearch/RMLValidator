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
import static be.ugent.mmlab.rml.extractor.RMLValidatedMappingExtractor.isLocalFile;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Level;
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
    
    private static void launchPreChecks(RMLSesameDataSet r2rmlMappingGraph){
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + RMLVocabulary.R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                    + RMLVocabulary.R2RMLTerm.SUBJECT_MAP);
            List<Statement> otherStatements = r2rmlMappingGraph.tuplePattern(
                    s.getSubject(), p, null);
            if (otherStatements.isEmpty()) {
                log.error( 
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Triples map without subject map : "
                        + s.getSubject().stringValue() + ".");
            }
        }
    }
    
    private void checkInputExists(
            RMLSesameDataSet rmlMappingGraph, String RMLFile){
        if(!isLocalFile(RMLFile)){
            log.info("[RMLMappingFactory:extractRMLMapping] file "
                    + RMLFile + " loaded from URI.");
            HttpURLConnection con ;
            try {
                con = (HttpURLConnection) new URL(RMLFile).openConnection();
                con.setRequestMethod("HEAD");
                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    log.error(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "INPUT error: "
                        + RMLFile.toString()
                        + " was not found.");
                }
            } catch (MalformedURLException ex) {
                log.error(ex);
                Logger.getLogger(RMLValidatedMappingExtractor.class.getName()).log(Level.ERROR, null, ex);
            } catch (IOException ex) {
                log.error(ex);
                Logger.getLogger(RMLValidatedMappingExtractor.class.getName()).log(Level.ERROR, null, ex);
            }
        }
        //RML document is a a local file
        else {
            File f = new File(RMLFile);
            if (!f.exists()) {
                log.error(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "INPUT error: "
                        + RMLFile.toString()
                        + " does not exist.");
            } else if (f.isDirectory()) {
                log.error(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "INPUT error: "
                        + RMLFile.toString()
                        + " is a Directory.");
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
    public void checkTripleMapResources(List<Statement> statements){
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
    public void checkStatements(List<Statement> statements, URI term){
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "No " + term.getClass().toString()
                    +" statement found. ");
        } else if (statements.size() > 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + statements.get(0).getSubject()
                    + " has many " + term.getClass().toString()
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
            TriplesMap triplesMap, List<Statement> statements, URI term, String type){
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMap.getName() + " "
                    + term.getLocalName()
                    + " has " + term.getClass().toString()
                    + " at " + type
                    +" with no statement found. ");
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
