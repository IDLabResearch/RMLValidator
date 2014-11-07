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

/**
 *
 * RDB2RDF Commons : SQLToRDF Toolkit
 *
 * Collection of useful tool-methods used for conversion between SQL and RDF. 
 *
 */
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;
import java.util.Map;

import net.antidot.sql.model.type.SQLSpecificType;
import net.antidot.sql.model.type.SQLType;
import net.antidot.semantic.xmls.xsd.XSDType;

public abstract class SpecificSQLToXMLS {

	/**
	 * Equivalence datatype between standard SQL types and XSD types according
	 * to XML Schema Part 2: Datatypes Second Edition (W3C Recommendation 28
	 * October 2004) See : http://www.w3.org/TR/xmlschema-2/
	 */
	private static Map<SQLSpecificType, XSDType> equivalentSpecificTypes = new HashMap<SQLSpecificType, XSDType>();
	private static Map<SQLType, XSDType> equivalentTypes = new HashMap<SQLType, XSDType>();

	static {
		// Text types
		// A fixed section from 0 to 255 characters long.
		equivalentSpecificTypes.put(SQLSpecificType.CHAR, XSDType.STRING);
		// A variable section from 0 to 255 characters long.
		equivalentSpecificTypes.put(SQLSpecificType.VARCHAR, XSDType.STRING);
		// A string with a maximum length of 255 characters.
		equivalentSpecificTypes.put(SQLSpecificType.TINYTEXT, XSDType.STRING);
		 // A string with a maximum length of 65535 characters.
		equivalentSpecificTypes.put(SQLSpecificType.TEXT, XSDType.STRING);
		// A string with a maximum length of 65535 characters.
		equivalentSpecificTypes.put(SQLSpecificType.BLOB, XSDType.STRING); 
		// A string with a maximum length of 16777215 characters.
		equivalentSpecificTypes.put(SQLSpecificType.MEDIUMTEXT, XSDType.STRING); 
		// A string with a maximum length of 16777215 characters.
		equivalentSpecificTypes.put(SQLSpecificType.MEDIUMBLOB, XSDType.STRING); 
		// A string with a maximum length of 4294967295 characters.
		equivalentSpecificTypes.put(SQLSpecificType.LONGTEXT, XSDType.STRING); 
		// A string with a maximum length of 4294967295 characters.
		equivalentSpecificTypes.put(SQLSpecificType.LONGBLOB, XSDType.STRING); 
		
		// Number types
		// 0 or 1
		equivalentSpecificTypes.put(SQLSpecificType.BIT, XSDType.BYTE); 
		// -128 to 127 normal
		equivalentSpecificTypes.put(SQLSpecificType.TINYINT, XSDType.BYTE); 
		// 0 to 255 UNSIGNED.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_TINYINT, XSDType.UNSIGNED_BYTE);
		// -32768 to 32767 normal
		equivalentSpecificTypes.put(SQLSpecificType.SMALLINT, XSDType.SHORT);
		// 0 to 65535 UNSIGNED.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_SMALLINT, XSDType.UNSIGNED_SHORT); 
		// -8388608 to 8388607 normal.
		equivalentSpecificTypes.put(SQLSpecificType.MEDIUMINT, XSDType.INT); 
		// 0 to 16777215 UNSIGNED.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_MEDIUMINT, XSDType.INT); 
		// -2147483648 to 2147483647 normal.
		equivalentSpecificTypes.put(SQLSpecificType.INT, XSDType.INT);
		// 0 to 4294967295 UNSIGNED.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_INT, XSDType.UNSIGNED_INT); 
		// -9223372036854775808 to 9223372036854775807 normal.
		equivalentSpecificTypes.put(SQLSpecificType.BIGINT, XSDType.LONG); 
		 // 0 to 18446744073709551615 UNSIGNED.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_BIGINT, XSDType.UNSIGNED_LONG);
		// A small number with a floating decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.FLOAT, XSDType.FLOAT);
		// A small number with a floating decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_FLOAT, XSDType.FLOAT);
		// A small number with a floating decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.DOUBLE, XSDType.DOUBLE); 
		// A large number with a floating decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_DOUBLE, XSDType.DOUBLE); 
		// A large number with a floating decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.DECIMAL, XSDType.DECIMAL); 
		// A DOUBLE stored as a string , allowing for a fixed decimal point.
		equivalentSpecificTypes.put(SQLSpecificType.UNSIGNED_DECIMAL, XSDType.DECIMAL);
		
		// Date types
		 // YYYY-MM-DD ("1000-01-01" - "9999-12-31").
		equivalentSpecificTypes.put(SQLSpecificType.DATE, XSDType.DATE);
		// YYYY-MM-DD HH:MM:SS ("1000-01-01 00:00:00" - "9999-12-31 23:59:59"). 
		// xsd:datetime doesn't match because the letter T is required ?
		// No, it's valid. See W3C Working Draft (24/03/2011), Section 2.3.4.
		equivalentSpecificTypes.put(SQLSpecificType.DATETIME, XSDType.DATETIME); 
		// YYYYMMDDHHMMSS (19700101000000 - 2037+)
		equivalentSpecificTypes.put(SQLSpecificType.TIMESTAMP, XSDType.DATETIME); 
		// HH:MM:SS ("-838:59:59" - "838:59:59").
		equivalentSpecificTypes.put(SQLSpecificType.TIME, XSDType.TIME); 
		// YYYY (1900 - 2155).
		equivalentSpecificTypes.put(SQLSpecificType.YEAR, XSDType.GYEAR); 
		
		// Misc types
		equivalentSpecificTypes.put(SQLSpecificType.ENUM, XSDType.ENUMERATION); // Short for ENUMERATION which means that each column may have one of a specified possible values.
		equivalentSpecificTypes.put(SQLSpecificType.SET, XSDType.ENUMERATION); // Similar to ENUM except each column may have more than one of the specified possible values.

		/**
		 * PostGreSQL equivalences.
		 */
		equivalentSpecificTypes.put(SQLSpecificType.INT4, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.FLOAT4, XSDType.FLOAT);
		equivalentSpecificTypes.put(SQLSpecificType.POINT, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.BIGSERIAL, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.VARBIT, XSDType.INT);
		equivalentSpecificTypes.put(SQLSpecificType.BIT_VARYING, XSDType.INT);
		equivalentSpecificTypes.put(SQLSpecificType.BOOL, XSDType.BYTE);
		equivalentSpecificTypes.put(SQLSpecificType.BPCHAR, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.BOOLEAN, XSDType.BYTE);
		equivalentSpecificTypes.put(SQLSpecificType.BOX, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.BYTEA, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.CHARACTER_VARYING, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.CHARACTER, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.CIDR, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.CIRCLE, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.DOUBLE_PRECISION, XSDType.DOUBLE);
		equivalentSpecificTypes.put(SQLSpecificType.FLOAT8, XSDType.FLOAT);
		equivalentSpecificTypes.put(SQLSpecificType.INET, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.INT2, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.INT8, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.INTERVAL, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.LINE, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.LSEG, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.MACADDR, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.MONEY, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.NUMERIC, XSDType.DECIMAL);
		equivalentSpecificTypes.put(SQLSpecificType.PATH, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.POINT, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.POLYGON, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.REAL, XSDType.FLOAT);
		equivalentSpecificTypes.put(SQLSpecificType.SERIAL, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.SERIAL4, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.TIMETZ, XSDType.FLOAT);
		equivalentSpecificTypes.put(SQLSpecificType.TIMESTAMPTZ, XSDType.STRING);
		equivalentSpecificTypes.put(SQLSpecificType.POLYGON, XSDType.INTEGER);
		equivalentSpecificTypes.put(SQLSpecificType.REAL, XSDType.FLOAT);
		equivalentSpecificTypes.put(SQLSpecificType.SERIAL, XSDType.STRING);
		
		// Unkown type
		equivalentSpecificTypes.put(SQLSpecificType.UNKNOW, XSDType.STRING);

		/**
		 * Generic equivalences.
		 */
		equivalentTypes.put(SQLType.BINARY, XSDType.HEXBINARY);
		equivalentTypes.put(SQLType.BINARY_VARYING, XSDType.HEXBINARY);
		equivalentTypes.put(SQLType.BINARY_LARGE_OBJECT, XSDType.HEXBINARY);
		equivalentTypes.put(SQLType.NUMERIC, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.DECIMAL, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.SMALLINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.INTEGER, XSDType.INTEGER);
		equivalentTypes.put(SQLType.BIGINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.FLOAT, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.REAL, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.DOUBLE_PRECISION, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.BOOLEAN, XSDType.BOOLEAN);
		equivalentTypes.put(SQLType.DATE, XSDType.DATE);
		equivalentTypes.put(SQLType.TIME, XSDType.TIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
		equivalentTypes.put(SQLType.CHAR, XSDType.STRING);
		equivalentTypes.put(SQLType.VARCHAR, XSDType.STRING);
		equivalentTypes.put(SQLType.STRING, XSDType.STRING);
		equivalentTypes.put(SQLType.UNKNOWN, XSDType.STRING);
		
		
	}

	public static XSDType getEquivalentSpecificType(SQLSpecificType SQLType) {
		return equivalentSpecificTypes.get(SQLType);
	}

	public static XSDType getEquivalentSpecificType(String sqlType) {
		return equivalentSpecificTypes.get(SQLSpecificType.toSQLType(sqlType));
	}

	public static boolean isValidSQLSpecificDatatype(String datatype) {
		return equivalentSpecificTypes.keySet().contains(SQLSpecificType.toSQLType(datatype));
	}
	
	public static XSDType getEquivalentType(SQLType SQLType) {
		return equivalentTypes.get(SQLType);
	}

	public static XSDType getEquivalentType(int sqlType) {
		return equivalentTypes.get(SQLType.toSQLType(sqlType));
	}

	public static boolean isValidSQLDatatype(int datatype) {
		return equivalentTypes.keySet().contains(SQLType.toSQLType(datatype));
	}

}
