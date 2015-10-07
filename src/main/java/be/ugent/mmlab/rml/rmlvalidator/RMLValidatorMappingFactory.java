package be.ugent.mmlab.rml.rmlvalidator;

import be.ugent.mmlab.rml.mapdochandler.extraction.RMLMappingExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.RMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingExtractor;
import be.ugent.mmlab.rml.model.RMLMapping;
import org.openrdf.repository.Repository;
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
    static final Logger log = 
            LoggerFactory.getLogger(RMLValidatorMappingFactory.class);
    
    private RMLMappingExtractor extractor;
    //private RMLMappingValidator validator;

    /**
     *
     */
    public RMLValidatorMappingFactory(){
    }
    
    //extraction and validation
    /**
     *
     * @param repository
     * @param validate
     */
    public RMLValidatorMappingFactory(Repository repository, boolean validate){
        setRMLMappingFactory(repository, validate);
    }
   
    /**
     *
     * @param repository
     * @param validate
     */
    public void setRMLMappingFactory(Repository repository, boolean validate){

        if(validate){
            this.extractor = new StdRMLMappingExtractor(validate);
        }
        else
            this.extractor = new StdRMLMappingExtractor();
    } 

    @Override
    public RMLMapping extractRMLMapping(String fileToRMLFile) {
        throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
    }
  
    
}
