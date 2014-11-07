/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/***************************************************************************
 *
 * XMLS XSD datatype : Lexical transformation
 *
 * Lexical transformation used to convert SQL datatype to RDF datatype.
 *
 ****************************************************************************/
package net.antidot.semantic.xmls.xsd;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class XSDLexicalTransformation {

    public enum Transformation {
	HEX_ENCODING, NONE_REQUIRED, ENSURE_LOWERCASE, REPLACE_SPACE_CHARACTER, UNDEFINED
    }

    /**
     * The RDF transformation of a SQL datatype is a transformation rule given
     * in the table below.
     */
    private static Map<XSDType, Transformation> correspondingTransformation = new HashMap<XSDType, Transformation>();

    static {
	correspondingTransformation.put(XSDType.HEXBINARY,
		Transformation.HEX_ENCODING);
	correspondingTransformation.put(XSDType.DECIMAL,
		Transformation.NONE_REQUIRED);
	correspondingTransformation.put(XSDType.INTEGER,
		Transformation.NONE_REQUIRED);
	correspondingTransformation.put(XSDType.DOUBLE,
		Transformation.NONE_REQUIRED);
	correspondingTransformation.put(XSDType.BOOLEAN,
		Transformation.ENSURE_LOWERCASE);
	correspondingTransformation.put(XSDType.DATE,
		Transformation.NONE_REQUIRED);
	correspondingTransformation.put(XSDType.TIME,
		Transformation.NONE_REQUIRED);
	correspondingTransformation.put(XSDType.DATETIME,
		Transformation.REPLACE_SPACE_CHARACTER);
    }

    /**
     * Get the corresponding transformation or conversion to string if the SQL
     * datatype does not occur in the table.
     */
    public static Transformation getLexicalTransformation(XSDType xsdType) {
	Transformation t = correspondingTransformation.get(xsdType);
	if (t == null) {
	    // Any types not appearing in the table, including all character
	    // string
	    // types and vendor-specific types, will default to producing RDF
	    // plain
	    // literals by using conversion to string.
	    return Transformation.NONE_REQUIRED;
	}
	return t;
    }

    /**
     * Transform a given value to its form obtained by its corresponding lexical
     * transformation.
     * 
     * @param value
     * @param transformation
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String transform(byte[] value, Transformation transformation)
	    throws UnsupportedEncodingException {
	switch (transformation) {
	case HEX_ENCODING:
	    return hexEncoding(value);
	default:
	    return transformFromString(new String(value, "UTF-8"),
		    transformation);
	}
    }

    /**
     * Transform a given value to its form obtained by its corresponding lexical
     * transformation.
     * 
     * @param value
     * @param transformation
     * @return
     */
    public static String transformFromString(String value,
	    Transformation transformation) {
	switch (transformation) {
	case NONE_REQUIRED:
	    return new String(value);
	case UNDEFINED:
	    throw new IllegalArgumentException(
		    "[SQLLexicalTransformation:transform] Unkonw lexical transformation.");
	case ENSURE_LOWERCASE:
	    return ensureLowercase(new String(value));
	case REPLACE_SPACE_CHARACTER:
	    return replaceSpaceCharacter(new String(value));
	case HEX_ENCODING:
	    throw new IllegalArgumentException(
		    "[SQLLexicalTransformation:transform] Cannot hex encoding from string");
	default:
	    break;
	}
	return value;
    }

    private static String byteToString(byte b) {
	String byte_str = Integer.toHexString(b).toUpperCase();
	if(byte_str.length() == 2)
	{
	    return byte_str;
	}
	else if(byte_str.length() == 1)
	{
	    return "0"+byte_str;
	}
	else if(byte_str.length() > 2)
	{
	    return byte_str.substring(byte_str.length() - 2);
	}
	throw new IllegalArgumentException("Unable to convert byte to string: "+b);
    }

    private static String hexEncoding(byte[] value) {
	StringBuffer buffer = new StringBuffer();
	for (byte b : value) {
	    buffer.append(byteToString(b));
	}
	return buffer.toString();
    }

    private static String replaceSpaceCharacter(String value) {
	return value.replace(' ', 'T');
    }

    private static String ensureLowercase(String value) {
	return value.toLowerCase();
    }

    public static String extractNaturalRDFFormFrom(XSDType xsdType, byte[] value)
	    throws UnsupportedEncodingException {
	String result = new String("");
	if (xsdType != null) {
	    // 3. Otherwise, if dt is listed in the table below: The result
	    // is a
	    // typed literal whose datatype IRI is the IRI indicated in the
	    // RDF
	    // Lexical transformation
	    result = XSDLexicalTransformation.transform(value,
		    XSDLexicalTransformation.getLexicalTransformation(xsdType));
	    // Canonical lexical form
	    result = XSDLexicalForm.getCanonicalLexicalForm(result, xsdType);
	}
	return result;

    }

}
