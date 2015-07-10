package be.ugent.mmlab.rml.extractor;

import be.ugent.mmlab.rml.extraction.RMLMappingExtractor;
import be.ugent.mmlab.rml.extraction.RMLUnValidatedMappingExtractor;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.rmlvalidator.RMLValidator;
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
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Validator : RMLValidatedMappingExtractor
 * 
 * @author andimou
 *
 */
public class RMLValidatedMappingExtractor extends RMLUnValidatedMappingExtractor
    implements RMLMappingExtractor {
    
    RMLMappingValidator validator;
    RMLUnValidatedMappingExtractor subextractor;

    // Log
    static final Logger log = LoggerFactory.getLogger(RMLValidatedMappingExtractor.class);   

    public RMLValidatedMappingExtractor(RMLMappingValidator validator) {
        this.validator = validator;
        this.subextractor = new RMLUnValidatedMappingExtractor();
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

    //TODO:Same as UnValidated
    @Override
    public Map<Resource, TriplesMap> extractTriplesMapResources(
            RMLSesameDataSet rmlMappingGraph) {
        Map<Resource, TriplesMap> triplesMapResources = new HashMap<Resource, TriplesMap>();
        
        List<Statement> statements = getTriplesMapResources(rmlMappingGraph);

        triplesMapResources = putTriplesMapResources(statements, triplesMapResources);
        
        return triplesMapResources;
    }
}
