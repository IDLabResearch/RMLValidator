package be.ugent.mmlab.rml.rmlvalidator;

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
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.StdGraphMap;
import be.ugent.mmlab.rml.model.StdJoinCondition;
import be.ugent.mmlab.rml.model.StdLogicalSource;
import be.ugent.mmlab.rml.model.StdObjectMap;
import be.ugent.mmlab.rml.model.StdPredicateMap;
import be.ugent.mmlab.rml.model.StdPredicateObjectMap;
import be.ugent.mmlab.rml.model.StdReferencingObjectMap;
import be.ugent.mmlab.rml.model.StdSubjectMap;
import be.ugent.mmlab.rml.model.StdTriplesMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifierImpl;
import be.ugent.mmlab.rml.rmlvalidator.RMLVocabulary.R2RMLTerm;
import be.ugent.mmlab.rml.rmlvalidator.RMLVocabulary.RMLTerm;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

public abstract class RMLMappingFactory {

    // Log
    private static final Logger log = LogManager.getLogger(RMLMappingFactory.class);
    // Value factory
    private static ValueFactory vf = new ValueFactoryImpl();

    /**
     * Extract RML Mapping object from a RML file written with Turtle syntax.
     *
     * Important : The R2RML vocabulary also includes the following R2RML
     * classes, which represent various R2RML mapping constructs. Using these
     * classes is optional in a mapping graph. The applicable class of a
     * resource can always be inferred from its properties. Consequently, in
     * order to identify each triple type, a rule will be used to extract the
     * applicable class of a resource.
     *
     * @param fileToRMLFile
     * @return
     * @throws InvalidR2RMLSyntaxException
     * @throws InvalidR2RMLStructureException
     * @throws R2RMLDataError
     * @throws IOException
     * @throws RDFParseException
     * @throws RepositoryException
     */
    public static RMLMapping extractRMLMapping(String fileToRMLFile)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError, RepositoryException, RDFParseException, IOException {
        // Load RDF data from R2RML Mapping document
        RMLSesameDataSet r2rmlMappingGraph = new RMLSesameDataSet();
        
        //RML document is a a URI
        if(!isLocalFile(fileToRMLFile)){
            log.info("[RMLMappingFactory:extractRMLMapping] file "
                    + fileToRMLFile + " loaded from URI.");
            HttpURLConnection con = (HttpURLConnection) new URL(fileToRMLFile).openConnection();
            con.setRequestMethod("HEAD");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try {
                    r2rmlMappingGraph.addURI(fileToRMLFile, RDFFormat.TURTLE);
                } catch (Exception e) {
                    log.error("[RMLMapping Factory:extractRMLMapping] " + e);
                }
            }
        }
        //RML document is a a local file
        else {
            r2rmlMappingGraph.loadDataFromFile(fileToRMLFile, RDFFormat.TURTLE);
        }
        
        log.debug("[RMLMappingFactory:extractRMLMapping] Number of R2RML triples in file "
                + fileToRMLFile + " : " + r2rmlMappingGraph.getSize() + " from local file");
        // Transform RDF with replacement shortcuts
        replaceShortcuts(r2rmlMappingGraph);
        // Run few tests to help user in its RDF syntax
        //launchPreChecks(r2rmlMappingGraph);
        // Construct R2RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = extractTripleMapResources(r2rmlMappingGraph);

