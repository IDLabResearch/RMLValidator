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

package net.antidot.sql.model.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLProcessor;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import net.antidot.sql.model.core.DriverType;
import net.antidot.sql.model.type.SQLType;

/**
 * @author Laurent Mazuel
 * 
 */
public class ColumnIdentifierImpl implements ColumnIdentifier {

    private String columnName = null;
    private SQLType sqlType = null;

    private ColumnIdentifierImpl(String columnName, SQLType sqlType) {
	this.columnName = columnName;
	this.sqlType = sqlType;
    }

    private static boolean isDelimitedIdentifier(String identifier) {
	return identifier != null && identifier.startsWith("\"")
		&& identifier.endsWith("\"");
    }

    private static String extractValueFromDelimitedIdentifier(String identifier) {
	if (!isDelimitedIdentifier(identifier))
	    throw new IllegalArgumentException(
		    "String must be an delimited identifier!!!!");
	return identifier.substring(1, identifier.length() - 1);
    }

    /**
     * Build a Column Identifier from a R2RML config file.
     * 
     * @param columnName
     *            The column name.
     * @return
     */
    public static ColumnIdentifier buildFromR2RMLConfigFile(String columnName) {
	if (columnName == null)
	    return null;

	// MySQL
	DriverType currentDriver = R2RMLProcessor.getDriverType();
	if (currentDriver.equals(DriverType.MysqlDriver)) {
	    if (isDelimitedIdentifier(columnName)) {
		String internValue = extractValueFromDelimitedIdentifier(columnName);
		return new ColumnIdentifierImpl(internValue.toLowerCase(), null);
	    }
	    return new ColumnIdentifierImpl(columnName.toLowerCase(), null);
	}
	// PostgreSQL
	else if (currentDriver.equals(DriverType.PostgreSQL)) {
	    if (isDelimitedIdentifier(columnName)) {
		String internValue = extractValueFromDelimitedIdentifier(columnName);
		if (internValue.toLowerCase().equals(internValue)) {
		    return new ColumnIdentifierImpl(internValue, null);
		}
		return new ColumnIdentifierImpl(columnName, null);
	    }
	    else {
		return new ColumnIdentifierImpl(columnName.toLowerCase(), null);
	    }
	}
	// Be optimist...
	return new ColumnIdentifierImpl(columnName, null);
    }

    /**
     * Build a Column identifier from a ResultSetMetadata. May have specific
     * rule depending of the driver...
     * 
     * @param columnName
     * @return
     * @throws SQLException
     */
    public static ColumnIdentifier buildFromJDBCResultSet(
	    ResultSetMetaData meta, int index) throws SQLException {
	String columnLabel = meta.getColumnLabel(index);
	SQLType sqlType = SQLType.toSQLType(meta.getColumnType(index));

	/*
	 * MySQL: not case-sensitive.... No notion of regular or delimited
	 * identifier
	 */
	DriverType currentDriver = R2RMLProcessor.getDriverType();
	if (currentDriver.equals(DriverType.MysqlDriver)) {
	    return new ColumnIdentifierImpl(columnLabel.toLowerCase(), sqlType);
	}
	/*
	 * Postgres: assume strings with only lowercase are "regular identifier"
	 * and strings with at least a non lowercase are "delimited identifier".
	 */
	else if (currentDriver.equals(DriverType.PostgreSQL)) {
	    if (columnLabel.toLowerCase().equals(columnLabel)) {
		return new ColumnIdentifierImpl(columnLabel, sqlType);
	    }
	    else {
		return new ColumnIdentifierImpl("\"" + columnLabel + "\"",
			sqlType);
	    }
	}
	// Be optimist...
	return new ColumnIdentifierImpl(columnLabel, sqlType);
    }

    public String replaceAll(String input, String replaceValue) {
	// Try simple replace...
	String localResult = input.replaceAll("\\{" + columnName + "\\}",
		replaceValue);

	// MySQL
	// "ID" in template have to match ID column name.
	DriverType currentDriver = R2RMLProcessor.getDriverType();
	if (currentDriver.equals(DriverType.MysqlDriver)) {
	    if (localResult.equals(input)) {
		Set<String> tokens = R2RMLToolkit
			.extractColumnNamesFromStringTemplate(input);
		for (String token : tokens) {
		    String lowerToken = token.toLowerCase();
		    if ((isDelimitedIdentifier(token) && extractValueFromDelimitedIdentifier(
			    lowerToken).equals(columnName))
			    || lowerToken.equals(columnName)) {
			localResult = input.replaceAll("\\{" + token + "\\}",
				replaceValue);
			break;
		    }
		}
	    }
	}
	// Postgres
	// "\"toto\"" is the same than "toto"
	else if (currentDriver.equals(DriverType.PostgreSQL)) {
	    if (localResult.equals(input)
		    && columnName.toLowerCase().equals(columnName)) {
		localResult = input.replaceAll("\\{\"" + columnName + "\"\\}",
			replaceValue);
	    }
	    // Search "columnName" not case-sensitive
	    if (localResult.equals(input) && !isDelimitedIdentifier(columnName)) {
		Set<String> tokens = R2RMLToolkit
			.extractColumnNamesFromStringTemplate(input);
		for (String token : tokens) {
		    if (token.toLowerCase().equals(columnName)) {
			localResult = input.replaceAll("\\{" + token + "\\}",
				replaceValue);
			break;
		    }
		}
	    }
	}

	// Must have replaced something
	assert !localResult.equals(input) : ("Impossible to replace "
		+ columnName + " in " + input);
	return localResult;
    }

    public SQLType getSqlType() {
	return sqlType;
    }

    @Override
    public String toString() {
	return columnName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((columnName == null) ? 0 : columnName.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof ColumnIdentifierImpl)) {
	    return false;
	}
	ColumnIdentifierImpl other = (ColumnIdentifierImpl) obj;
	if (columnName == null) {
	    if (other.columnName != null) {
		return false;
	    }
	}
	else if (!columnName.equals(other.columnName)) {
	    return false;
	}
	return true;
    }
}
