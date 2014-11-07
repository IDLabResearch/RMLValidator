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
 * R2RML Model : SQLBaseTableOrView Class
 *
 * A SQL base table or view is a logical table
 * containing SQL data from a base table or view
 * in the input database.
 * 
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.sql.model.tools.SQLDataValidator;

public class StdSQLBaseTableOrView implements SQLBaseTableOrView {

	private String tableName;

	public StdSQLBaseTableOrView(String tableName)
			throws InvalidR2RMLSyntaxException {
		if (tableName == null)
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Table name must not have to be NULL.");
		if (!SQLDataValidator.isValidSQLIdentifier(tableName))
			throw new InvalidR2RMLSyntaxException(
					"[StdStdSQLBaseTableOrView:construct] Table name must be a valid schema-qualified"
							+ " name.");
		//this.tableName = R2RMLToolkit.deleteBackSlash(tableName);
		this.tableName = tableName;
	}

	public String getEffectiveSQLQuery() {
		// The effective SQL query of a SQL base table or view is SELECT * FROM
		// {table}
		// with {table} replaced with the table or view name.
		return "SELECT * FROM " + tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String toString(){
		return "[StdSQLBaseTableOrView : tableName = " + tableName + "]";
	}

}
