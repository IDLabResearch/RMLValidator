package be.ugent.mmlab.rml.tools;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;



/**
 * *************************************************************************
 *
 * RML - Validator : RMLValidatorResult
 *
 *
 * @author mielvandersande, andimou
 *
 ***************************************************************************
 */
public class CustomRDFDataValidator {

    /**
     * @param string
     * @return
     */
    public static boolean isValidDatatype(String datatype) {
        boolean isValid = true;
        if (!isValidURI(datatype)) {
            return false;
        }

        return isValid;
    }

    public static boolean isValidURI(String strURI) {
        ValueFactory vf = new ValueFactoryImpl();
        boolean isValid = false;
        if (strURI == null) {
            return false;
        }
        try {
            // All cases are not take into account... use openRDF library
            vf.createURI(strURI);
            // @todo : check if this rule is not too strict
            if (!strURI.contains(" ")) {
                isValid = true;
            }
        } catch (Exception e) {
            // Nothing
        }
        return isValid;
    }
}
