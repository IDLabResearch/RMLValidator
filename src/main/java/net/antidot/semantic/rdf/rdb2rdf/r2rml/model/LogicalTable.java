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
 * R2RML Model : LogicalTable Interface
 *
 * A logical table is a possibly virtual database
 * table that is to be mapped to RDF triples. 
 * A logical table is either a SQL base table or view,
 * or a R2RML view.
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

public interface LogicalTable {

	/**
	 * Every logical table has an effective SQL query that,
	 * if executed over the SQL connection, produces as its
	 * result the contents of the logical table.
	 */
	public String getEffectiveSQLQuery();

}
