package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.mapdochandler.extraction.RMLMappingExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.RMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.extraction.StdRMLMappingFactory;
import be.ugent.mmlab.rml.extractor.RMLValidatedMappingExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.StdRMLMappingExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * *************************************************************************
 *
 * RML - Validator : RMLMappingFactory
 *
 *
 * @author andimou
 *
 ***************************************************************************
 */
public final class RMLValidatorMappingFactory extends StdRMLMappingFactory implements RMLMappingFactory{
    
    // Log
    static final Logger log = LoggerFactory.getLogger(RMLValidatorMappingFactory.class);
    
    private RMLMappingExtractor extractor;
    private RMLMappingValidator validator;

    //extraction and validation
    public RMLValidatorMappingFactory(boolean validate){
        setRMLMappingFactory(validate);
    }
   
    public void setRMLMappingFactory(boolean validate){
        this.validator = new RMLValidator();

        if(validate){
            this.extractor = new RMLValidatedMappingExtractor(validator);
        }
        else
            this.extractor = new StdRMLMappingExtractor();
    } 
  
    
}
