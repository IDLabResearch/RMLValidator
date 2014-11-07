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
 * SQL specific types
 *
 * Represents SQL types in different formalism (MySQL or PostGreSQL for instance).
 *
 */
package net.antidot.sql.model.type;

import java.util.ArrayList;

public enum SQLSpecificType {
	
	/**
	 * Unkonw type.
	 */
	UNKNOW("UNKNOW"),

	/**
	 * MySQL Types and their display name. References :
	 * http://www.htmlite.com/mysql003.php
	 * http://dev.mysql.com/doc/refman/5.0/en/numeric-types.html
	 */

	CHAR("CHAR"), // A fixed section from 0 to 255 characters long.
	VARCHAR("VARCHAR"), // A variable section from 0 to 255 characters long.
	TINYTEXT("TINYTEXT"), // A string with a maximum length of 255
	// characters.
	TINYBLOB("TINYBLOB"),
	TEXT("TEXT"), // A string with a maximum length of 65535 characters.
	BLOB("BLOB"), // A string with a maximum length of 65535 characters.
	MEDIUMTEXT("MEDIUMTEXT"), // A string with a maximum length of 16777215
	MEDIUMBLOB("MEDIUMBLOB"), // A string with a maximum length of 16777215
	// characters.
	LONGTEXT("LONGTEXT"), // A string with a maximum length of 4294967295
	// characters.
	LONGBLOB("LONGBLOB"), // A string with a maximum length of 4294967295
	// characters.
	BIT("BIT"), // 0 or 1, equivalent to TINYINT(1) (since MySQL 5.0.3)
	TINYINT("TINYINT"), // -128 to 127 normal
	UNSIGNED_TINYINT("TINYINT UNSIGNED"), // 0 to 255 UNSIGNED.
	SMALLINT("SMALLINT"), // -32768 to 32767 normal
	UNSIGNED_SMALLINT("SMALLINT UNSIGNED"), // 0 to 65535 UNSIGNED.
	MEDIUMINT("MEDIUMINT"), // -8388608 to 8388607 normal.
	UNSIGNED_MEDIUMINT("MEDIUMINT UNSIGNED"), // 0 to 16777215 UNSIGNED.
	INT("INT"), // -2147483648 to 2147483647 normal.
	UNSIGNED_INT("INT UNSIGNED"), // 0 to 4294967295 UNSIGNED.
	BIGINT("BIGINT"), // -9223372036854775808 to 9223372036854775807 normal.
	UNSIGNED_BIGINT("BIGINT UNSIGNED"), // 0 to 18446744073709551615
	// UNSIGNED.
	FLOAT("FLOAT"), // A small number with a floating decimal point.
	UNSIGNED_FLOAT("FLOAT UNSIGNED"), // A small positive number with a
	// floating decimal point.
	DOUBLE("DOUBLE"), // A large number with a floating decimal point.
	UNSIGNED_DOUBLE("DOUBLE UNSIGNED"), // A large positive number with a
	// floating decimal point.
	DECIMAL("DECIMAL"), // A DOUBLE stored as a string , allowing for a
	// fixed decimal
	UNSIGNED_DECIMAL("DECIMAL UNSIGNED"), // A positive DOUBLE stored as a
	// string , allowing for a
	// fixed decimal
	// point.
	DATE("DATE"), // YYYY-MM-DD ("1000-01-01" - "9999-12-31").
	DATETIME("DATETIME"), // YYYY-MM-DD HH:MM:SS ("1000-01-01 00:00:00" -
	// "9999-12-31 23:59:59").
	TIMESTAMP("TIMESTAMP"), // YYYYMMDDHHMMSS (19700101000000 - 2037+).
	TIME("TIME"), // HH:MM:SS ("-838:59:59" -"838:59:59").
	YEAR("YEAR"), // YYYY (1900 - 2155).
	SET("SET"), // // Similar to ENUM except each column may have more than
	// one of
	// the specified possible values.
	ENUM("ENUM"),
	// Short for ENUMERATION which means that each column may have one of a
	// specified possible values.
	
	/**
	 * PostGreSQL Types and their display name. References :
	 * http://docs.postgresql.fr/8.1/datatype.html
	 */
	BIGSERIAL("BIGSERIAL"),
	BPCHAR("BPCHAR"),
	SERIAL8("SERIAL8"),
	VARBIT("VARBIT"),
	BIT_VARYING("BIT VARYING"),
	BOOL("BOOL"),
	BOOLEAN("BOOLEAN"),
	BOX("BOX"),
	BYTEA("BYTEA"),
	CHARACTER_VARYING("CHARACTER VARYING"),
	CHARACTER("CHARACTER"),
	CIDR("CIDR"),
	CIRCLE("CIRCLE"),
	DOUBLE_PRECISION("DOUBLE PRECISION"),
	FLOAT4("FLOAT4"), 
	FLOAT8("FLOAT8"), 
	INET("INET"),
	INT2("INT2"),
	INT4("INT4"),
	INT8("INT8"),
	INTERVAL("INTERVAL"),
	LINE("LINE"),
	LSEG("LSEG"),
	MACADDR("MACADDR"),
	MONEY("MONEY"),
	NUMERIC("NUMERIC"), 
	PATH("PATH"), 
	POINT("POINT"),
	POLYGON("POLYGON"),
	REAL("REAL"),
	SERIAL("SERIAL"),
	SERIAL4("SERIAL4"),
	TIMETZ("TIMETZ"),
	TIMESTAMPTZ("TIMESTAMPTZ");
	
