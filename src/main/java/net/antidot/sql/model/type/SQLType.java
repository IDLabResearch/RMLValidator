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
* SQL generic types
*
* Represents SQL types in formalisn defined in :
* SQL. ISO/IEC 9075-1:2008 SQL – Part 1: Framework (SQL/Framework) International Organization for Standardization, 27 January 2009.
* SQLFN. ISO/IEC 9075-2:2008 SQL – Part 2: Foundation (SQL/Foundation) International Organization for Standardization, 27 January 2009.
*
*/
package net.antidot.sql.model.type;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum SQLType {
	BINARY(Types.BINARY),
	BINARY_VARYING(Types.VARBINARY),
	BINARY_LARGE_OBJECT(Types.LONGVARBINARY),
	BIT(Types.BIT),
	NUMERIC(Types.NUMERIC),
	DECIMAL(Types.DECIMAL), 
	SMALLINT(Types.SMALLINT),
	INTEGER(Types.INTEGER),
	BIGINT(Types.BIGINT),
	FLOAT(Types.FLOAT),
	REAL(Types.REAL), 
	DOUBLE_PRECISION(Types.DOUBLE), 
	BOOLEAN(Types.BOOLEAN), 	
	DATE(Types.DATE),	
	TIME(Types.TIME),
	TIMESTAMP(Types.TIMESTAMP),
	UNKNOWN(Types.OTHER),
	CHAR(Types.CHAR),
	VARCHAR(Types.VARCHAR),
	STRING(Types.LONGVARCHAR),
	TINYINT(Types.TINYINT),
	
	
	// Unsupported
	BLOB(Types.BLOB);
	
	// Log
	private static Log log = LogFactory
			.getLog(SQLType.class);
	
	private int javaSQLType;

	private SQLType(int javaSQLType) {
		this.javaSQLType = javaSQLType;
		
	}
	
	public int getID(){
		return javaSQLType;
	}
	
	private static Map<SQLType, String> sqlCastQueries = new HashMap<SQLType, String>();
	
	static {
		sqlCastQueries.put(SQLType.NUMERIC, "CAST(value AS CHAR(18))");
		sqlCastQueries.put(SQLType.DECIMAL, "CAST(value AS CHAR(18))");
		sqlCastQueries.put(SQLType.SMALLINT, "CAST(value AS CHAR(18))");
		sqlCastQueries.put(SQLType.INTEGER, "CAST(value AS CHAR(18))");
		sqlCastQueries.put(SQLType.BIGINT, "CAST(value AS CHAR(18))");
		sqlCastQueries.put(SQLType.FLOAT, "CAST(value AS CHAR(23))");
		sqlCastQueries.put(SQLType.REAL, "CAST(value AS CHAR(23))");
		sqlCastQueries.put(SQLType.DOUBLE_PRECISION, "CAST(value AS CHAR(23))");
		sqlCastQueries.put(SQLType.BOOLEAN, "IF (value, 'true', 'false')");
		sqlCastQueries.put(SQLType.DATE, "CAST(value AS CHAR(13))");
		sqlCastQueries.put(SQLType.TIME, "CAST(value AS CHAR(23))");
		sqlCastQueries.put(SQLType.TIMESTAMP, "REPLACE(CAST(value AS CHAR(37)), ' ', 'T')");
	}

	public static ArrayList<SQLType> getDateTypes() {
		ArrayList<SQLType> result = new ArrayList<SQLType>();
		result.add(DATE);
		result.add(TIMESTAMP);
		return result;
	}

	public static ArrayList<SQLType> getBlobTypes() {
		ArrayList<SQLType> result = new ArrayList<SQLType>();
		result.add(BLOB);
		return result;
	}
	
	public static ArrayList<SQLType> getBinaryTypes() {
		ArrayList<SQLType> result = new ArrayList<SQLType>();
		result.add(BINARY);
		result.add(BINARY_VARYING);
		result.add(SQLType.BINARY_LARGE_OBJECT);
		return result;
	}
	
	public static ArrayList<SQLType> getStringTypes() {
		ArrayList<SQLType> result = new ArrayList<SQLType>();
		result.add(CHAR);
		result.add(VARCHAR);
		result.add(STRING);
		return result;
	}

	public boolean isDateType() {
		return getDateTypes().contains(this);
	}

	public boolean isBlobType() {
		return getBlobTypes().contains(this);
	}
	
	public boolean isBinaryType() {
		return getBlobTypes().contains(this);
	}
	
	public boolean isStringType() {
		return getStringTypes().contains(this);
	}
	
	public boolean isCastable(){
		return sqlCastQueries.keySet().contains(this);
	}

	/**
	 * Converts a specific SQL Type from its display name.
	 * 
	 * @param displayName
	 * @return
	 */
	public static SQLType toSQLType(int id) {
		for (SQLType sqlType : SQLType.values()) {
			if (sqlType.getID() == id)
				return sqlType;
		}
		log.warn("[SQLType:getSQLCastQuery] Unkonwm SQL id : " + id);
		return UNKNOWN;
	}
	
	/**
	 * Return the SQL query use for convert datatype directly from database.
	 * @param type
	 * @param value
	 * @return
	 * @deprecated
	 */
	public static String getSQLCastQuery(SQLType type, String value){
		if (type.isBinaryType()){
			log.warn("[SQLType:getSQLCastQuery] Binary types unsupported for this time. " +
					"There will be considered like string object.");
		}
		if (!type.isCastable()){
			// No necessery parsing
			return value;
		} else {
			return sqlCastQueries.get(type).replaceFirst("value", value);
		}
	}

}
