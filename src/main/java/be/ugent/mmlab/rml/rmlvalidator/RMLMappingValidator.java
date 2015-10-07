package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.termMap.ReferenceMap;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import be.ugent.mmlab.rml.vocabularies.Term;
import java.util.List;
import java.util.Map;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * *************************************************************************
 *
 * RML - Validator : RMLMappingValidator
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public interface RMLMappingValidator {
    RMLValidatorResult validationResult = new RMLValidatorResult();

    /**
     *
     * @param triplesMapResources
     */
    public void checkTriplesMapResources(
            Map<Resource, TriplesMap> triplesMapResources);
    
    /**
     *
     * @param subjMap
     */
    public void checkSubjectMap(SubjectMap subjMap);

    /**
     *
     * @param triplesMap
     * @param statements
     * @param p
     * @param resource
     */
    public void checkEmptyStatements(TriplesMap triplesMap, 
            List<Statement> statements, URI p, Resource resource);

    /**
     *
     * @param triplesMap
     * @param statements
     * @param p
     * @param termType
     */
    public void checkMultipleStatements(TriplesMap triplesMap, 
            List<Statement> statements, URI p, String termType);

    /**
     *
     * @param constantValue
     * @param stringTemplate
     * @param referenceValue
     * @param stringValue
     * @param term
     */
    public void checkTermMap(
            Value constantValue, String stringTemplate, 
            ReferenceMap referenceValue, Resource stringValue, Term term);

    /**
     *
     * @param resource
     * @param statements
     * @param p
     */
    public void checkStatements(
            Resource resource, List<Statement> statements, Term p);

    /**
     *
     * @param triplesMapSubject
     * @param iterators
     * @param referenceFormulation
     */
    public void checkIterator(
            Resource triplesMapSubject, List<Statement> iterators, 
            QLVocabulary.QLTerm referenceFormulation);

    /**
     *
     * @param triplesMapSubject
     * @param sourceStatements
     */
    public void checkSource(
            Resource triplesMapSubject, List<Statement> sourceStatements);

    /**
     *
     * @param triplesMapSubject
     * @param logicalSourceStatements
     * @param triplesMap
     */
    public void checkLogicalSource(
            Resource triplesMapSubject, List<Statement> logicalSourceStatements, 
            TriplesMap triplesMap);

    /**
     *
     * @param triplesMapSubject
     * @param statements
     */
    public void checkReferenceFormulation(Resource triplesMapSubject, 
            List<Statement> statements);
}