        log.debug("[RMLMappingFactory:extractRMLMapping] Number of RML triples with "
                + " type "
                + R2RMLTerm.TRIPLES_MAP_CLASS
                + " in file "
                + fileToRMLFile + " : " + triplesMapResources.size());
        // Fill each triplesMap object
        for (Resource triplesMapResource : triplesMapResources.keySet()) // Extract each triplesMap
        {
            extractTriplesMap(r2rmlMappingGraph, triplesMapResource,
                    triplesMapResources);
        }
        // Generate RMLMapping object
        RMLMapping result = new RMLMapping(triplesMapResources.values());
        return result;
    }

    /**
     * Constant-valued term maps can be expressed more concisely using the
     * constant shortcut properties rr:subject, rr:predicate, rr:object and
     * rr:graph. Occurrences of these properties must be treated exactly as if
     * the following triples were present in the mapping graph instead.
     *
     * @param r2rmlMappingGraph
     */
    private static void replaceShortcuts(RMLSesameDataSet r2rmlMappingGraph) {
        Map<URI, URI> shortcutPredicates = new HashMap<URI, URI>();
        shortcutPredicates.put(
                vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT),
                vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT_MAP));
        shortcutPredicates.put(
                vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE),
                vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_MAP));
        shortcutPredicates.put(vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT), vf
                .createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT_MAP));
        shortcutPredicates
                .put(vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.GRAPH),
                vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.GRAPH_MAP));
        for (URI u : shortcutPredicates.keySet()) {
            List<Statement> shortcutTriples = r2rmlMappingGraph.tuplePattern(
                    null, u, null);
            log.debug("[RMLMappingFactory:replaceShortcuts] Number of R2RML shortcuts found "
                    + "for "
                    + u.getLocalName()
                    + " : "
                    + shortcutTriples.size());
            for (Statement shortcutTriple : shortcutTriples) {
                r2rmlMappingGraph.remove(shortcutTriple.getSubject(),
                        shortcutTriple.getPredicate(),
                        shortcutTriple.getObject());
                BNode blankMap = vf.createBNode();

                URI pMap = vf.createURI(shortcutPredicates.get(u).toString());
                URI pConstant = vf.createURI(RMLVocabulary.R2RML_NAMESPACE
                        + R2RMLTerm.CONSTANT);
                r2rmlMappingGraph.add(shortcutTriple.getSubject(), pMap,
                        blankMap);
                r2rmlMappingGraph.add(blankMap, pConstant,
                        shortcutTriple.getObject());
            }
        }
    }

    /**
     * Construct TriplesMap objects rule. A triples map is represented by a
     * resource that references the following other resources : - It must have
     * exactly one subject map * using the rr:subjectMap property.
     *
     * @param r2rmlMappingGraph
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Map<Resource, TriplesMap> extractTripleMapResources(
            RMLSesameDataSet r2rmlMappingGraph)
            throws InvalidR2RMLStructureException {
        // A triples map is represented by a resource that references the
        // following other resources :
        // - It must have exactly one subject map
        Map<Resource, TriplesMap> triplesMapResources = new HashMap<Resource, TriplesMap>();
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + RMLVocabulary.R2RMLTerm.SUBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
                null);

        checkTripleMapResources(statements);

        for (Statement s : statements) {
            List<Statement> otherStatements = r2rmlMappingGraph
                    .tuplePattern(s.getSubject(), p, null);
            //if (otherStatements.size() == 1) {
                triplesMapResources.put(s.getSubject(), new StdTriplesMap(
                        null, null, null, s.getSubject().stringValue()));
            //}
        }
        return triplesMapResources;
    }

    private static void launchPreChecks(RMLSesameDataSet r2rmlMappingGraph)
            throws InvalidR2RMLStructureException {
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                    + R2RMLTerm.SUBJECT_MAP);
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

    /**
     * Extract triplesMap contents.
     *
     * @param triplesMap
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @param triplesMapResources
     * @param storedTriplesMaps
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static void extractTriplesMap(RMLSesameDataSet r2rmlMappingGraph,
            Resource triplesMapSubject,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError {

        if (log.isDebugEnabled()) {
            log.debug("[RMLMappingFactory:extractTriplesMap] Extract TriplesMap subject : "
                    + triplesMapSubject.stringValue());
        }

        TriplesMap result = triplesMapResources.get(triplesMapSubject);

        // Extract TriplesMap properties
        LogicalSource logicalSource = extractLogicalSource(r2rmlMappingGraph, triplesMapSubject);

        // Extract subject
        // Create a graph maps storage to save all met graph uri during parsing.
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        //log.debug("[RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
        //        + graphMaps.size());
        SubjectMap subjectMap = extractSubjectMap(r2rmlMappingGraph,
                triplesMapSubject, graphMaps, result);
        //log.debug("[RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
        //        + graphMaps.size());
        // Extract predicate-object maps
        Set<PredicateObjectMap> predicateObjectMaps = extractPredicateObjectMaps(
                r2rmlMappingGraph, triplesMapSubject, graphMaps, result,
                triplesMapResources);
        log.debug("[RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
                + graphMaps.size());
        // Fill triplesMap
        for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
            result.addPredicateObjectMap(predicateObjectMap);
        }
        result.setLogicalSource(logicalSource);
        result.setSubjectMap(subjectMap);
        log.debug("[RMLMappingFactory:extractTriplesMap] Extract of TriplesMap subject : "
                + triplesMapSubject.stringValue() + " done.");
    }

    private static Set<PredicateObjectMap> extractPredicateObjectMaps(
            RMLSesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> graphMaps, TriplesMap result,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractPredicateObjectMaps] Extract predicate-object maps...");
        // Extract predicate-object maps
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);
        
        Set<PredicateObjectMap> predicateObjectMaps = new HashSet<PredicateObjectMap>();
        try {
            for (Statement statement : statements) {
                PredicateObjectMap predicateObjectMap = extractPredicateObjectMap(
                        r2rmlMappingGraph, triplesMapSubject,
                        (Resource) statement.getObject(),
                        graphMaps, triplesMapResources);
                // Add own tripleMap to predicateObjectMap
                predicateObjectMap.setOwnTriplesMap(result);
                predicateObjectMaps.add(predicateObjectMap);
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractPredicateObjectMaps] "
                    + "A resource was expected in object of predicateObjectMap of "
                    + triplesMapSubject.stringValue());
        }
        log.debug("[RMLMappingFactory:extractPredicateObjectMaps] Number of extracted predicate-object maps : "
                + predicateObjectMaps.size());
        return predicateObjectMaps;
    }
    /*
     * Still needs changing
     */

    private static PredicateObjectMap extractPredicateObjectMap(
            RMLSesameDataSet r2rmlMappingGraph,
            Resource triplesMapSubject,
            Resource predicateObject,
            Set<GraphMap> savedGraphMaps,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map..");
        // Extract predicate maps
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_MAP);
        
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                predicateObject, p, null);
        
        checkStatements(statements, p);

        /*if (statements.size() < 1) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + predicateObject.stringValue()
                    + " has no predicate map defined : one or more is required.");
        }*/
        Set<PredicateMap> predicateMaps = new HashSet<PredicateMap>();
        try {
            for (Statement statement : statements) {
                log.info("[RMLMappingFactory] saved Graphs " + savedGraphMaps);
                PredicateMap predicateMap = extractPredicateMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps);
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
                log.debug("[RMLMappingFactory:extractPredicateObjectMap] Try to extract object map..");
                ReferencingObjectMap refObjectMap = extractReferencingObjectMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps, triplesMapResources);
                if (refObjectMap != null) {
                    refObjectMaps.add(refObjectMap);
                    // Not a simple object map, skip to next.
                    continue;
                } 
                ObjectMap objectMap = extractObjectMap(r2rmlMappingGraph,
                        (Resource) statement.getObject(), savedGraphMaps, 
                        triplesMapResources, triplesMapSubject );
                
                objectMap.setOwnTriplesMap(triplesMapResources.get(triplesMapSubject));
                log.debug("[RMLMappingFactory:extractPredicateObjectMap] ownTriplesMap attempted " 
                        + triplesMapResources.get(statement.getContext()) +
                        " for object " + statement.getObject().stringValue());
                objectMaps.add(objectMap);
            } 
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
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
            graphMaps = extractGraphMapValues(r2rmlMappingGraph, graphMapValues, savedGraphMaps);
            log.info("[RMLMappingFactory] graph Maps returned " + graphMaps);
        }
        /*Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        log.debug("[RMLMappingFactory] GraphMaps " + graphMaps);
        if (graphMapValues != null) {
            for (Value graphMap : graphMapValues) {
                log.info("[RMLMappingFactory] graph map + " + graphMap);
                // Create associated graphMap if it has not already created
                boolean found = false;
                GraphMap graphMapFound = null;
                /*
                 * for (GraphMap savedGraphMap : savedGraphMaps) if
                 * (savedGraphMap.getGraph().equals(graphMap)) { found = true;
                 * graphMapFound = savedGraphMap; }
                 */
        /*        if (found) {
                    log.info("[RMLMappingFactory] graph map + " + graphMap);
                    graphMaps.add(graphMapFound);
                } else {
                    GraphMap newGraphMap = extractGraphMap(r2rmlMappingGraph,
                            (Resource) graphMap);
                    savedGraphMaps.add(newGraphMap);
                    graphMaps.add(newGraphMap);
                    log.info("[RMLMappingFactory] new graph map + " + newGraphMap);
                }
            }
        }*/
        predicateObjectMap.setGraphMaps(graphMaps);
        log.debug("[RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map done.");
        return predicateObjectMap;
    }
    /*
     * Still needs changing
     */

    private static ReferencingObjectMap extractReferencingObjectMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource object,
            Set<GraphMap> graphMaps,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map..");
        URI parentTriplesMap = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.PARENT_TRIPLES_MAP);
        Set<JoinCondition> joinConditions = extractJoinConditions(
                r2rmlMappingGraph, object);
        if (parentTriplesMap == null && !joinConditions.isEmpty()) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractReferencingObjectMap] "
                    + object.stringValue()
                    + " has no parentTriplesMap map defined whereas one or more joinConditions exist"
                    + " : exactly one parentTripleMap is required.");
        }
        if (parentTriplesMap == null && joinConditions.isEmpty()) {
            log.debug("[RMLMappingFactory:extractReferencingObjectMap] This object map is not a referencing object map.");
            return null;
        }
        // Extract parent
        boolean contains = false;
        TriplesMap parent = null;
        for (Resource triplesMapResource : triplesMapResources.keySet()) {
            if (triplesMapResource.stringValue().equals(
                    parentTriplesMap.stringValue())) {
                contains = true;
                parent = triplesMapResources.get(triplesMapResource);
                log.debug("[RMLMappingFactory:extractReferencingObjectMap] Parent triples map found : "
                        + triplesMapResource.stringValue());
                break;
            }
        }
        if (!contains) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractReferencingObjectMap] "
                    + object.stringValue()
                    + " reference to parent triples maps is broken : "
                    + parentTriplesMap.stringValue() + " not found.");
        }
        // Link between this reerencing object and its triplesMap parent will be
        // performed
        // at the end f treatment.
        ReferencingObjectMap refObjectMap = new StdReferencingObjectMap(null,
                parent, joinConditions);
        log.debug("[RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map done.");
        return refObjectMap;
    }
    /*
     * Still needs changing
     */

    private static Set<JoinCondition> extractJoinConditions(
            RMLSesameDataSet r2rmlMappingGraph, Resource object)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractJoinConditions] Extract join conditions..");
        Set<JoinCondition> result = new HashSet<JoinCondition>();
        // Extract predicate-object maps
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.JOIN_CONDITION);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(object, p,
                null);
        try {
            for (Statement statement : statements) {
                Resource jc = (Resource) statement.getObject();
                String child = extractLiteralFromTermMap(r2rmlMappingGraph, jc,
                        R2RMLTerm.CHILD);
                String parent = extractLiteralFromTermMap(r2rmlMappingGraph,
                        jc, R2RMLTerm.PARENT);
                if (parent == null || child == null) {
                    throw new InvalidR2RMLStructureException(
                            "[RMLMappingFactory:extractReferencingObjectMap] "
                            + object.stringValue()
                            + " must have exactly two properties child and parent. ");
                }
                result.add(new StdJoinCondition(child, parent));
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractJoinConditions] "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        }
        log.debug("[RMLMappingFactory:extractJoinConditions] Extract join conditions done.");
        return result;
    }
    /*
     * Still needs changing
     */

    private static ObjectMap extractObjectMap(RMLSesameDataSet r2rmlMappingGraph,
            Resource object, Set<GraphMap> graphMaps, 
            Map<Resource, TriplesMap> triplesMapResources, Resource o)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractObjectMap] Extract object map..");
        // Extract object maps properties
        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.TEMPLATE);
        String languageTag = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.LANGUAGE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.TERM_TYPE);
        URI dataType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.DATATYPE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.INVERSE_EXPRESSION);

        //MVS: Decide on ReferenceIdentifier
        ReferenceIdentifier referenceValue = extractReferenceIdentifier(r2rmlMappingGraph, object);
        
        checkTermMap(constantValue, stringTemplate, referenceValue, o.stringValue());

        StdObjectMap result = new StdObjectMap(null, constantValue, dataType,
                languageTag, stringTemplate, termType, inverseExpression,
                referenceValue);
        log.debug("[RMLMappingFactory:extractObjectMap] Extract object map done.");
        return result;
    }

    private static ReferenceIdentifier extractReferenceIdentifier(RMLSesameDataSet r2rmlMappingGraph, Resource resource) throws InvalidR2RMLStructureException {
        //MVS: look for a reference or column, prefer rr:column
        String columnValueStr = extractLiteralFromTermMap(r2rmlMappingGraph, resource, R2RMLTerm.COLUMN);
        String referenceValueStr = extractLiteralFromTermMap(r2rmlMappingGraph, resource, RMLTerm.REFERENCE);

        if (columnValueStr != null && referenceValueStr != null) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractReferenceIdentifier] "
                    + resource
                    + " has a reference and column defined.");
        }

        //MVS: use the generic ReferenceIdentifier to represent rr:column or rml:reference
        if (columnValueStr != null) {
            return ReferenceIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        }

        return ReferenceIdentifierImpl.buildFromR2RMLConfigFile(referenceValueStr);
    }

    private static PredicateMap extractPredicateMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource object,
            Set<GraphMap> graphMaps) throws InvalidR2RMLStructureException,
            R2RMLDataError, InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractPredicateMap] Extract predicate map..");
        // Extract object maps properties
        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.TEMPLATE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.TERM_TYPE);

        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.INVERSE_EXPRESSION);

        //MVS: Decide on ReferenceIdentifier
        ReferenceIdentifier referenceValue = extractReferenceIdentifier(r2rmlMappingGraph, object);

        PredicateMap result = new StdPredicateMap(null, constantValue,
                stringTemplate, inverseExpression, referenceValue, termType);
        log.debug("[RMLMappingFactory:extractPredicateMap] Extract predicate map done.");
        return result;
    }

    /**
     * Extract subjectMap contents
     *
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @return
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static SubjectMap extractSubjectMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> savedGraphMaps, TriplesMap ownTriplesMap)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractPredicateObjectMaps] Extract subject map...");
        // Extract subject map
        URI p = r2rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);
        
        checkStatements(statements, p);

        Resource subjectMap = (Resource) statements.get(0).getObject();
        log.debug("[RMLMappingFactory:extractTriplesMap] Found subject map : "
                + subjectMap.stringValue());

        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TEMPLATE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TERM_TYPE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.INVERSE_EXPRESSION);
        
        checkTermMap(constantValue, stringTemplate, null, subjectMap.toString());

        //MVS: Decide on ReferenceIdentifier
        //TODO:Add check if the referenceValue is a valid reference according to the reference formulation
        ReferenceIdentifier referenceValue = extractReferenceIdentifier(r2rmlMappingGraph, subjectMap);
        //AD: The values of the rr:class property must be IRIs. 
        //AD: Would that mean that it can not be a reference to an extract of the input or a template?
        Set<URI> classIRIs = extractURIsFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CLASS);
        
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, subjectMap, R2RMLTerm.GRAPH_MAP);
       
        if (graphMapValues != null) {
            graphMaps = extractGraphMapValues(r2rmlMappingGraph, graphMapValues, savedGraphMaps);
            log.info("[RMLMappingFactory] graph Maps returned " + graphMaps);
        }
        /*Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, subjectMap, R2RMLTerm.GRAPH_MAP);
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        if (graphMapValues != null) {
            for (Value graphMap : graphMapValues) {
                // Create associated graphMap if it has not already created
                boolean found = false;
                GraphMap graphMapFound = null;
                /*
                 * for (GraphMap savedGraphMap : savedGraphMaps) if
                 * (savedGraphMap.getGraph().equals(graphMap)) { found = true;
                 * graphMapFound = savedGraphMap; }
                 */
        /*        if (found) {
                    graphMaps.add(graphMapFound);
                } else {
                    GraphMap newGraphMap = extractGraphMap(r2rmlMappingGraph,
                            (Resource) graphMap);
                    savedGraphMaps.add(newGraphMap);
                    graphMaps.add(newGraphMap);
                }
            }
        }*/
        SubjectMap result = new StdSubjectMap(ownTriplesMap, constantValue,
                stringTemplate, termType, inverseExpression, referenceValue,
                classIRIs, graphMaps);
        log.debug("[RMLMappingFactory:extractSubjectMap] Subject map extracted.");
        return result;
    }
    
    
    private static Set<GraphMap> extractGraphMapValues(RMLSesameDataSet r2rmlMappingGraph, Set<Value> graphMapValues, Set<GraphMap> savedGraphMaps) throws InvalidR2RMLStructureException {
        
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        
            for (Value graphMap : graphMapValues) {
                // Create associated graphMap if it has not already created
                boolean found = false;
                GraphMap graphMapFound = null;
                /*
                 * for (GraphMap savedGraphMap : savedGraphMaps) if
                 * (savedGraphMap.getGraph().equals(graphMap)) { found = true;
                 * graphMapFound = savedGraphMap; }
                 */
                if (found) {
                    graphMaps.add(graphMapFound);
                } else {
                    GraphMap newGraphMap = null;
                    try {
                        newGraphMap = extractGraphMap(r2rmlMappingGraph, (Resource) graphMap);
                    } catch (R2RMLDataError ex) {
                        Logger.getLogger(RMLMappingFactory.class.getName()).log(Level.ERROR, null, ex);
                    } catch (InvalidR2RMLSyntaxException ex) {
                        Logger.getLogger(RMLMappingFactory.class.getName()).log(Level.ERROR, null, ex);
                    }

                    savedGraphMaps.add(newGraphMap);
                    graphMaps.add(newGraphMap);
                }
            }
        
        return graphMaps;
    }
    

    /*
     * Still needs to be modified!!
     */
    private static GraphMap extractGraphMap(RMLSesameDataSet r2rmlMappingGraph,
            Resource graphMap) throws InvalidR2RMLStructureException,
            R2RMLDataError, InvalidR2RMLSyntaxException {
        log.debug("[RMLMappingFactory:extractPredicateObjectMaps] Extract graph map...");

        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.TEMPLATE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.INVERSE_EXPRESSION);

        //MVS: Decide on ReferenceIdentifier
        ReferenceIdentifier referenceValue = extractReferenceIdentifier(r2rmlMappingGraph, graphMap);

        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.TERM_TYPE);

        GraphMap result = new StdGraphMap(constantValue, stringTemplate,
                inverseExpression, referenceValue, termType);
        log.debug("[RMLMappingFactory:extractPredicateObjectMaps] Graph map extracted.");
        return result;
    }

    /**
     * Extract content literal from a term type resource.
     *
     * @param r2rmlMappingGraph
     * @param termType
     * @param term
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static String extractLiteralFromTermMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource termType, Enum term)
            throws InvalidR2RMLStructureException {

        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        String result = statements.get(0).getObject().stringValue();
        if (log.isDebugEnabled()) {
            log.debug("[RMLMappingFactory:extractLiteralFromTermMap] Extracted "
                    + term + " : " + result);
        }
        return result;
    }

    /**
     * Extract content value from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
        private static Value extractValueFromTermMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource termType,
            Enum term)
            throws InvalidR2RMLStructureException {

        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        Value result = statements.get(0).getObject();
        log.debug("[RMLMappingFactory:extractValueFromTermMap] Extracted "
                + term + " : " + result.stringValue());
        return result;
    }

    /**
     * Extract content values from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Set<Value> extractValuesFromResource(
            RMLSesameDataSet r2rmlMappingGraph,
            Resource termType,
            Enum term)
            throws InvalidR2RMLStructureException {
        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        Set<Value> values = new HashSet<Value>();
        for (Statement statement : statements) {
            Value value = statement.getObject();
            log.debug("[RMLMappingFactory:extractURIsFromTermMap] Extracted "
                    + term + " : " + value.stringValue());
            values.add(value);
        }
        return values;
    }

    /**
     * Extract content URIs from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Set<URI> extractURIsFromTermMap(
            RMLSesameDataSet r2rmlMappingGraph, Resource termType,
            R2RMLTerm term)
            throws InvalidR2RMLStructureException {
        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        Set<URI> uris = new HashSet<URI>();
        for (Statement statement : statements) {
            URI uri = (URI) statement.getObject();
            log.debug("[RMLMappingFactory:extractURIsFromTermMap] Extracted "
                    + term + " : " + uri.stringValue());
            uris.add(uri);
        }
        return uris;
    }

    private static URI getTermURI(RMLSesameDataSet r2rmlMappingGraph, Enum term) throws InvalidR2RMLStructureException {
        String namespace = RMLVocabulary.R2RML_NAMESPACE;

        if (term instanceof RMLVocabulary.RMLTerm) {
            namespace = RMLVocabulary.RML_NAMESPACE;
        } else if (!(term instanceof R2RMLTerm)) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractValueFromTermMap] " + term + " is not valid.");
        }

        return r2rmlMappingGraph
                .URIref(namespace + term);
    }

    /**
     * Extract logicalSource contents.
     *
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @return
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static LogicalSource extractLogicalSource(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject) {
        // Extract logical table blank node
        // favor logical table over source
        List<Statement> table = getStatements(
                rmlMappingGraph, triplesMapSubject, 
                RMLVocabulary.R2RML_NAMESPACE, RMLVocabulary.R2RMLTerm.LOGICAL_TABLE);
        
        if (!table.isEmpty()) {
            extractLogicalTable();
        }
        
        List<Statement> logicalSourceStatements = getStatements(
                rmlMappingGraph, triplesMapSubject,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.LOGICAL_SOURCE);

        Resource blankLogicalSource = null;
        
        checkLogicalSource(triplesMapSubject, logicalSourceStatements);
        
        if (!logicalSourceStatements.isEmpty())
            blankLogicalSource = (Resource) logicalSourceStatements.get(0).getObject();
            //TODO:Check if I need to add another control here

        RMLVocabulary.QLTerm referenceFormulation =
                getReferenceFormulation(rmlMappingGraph, triplesMapSubject, blankLogicalSource);

        //Extract the iterator to create the iterator. Some formats have null, like CSV or SQL
        List<Statement> iterators = getStatements(
                rmlMappingGraph, blankLogicalSource,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.ITERATOR);

        checkIterator(triplesMapSubject, iterators, referenceFormulation);

        List<Statement> sourceStatements = getStatements(
                rmlMappingGraph,blankLogicalSource,
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.SOURCE);
        
        checkSource(triplesMapSubject, sourceStatements);

        LogicalSource logicalSource = null;

        if (!sourceStatements.isEmpty()) {
            //Extract the file identifier
            String file = sourceStatements.get(0).getObject().stringValue();
            
            if(!iterators.isEmpty())
                logicalSource = new StdLogicalSource(
                        iterators.get(0).getObject().stringValue(), 
                        file, referenceFormulation);
        }
        log.debug("[RMLMappingFactory:extractLogicalSource] Logical source extracted : "
                + logicalSource);
        return logicalSource;
    }
    
    private static List<Statement> getStatements(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, 
            String namespace, RMLVocabulary.Term term){
        URI logicalSource = rmlMappingGraph.URIref(namespace
                + term);

        List<Statement> source = rmlMappingGraph.tuplePattern(
                triplesMapSubject, logicalSource, null);
        
        //TODO: normally that shouldn't be error
        if (source.size() > 1) {
                log.error(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + triplesMapSubject.stringValue()
                        + " has too many logical source name defined.");
            }
        
        //check if the source exists
        
        return source;
    }
       
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
    
    private static void checkLogicalSource(
            Resource triplesMapSubject, List<Statement> statements){
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

    private static RMLVocabulary.QLTerm getReferenceFormulation(
            RMLSesameDataSet rmlMappingGraph, Resource triplesMapSubject, Resource subject) 
    {       
        List<Statement> statements = getStatements(
                rmlMappingGraph, subject, 
                RMLVocabulary.RML_NAMESPACE, RMLVocabulary.RMLTerm.REFERENCE_FORMULATION);
        
        checkReferenceFormulation(triplesMapSubject, statements);
        
        if (statements.isEmpty()) 
            return null;
        else
            return RMLVocabulary.getQLTerms(statements.get(0).getObject().stringValue());
    }
    
    private static void checkReferenceFormulation(
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
    
    private static void checkSource(
            Resource triplesMapSubject, List<Statement> statements) {
        if (statements.isEmpty()) {
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + triplesMapSubject.stringValue()
                    + " has no source for the Logicla Source.");
        }
    }
    
    private static void checkIterator(
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
    
    private static void checkTripleMapResources(List<Statement> statements){
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
    
    private static void checkStatements(List<Statement> statements, URI term){
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
    
    private static void checkTermMap(
            Value constantValue, String stringTemplate, 
            ReferenceIdentifier referenceValue, String resource){
        if(constantValue != null && stringTemplate != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + "contains a Term Map that should have "
                    + " both constant and template.");
        else if(constantValue != null && referenceValue != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + " contains a Term Map that should have "
                    + "both constant and reference.");
        else if(stringTemplate != null && referenceValue != null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource
                    + " contains a Term Map that should have "
                    + "both constant and reference.");
        else if(stringTemplate == null && referenceValue == null && constantValue == null)
            log.error(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + resource.toString()
                    + " contains a Term Map that should have "
                    + "a constant, a string template or a reference.");
                
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
