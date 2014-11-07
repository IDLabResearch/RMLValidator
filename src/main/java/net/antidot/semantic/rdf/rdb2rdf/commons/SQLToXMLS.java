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
 * RDB2RDF Commons : SQLToXMLS Toolkit
 * 
 * Collection of useful tool-methods used for conversion between SQL datatype and XML Schema Datatypes.
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;
import java.util.Map;

import net.antidot.semantic.xmls.xsd.XSDType;
import net.antidot.sql.model.type.SQLType;

public abstract class SQLToXMLS {

	/**
	 * Equivalence datatype between standard SQL types and XSD types.
	 */
	private static Map<SQLType, XSDType> equivalentTypes = new HashMap<SQLType, XSDType>();
	
	static {
		equivalentTypes.put(SQLType.BINARY, XSDType.HEXBINARY);
		equivalentTypes.put(SQLType.BINARY_VARYING, XSDType.HEXBINARY);
		equivalentTypes.put(SQLType.NUMERIC, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.DECIMAL, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.SMALLINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.INTEGER, XSDType.INTEGER);
		equivalentTypes.put(SQLType.BIGINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.FLOAT, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.REAL, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.DOUBLE_PRECISION, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.BOOLEAN, XSDType.BOOLEAN);
		equivalentTypes.put(SQLType.BIT, XSDType.BOOLEAN);
		equivalentTypes.put(SQLType.TINYINT, XSDType.BOOLEAN);
		equivalentTypes.put(SQLType.DATE, XSDType.DATE);
		equivalentTypes.put(SQLType.TIME, XSDType.TIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
		equivalentTypes.put(SQLType.VARCHAR, XSDType.STRING);
		equivalentTypes.put(SQLType.CHAR, XSDType.STRING);
		equivalentTypes.put(SQLType.STRING, XSDType.STRING);
	}
	
	public static XSDType getEquivalentType(SQLType sqlType){
		return equivalentTypes.get(sqlType);
	}
	
	public static XSDType getEquivalentType(int sqlType){
		return equivalentTypes.get(SQLType.toSQLType(sqlType));
	}
	
	public static boolean isValidSQLDatatype(int datatype){
		return equivalentTypes.keySet().contains(SQLType.toSQLType(datatype));
	}

}
