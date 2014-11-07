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
 * R2RML Model : R2RMLView Class
 *
 * An R2RML view is a logical table whose contents
 * are the result of executing a SQL query against
 * the input database.
 * 
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import net.antidot.sql.model.tools.SQLDataValidator;

public class StdR2RMLView implements R2RMLView {

	private String sqlQuery;
	private Set<SQLVersion> sqlVersion;

	public StdR2RMLView(String sqlQuery) {
		this(sqlQuery, null);
	}

	public StdR2RMLView(String sqlQuery, Set<SQLVersion> sqlVersions) {
		if (sqlQuery == null)
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Query must not have to be NULL.");
		if (!SQLDataValidator.isValidSQLQuery(sqlQuery))
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Query must be SQL valid.");
		this.sqlQuery = sqlQuery;
		this.sqlVersion = new HashSet<SQLVersion>();
		if (sqlVersions == null || sqlVersions.isEmpty())
			// The absence of a SQL version identifier indicates that no claim
			// to
			// Core SQL 2008 conformance is made.
			this.sqlVersion.add(SQLVersion.SQL2008);
		else
			this.sqlVersion.addAll(sqlVersions);
	}

	public String getEffectiveSQLQuery() {
		// The effective SQL query of an R2RML view is the value of its
		// rr:sqlQuery property.
		return sqlQuery;
	}

	public String getSQLQuery() {
		return sqlQuery;
	}

	public Set<SQLVersion> getSQLVersion() {
		return sqlVersion;
	}

	public String toString() {
		return "[StdSQLBaseTableOrView : sqlVersion = " + sqlVersion
				+ "; sqlQuery = " + sqlQuery + "]";
	}
}
