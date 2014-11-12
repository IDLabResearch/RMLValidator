package be.ugent.mmlab.rml.extractor;

/**
 * *************************************************************************
 *
 * RML : RML Mapping Factory abstract class
 *
 * Factory responsible of RML Mapping generation.
 *
 * based on R2RMLMappingFactory in db2triples
 * 
 * modified by andimou
 *
 ***************************************************************************
 */


import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.std.StdLogicalSource;
import be.ugent.mmlab.rml.model.std.StdObjectMap;
import be.ugent.mmlab.rml.model.std.StdPredicateObjectMap;
import be.ugent.mmlab.rml.model.std.StdSubjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.rmlvalidator.RMLValidator;
import be.ugent.mmlab.rml.rml.RMLVocabulary;
import be.ugent.mmlab.rml.rml.RMLVocabulary.R2RMLTerm;
import be.ugent.mmlab.rml.rmlvalidator.RMLMappingValidator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class RMLValidatedMappingExtractor extends RMLUnValidatedMappingExtractor
    implements RMLMappingExtractor {

    public static RMLMapping extractRMLMapping(String homeandimouDesktopofferrmlttl, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    RMLMappingValidator validator;
    RMLUnValidatedMappingExtractor subextractor;

    // Log
    private static final Logger log = LogManager.getLogger(RMLValidatedMappingExtractor.class);   

    public RMLValidatedMappingExtractor(RMLMappingValidator validator) {
        this.validator = validator;
        this.subextractor = new RMLUnValidatedMappingExtractor();
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void RMLValidatedMappingExtractor(RMLMappingExtractor extractor, RMLValidator validator){
//        setRMLMappingValidator(validator);
    }
    
    /**
     *
     * @param validator
     */
    public void RMLValidatedMappingExtractor(RMLValidator validator){
        this.validator = validator;
    }
    
    //@Override
    //public void extract(String mappingDoc){
        //extractRMLMapping(mappingDoc);
    //}
    
    /**
     * Construct TriplesMap objects rule. A triples map is represented by a
     * resource that references the following other resources : - It must have
     * exactly one subject map * using the rr:subjectMap property.
     *
     * @param rmlMappingGraph
     * @return
     */
    @Override
        public Map<Resource, TriplesMap> extractTriplesMapResources(
            RMLSesameDataSet rmlMappingGraph) {
        Map<Resource, TriplesMap> triplesMapResources = new HashMap<Resource, TriplesMap>();
        
        List<Statement> statements = getTriplesMapResources(rmlMappingGraph);

        validator.checkTripleMapResources(statements);
        
        triplesMapResources = putTriplesMapResources(statements, triplesMapResources);

        return triplesMapResources;
    }

    private PredicateObjectMap extractPredicateObjectMap(
            RMLSesameDataSet r2rmlMappingGraph,
            Resource triplesMapSubject,
            Resource predicateObject,
            Set<GraphMap> savedGraphMaps,
            Map<Resource, TriplesMap> triplesMapResources,
            TriplesMap triplesMap){
            //throws InvalidR2RMLStructureException, R2RMLDataError,
            //InvalidR2RMLSyntaxException {
        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract predicate-object map..");
        // Extract predicate maps
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_MAP);
        
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                predicateObject, p, null);
        
        String termType = new Object(){}.getClass().getEnclosingMethod().getReturnType().getSimpleName();
        validator.checkEmptyStatements(triplesMap, statements, p, termType);
        //validator.checkMultipleStatements(triplesMap, statements, p, termType);
        
        Set<PredicateMap> predicateMaps = new HashSet<PredicateMap>();
        try {
            for (Statement statement : statements) {
                log.info("[RMLMappingFactory] saved Graphs " + savedGraphMaps);
                PredicateMap predicateMap = extractPredicateMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps, triplesMap);
                predicateMaps.add(predicateMap);
            }
        } catch (ClassCastException e) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + predicateObject.stringValue());
        }
        // Extract object maps
        URI o = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT_MAP);
        statements = r2rmlMappingGraph.tuplePattern(predicateObject, o, null);
        if (statements.size() < 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + predicateObject.stringValue()
                    + " has no object map defined : one or more is required.");
        }
        Set<ObjectMap> objectMaps = new HashSet<ObjectMap>();
        Set<ReferencingObjectMap> refObjectMaps = new HashSet<ReferencingObjectMap>();
        try {
            for (Statement statement : statements) {
                log.debug(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        //"[RMLMappingFactory:extractPredicateObjectMap] 
                        + "Try to extract object map..");
                ReferencingObjectMap refObjectMap = extractReferencingObjectMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps, triplesMapResources, triplesMap);
                if (refObjectMap != null) {
                    refObjectMaps.add(refObjectMap);
                    // Not a simple object map, skip to next.
                    continue;
                } 
                ObjectMap objectMap = extractObjectMap(r2rmlMappingGraph,
                        (Resource) statement.getObject(), savedGraphMaps, 
                        triplesMapResources, triplesMapSubject, triplesMap );
                
                objectMap.setOwnTriplesMap(triplesMapResources.get(triplesMapSubject));
                log.debug(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "ownTriplesMap attempted " 
                        + triplesMapResources.get(statement.getContext()) +
                        " for object " + statement.getObject().stringValue());
                objectMaps.add(objectMap);
            } 
        } catch (ClassCastException e) {
            log.error(
                    "[RMLMappingFactory:extractPredicateObjectMaps] "
                    + "A resource was expected in object of objectMap of "
                    + predicateObject.stringValue());
        }
        PredicateObjectMap predicateObjectMap = new StdPredicateObjectMap(
                predicateMaps, objectMaps, refObjectMaps);       
        
        // Add graphMaps
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, predicateObject, R2RMLTerm.GRAPH_MAP);
        
        if (graphMapValues != null) {
            graphMaps = extractGraphMapValues(
                    r2rmlMappingGraph, graphMapValues, savedGraphMaps, triplesMap);
            log.info(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "graph Maps returned " + graphMaps);
        }
        predicateObjectMap.setGraphMaps(graphMaps);
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract predicate-object map done.");
        return predicateObjectMap;
    }

    //AD:change it to re-use the one from RMLUnValidatedMappingExtractor
    private ObjectMap extractObjectMap(RMLSesameDataSet r2rmlMappingGraph,
            Resource object, Set<GraphMap> graphMaps, 
            Map<Resource, TriplesMap> triplesMapResources, Resource o, TriplesMap triplesMap){
            //throws InvalidR2RMLStructureException, R2RMLDataError,
            //InvalidR2RMLSyntaxException {
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": " 
                + "Extract object map..");
        
        // Extract object maps properties
        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.CONSTANT, triplesMap);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.TEMPLATE, triplesMap);
        String languageTag = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.LANGUAGE, triplesMap);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.TERM_TYPE, triplesMap);
        URI dataType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.DATATYPE, triplesMap);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.INVERSE_EXPRESSION, triplesMap);

        //MVS: Decide on ReferenceIdentifier
        ReferenceIdentifier referenceValue = 
                extractReferenceIdentifier(r2rmlMappingGraph, object, triplesMap);
        
        validator.checkTermMap(constantValue, stringTemplate, referenceValue, o.stringValue());

        StdObjectMap result = new StdObjectMap(null, constantValue, dataType,
                languageTag, stringTemplate, termType, inverseExpression,
                referenceValue);
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                //"[RMLMappingFactory:extractObjectMap] 
                + "Extract object map done.");
        return result;
    }

    /**
     * Extract subjectMap contents
     *
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @return
     */
    //AD:change it to reuse the one from RMLUnValidatedMappingExtractor
    @Override
    protected SubjectMap extractSubjectMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> savedGraphMaps, TriplesMap triplesMap) {
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract subject map...");

        // Extract subject map
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);

        validator.checkStatements(statements, p);

        Resource subjectMap = (Resource) statements.get(0).getObject();
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Found subject map : "
                + subjectMap.stringValue());

        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CONSTANT, triplesMap);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TEMPLATE, triplesMap);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TERM_TYPE, triplesMap);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.INVERSE_EXPRESSION, triplesMap);

        validator.checkTermMap(constantValue, stringTemplate, null, subjectMap.toString());

        //MVS: Decide on ReferenceIdentifier
        //TODO:Add check if the referenceValue is a valid reference according to the reference formulation
        ReferenceIdentifier referenceValue =
                extractReferenceIdentifier(r2rmlMappingGraph, subjectMap, triplesMap);
        //AD: The values of the rr:class property must be IRIs. 
        //AD: Would that mean that it can not be a reference to an extract of the input or a template?
        Set<URI> classIRIs = extractURIsFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CLASS);

        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, subjectMap, R2RMLTerm.GRAPH_MAP);

        if (graphMapValues != null) {
            graphMaps = extractGraphMapValues(r2rmlMappingGraph, graphMapValues, savedGraphMaps, triplesMap);
            log.info(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "graph Maps returned " + graphMaps);
        }
        SubjectMap result = new StdSubjectMap(triplesMap, constantValue,
                stringTemplate, termType, inverseExpression, referenceValue,
                classIRIs, graphMaps);
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Subject map extracted.");
        return result;
    }
   
   
    
    private Set<GraphMap> extractGraphMapValues(
            RMLSesameDataSet r2rmlMappingGraph, Set<Value> graphMapValues,
            Set<GraphMap> savedGraphMaps, TriplesMap triplesMap){
            //throws InvalidR2RMLStructureException {

        Set<GraphMap> graphMaps = new HashSet<GraphMap>();

        for (Value graphMap : graphMapValues) {
            // Create associated graphMap if it has not already created
            boolean found = false;
            GraphMap graphMapFound = null;

            if (found) {
                graphMaps.add(graphMapFound);
            } else {
                GraphMap newGraphMap = null;
                newGraphMap = extractGraphMap(r2rmlMappingGraph, (Resource) graphMap, triplesMap);

                savedGraphMaps.add(newGraphMap);
                graphMaps.add(newGraphMap);
            }
        }

        return graphMaps;
    }
    
    /**
     *
     * @param r2rmlMappingGraph
     * @param termType
     * @param term
     * @param triplesMap
     * @return
     */
    @Override
    protected Value extractValueFromTermMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource termType,
            Enum term, TriplesMap triplesMap) {
        Value value = subextractor.extractValueFromTermMap(
            r2rmlMappingGraph, termType, term, triplesMap) ;
        //AD:fix this
        //validator.checkMultipleStatements(triplesMap, statements, p, type);
        return value;
    }
   
    @Override
    protected LogicalSource extractLogicalSources(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, TriplesMap triplesMap) {

        Resource blankLogicalSource = 
                extractLogicalSource(rmlMappingGraph, triplesMapSubject, triplesMap);
        
        RMLVocabulary.QLTerm referenceFormulation =
                getReferenceFormulation(rmlMappingGraph, triplesMapSubject, blankLogicalSource, triplesMap);

        //Extract the iterator to create the iterator. Some formats have null, like CSV or SQL
        List<Statement> iterators = getStatements(
                rmlMappingGraph, blankLogicalSource,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.ITERATOR, triplesMap);

        validator.checkIterator(triplesMapSubject, iterators, referenceFormulation);

        List<Statement> sourceStatements = getStatements(
                rmlMappingGraph,blankLogicalSource,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.SOURCE, triplesMap);
        
        validator.checkSource(triplesMapSubject, sourceStatements);

        LogicalSource logicalSource = null;

        if (!sourceStatements.isEmpty()) {
            //Extract the file identifier
            String file = sourceStatements.get(0).getObject().stringValue();
            
            if(!iterators.isEmpty())
                logicalSource = 
                        new StdLogicalSource(iterators.get(0).getObject().stringValue(), 
                        file, referenceFormulation);
        }
        
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                //"[RMLMappingFactory:extractLogicalSource] 
                + "Logical source extracted : "
                + logicalSource);
        return logicalSource;
    }
    
    @Override
    protected Resource extractLogicalSource(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, TriplesMap triplesMap) {
        
        List<Statement> logicalSourceStatements = getStatements(
                rmlMappingGraph, triplesMapSubject,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.LOGICAL_SOURCE, triplesMap);

        Resource blankLogicalSource = 
                subextractor.extractLogicalSource(rmlMappingGraph, triplesMapSubject, triplesMap);
        
        validator.checkLogicalSource(triplesMapSubject, logicalSourceStatements, triplesMap);
        
        return blankLogicalSource;
    }
    
    /**
     *
     * @param rmlMappingGraph
     * @param triplesMapSubject
     * @param namespace
     * @param term
     * @param triplesMap
     * @return
     */
    @Override
        protected List<Statement> getStatements(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, 
            String namespace, RMLVocabulary.Term term, TriplesMap triplesMap){
        
        List<Statement> statements = 
                subextractor.getStatements(
                rmlMappingGraph, triplesMapSubject, namespace, term, triplesMap);
        
        validator.checkEmptyStatements(
                triplesMap, statements, rmlMappingGraph.URIref(namespace + term), namespace);
        //validator.checkMultipleStatements(
        //        triplesMap, statements, rmlMappingGraph.URIref(namespace + term), namespace);
        
        return statements;
    }
    
    /**
     *
     * @param rmlMappingGraph
     * @param term
     * @param termType
     * @param triplesMap
     * @return
     */
    @Override
        protected List<Statement> getStatements(
            RMLSesameDataSet rmlMappingGraph, Enum term,  Resource termType, TriplesMap triplesMap){
        URI p = getTermURI(rmlMappingGraph, term);

        List<Statement> statements = rmlMappingGraph.tuplePattern(termType,
                p, null);
        
        String type = new Object(){}.getClass().getEnclosingMethod().getReturnType().getSimpleName();
        
        //validator.checkMultipleStatements(triplesMap, statements, p, type);
        
        return statements;
    }
    
    /**
     *
     * @param rmlMappingGraph
     * @param triplesMapSubject
     * @param subject
     * @param triplesMap
     * @return
     */
    @Override
    protected RMLVocabulary.QLTerm getReferenceFormulation(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, 
            Resource subject, TriplesMap triplesMap) 
    {       
        List<Statement> statements = getStatements(
                rmlMappingGraph, subject, 
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.REFERENCE_FORMULATION, triplesMap);
        
        validator.checkReferenceFormulation(triplesMapSubject, statements);
        
        if (statements.isEmpty()) 
            return null;
        else
            return RMLVocabulary.getQLTerms(statements.get(0).getObject().stringValue());
    }
    
    public static boolean isLocalFile(String source) {
        try {
            new URL(source);
            return false;
        } catch (MalformedURLException e) {
            return true;
        }
    }

    private static void extractLogicalTable() {
        // TODO: Original R2RML Logic move here
    }
}
