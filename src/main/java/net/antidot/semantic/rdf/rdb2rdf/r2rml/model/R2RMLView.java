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
 * R2RML Model : R2RMLView Interface
 *
 * An R2RML view is a logical table whose contents
 * are the result of executing a SQL query against
 * the input database.
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import java.util.Set;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

public interface R2RMLView extends LogicalTable {

	public enum SQLVersion {
		SQL2008("SQL2008");
		// The RDB2RDF Working Group intends to maintain a non-normative
		// list of identifiers for other SQL versions

		private String version;

		private SQLVersion(String version) {
			this.version = version;
		}

		public static SQLVersion getSQLVersion(String version) throws R2RMLDataError{
			for (SQLVersion sqlVersion : SQLVersion.values())
				if (sqlVersion.toString().equals(version) || (R2RMLVocabulary.R2RML_NAMESPACE + sqlVersion.toString()).equals(version))
					return sqlVersion;
			// Unkwown SQL Version
		    throw new R2RMLDataError("[R2RMLView:getSQLVersion] Unknow SQL version : " + version);
		}

		public String toString() {
			return version;
		}
	}

	/**
	 * A SQL query is a SELECT query in the SQL language that can be executed
	 * over the input database.
	 */
	public String getSQLQuery();

	/**
	 * An R2RML view may have one or more SQL version identifiers. The absence
	 * of a SQL version identifier indicates that no claim to Core SQL 2008
	 * conformance is made.
	 */
	public Set<SQLVersion> getSQLVersion();

}
