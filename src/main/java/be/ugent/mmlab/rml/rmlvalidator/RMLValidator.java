/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import static be.ugent.mmlab.rml.rmlvalidator.RMLMappingFactory.isLocalFile;
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
public class RMLValidator {
    
    // Log
    private static final Logger log = LogManager.getLogger(RMLMappingFactory.class);
    
    RMLValidator(){}
    
    private void checkInputExists(
            RMLSesameDataSet rmlMappingGraph, String RMLFile){
        if(!isLocalFile(RMLFile)){
            log.info("[RMLMappingFactory:extractRMLMapping] file "
                    + RMLFile + " loaded from URI.");
            HttpURLConnection con = null;
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
                Logger.getLogger(RMLMappingFactory.class.getName()).log(Level.ERROR, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RMLMappingFactory.class.getName()).log(Level.ERROR, null, ex);
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
    
    public void checkSource(
            Resource triplesMapSubject, List<Statement> statements) {
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no source for the Logicla Source.");
        }
    }
    
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