	public static ArrayList<SQLSpecificType> postGreSQLTypes = new ArrayList<SQLSpecificType>();
	static {
		postGreSQLTypes.add(VARCHAR);
		postGreSQLTypes.add(TEXT);
		postGreSQLTypes.add(BIT);
		postGreSQLTypes.add(SMALLINT);
		postGreSQLTypes.add(INT);
		postGreSQLTypes.add(BIGINT);
		postGreSQLTypes.add(DECIMAL);
		postGreSQLTypes.add(DATE);
		postGreSQLTypes.add(TIMESTAMP);
		postGreSQLTypes.add(TIME);
		postGreSQLTypes.add(BIGSERIAL);
		postGreSQLTypes.add(SERIAL8);
		postGreSQLTypes.add(VARBIT);
		postGreSQLTypes.add(BIT_VARYING);
		postGreSQLTypes.add(BOOL);
		postGreSQLTypes.add(BOX);
		postGreSQLTypes.add(BOOLEAN);
		postGreSQLTypes.add(BYTEA);
		postGreSQLTypes.add(CHARACTER_VARYING);
		postGreSQLTypes.add(CHARACTER);
		postGreSQLTypes.add(CIDR);
		postGreSQLTypes.add(CIRCLE);
		postGreSQLTypes.add(DOUBLE_PRECISION);
		postGreSQLTypes.add(FLOAT4);
		postGreSQLTypes.add(FLOAT8);
		postGreSQLTypes.add(INET);
		postGreSQLTypes.add(INT2);
		postGreSQLTypes.add(INT4);
		postGreSQLTypes.add(INT8);
		postGreSQLTypes.add(INTERVAL);
		postGreSQLTypes.add(LINE);
		postGreSQLTypes.add(LSEG);
		postGreSQLTypes.add(MACADDR);
		postGreSQLTypes.add(MONEY);
		postGreSQLTypes.add(NUMERIC);
		postGreSQLTypes.add(PATH);
		postGreSQLTypes.add(POINT);
		postGreSQLTypes.add(POLYGON);
		postGreSQLTypes.add(REAL);
		postGreSQLTypes.add(SERIAL);
		postGreSQLTypes.add(SERIAL4);
		postGreSQLTypes.add(TIMETZ);
		postGreSQLTypes.add(TIMESTAMPTZ);
		postGreSQLTypes.add(BPCHAR);
	}
	
	public static ArrayList<SQLSpecificType> mySQLTypes = new ArrayList<SQLSpecificType>();
	static {	mySQLTypes.add(CHAR);
		mySQLTypes.add(VARCHAR);
		mySQLTypes.add(TINYTEXT);
		mySQLTypes.add(TINYBLOB);
		mySQLTypes.add(TEXT);
		mySQLTypes.add(BLOB);
		mySQLTypes.add(MEDIUMTEXT);
		mySQLTypes.add(MEDIUMBLOB);
		mySQLTypes.add(LONGTEXT);
		mySQLTypes.add(LONGBLOB);
		mySQLTypes.add(BIT);
		mySQLTypes.add(TINYINT);
		mySQLTypes.add(UNSIGNED_TINYINT);
		mySQLTypes.add(SMALLINT);
		mySQLTypes.add(UNSIGNED_SMALLINT);
		mySQLTypes.add(MEDIUMINT);
		mySQLTypes.add(UNSIGNED_MEDIUMINT);
		mySQLTypes.add(INT);
		mySQLTypes.add(UNSIGNED_INT);
		mySQLTypes.add(BIGINT);
		mySQLTypes.add(UNSIGNED_BIGINT);
		mySQLTypes.add(FLOAT);
		mySQLTypes.add(UNSIGNED_FLOAT);
		mySQLTypes.add(DOUBLE);
		mySQLTypes.add(UNSIGNED_DOUBLE);
		mySQLTypes.add(DECIMAL);
		mySQLTypes.add(DATE);
		mySQLTypes.add(DATETIME);
		mySQLTypes.add(TIMESTAMP);
		mySQLTypes.add(TIME);
		mySQLTypes.add(YEAR);
		mySQLTypes.add(SET);
		mySQLTypes.add(ENUM);
	}
	
	private String displayName;

	private SQLSpecificType(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		return displayName;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	public static ArrayList<SQLSpecificType> getDateTypes() {
		ArrayList<SQLSpecificType> result = new ArrayList<SQLSpecificType>();
		result.add(DATE);
		result.add(DATETIME);
		result.add(TIMESTAMP);
		return result;
	}

	public static ArrayList<SQLSpecificType> getBlobTypes() {
		ArrayList<SQLSpecificType> result = new ArrayList<SQLSpecificType>();
		result.add(TINYBLOB);
		result.add(MEDIUMBLOB);
		result.add(LONGBLOB);
		result.add(BLOB);
		return result;
	}

	public boolean isDateType() {
		return getDateTypes().contains(this);
	}

	public boolean isBlobType() {
		return getBlobTypes().contains(this);
	}

	/**
	 * Converts a specific SQL Type from its display name.
	 * 
	 * @param displayName
	 * @return
	 */
	public static SQLSpecificType toSQLType(String displayName) {
		for (SQLSpecificType sqlType : SQLSpecificType.values()) {
			if (sqlType.getDisplayName().equals(displayName)
					|| sqlType.getDisplayName().toLowerCase().equals(
							displayName))
				return sqlType;
		}
		return UNKNOW;
	}

}
