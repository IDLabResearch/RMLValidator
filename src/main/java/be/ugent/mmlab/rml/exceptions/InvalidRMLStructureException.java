/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.exceptions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class InvalidRMLStructureException extends Exception {
    
    // Log
    private static final Logger log = LogManager.getLogger(InvalidRMLStructureException.class);  

    public InvalidRMLStructureException(String string) {
        log.error(string);
    }
    
}
