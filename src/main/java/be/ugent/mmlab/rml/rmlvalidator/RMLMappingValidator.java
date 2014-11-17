/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import be.ugent.mmlab.rml.rml.RMLVocabulary;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author andimou
 */
public interface RMLMappingValidator {

    public void checkTriplesMapResources(List<Statement> statements);

    /**
     *
     * @param triplesMap
     * @param statements
     * @param p
     * @param termType
     */
    public void checkEmptyStatements(TriplesMap triplesMap, List<Statement> statements, URI p, String termType);

    public void checkMultipleStatements(TriplesMap triplesMap, List<Statement> statements, URI p, String termType);

    public void checkTermMap(Value constantValue, String stringTemplate, ReferenceIdentifier referenceValue, String stringValue);

    public void checkStatements(List<Statement> statements, URI p);

    public void checkIterator(Resource triplesMapSubject, List<Statement> iterators, RMLVocabulary.QLTerm referenceFormulation);

    public void checkSource(Resource triplesMapSubject, List<Statement> sourceStatements);

    public void checkLogicalSource(Resource triplesMapSubject, List<Statement> logicalSourceStatements, TriplesMap triplesMap);

    public void checkReferenceFormulation(Resource triplesMapSubject, List<Statement> statements);
}
