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
 * R2RML Model : SQLBaseTableOrView Interface
 *
 * A SQL base table or view is a logical table
 * containing SQL data from a base table or view
 * in the input database.
 * 
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

public interface SQLBaseTableOrView extends LogicalTable {
	
	/**
	 * A SQL base tables or views is represented by a resource that has exactly
	 * one rr:tableName property. 
	 */
	public String getTableName();

}
