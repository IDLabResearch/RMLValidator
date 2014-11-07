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
 * SQL model : Standard Body
 *
 * Represents body of a database according to W3C database model. 
 * The body contains all rows of a table.
 * 
 * Reference : Direct Mapping Definition, 
 * - A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011
 * - A Direct Mapping of Relational Data to RDF W3C Working Draft 20 September 2011 
 *
 */
package net.antidot.sql.model.db;

import java.util.HashSet;

public class StdBody implements Body {

	private HashSet<Row> rows;
	private StdTable parentTable;

	public StdBody(HashSet<Row> rows, StdTable parentTable) {
		this.rows = rows;
		this.parentTable = parentTable;
	}

	/**
	 * Return row of this body.
	 * @return
	 */
	public HashSet<Row> getRows() {
		return rows;
	}

	public StdTable getParentTable() {
		return parentTable;
	}

	/**
	 * Set rows of this table.
	 * @param rows
	 */
	public void setRows(HashSet<Row> rows) {
		this.rows = rows;
	}

	public void setParentTable(StdTable parentTable) {
		this.parentTable = parentTable;
	}

	public String toString() {
		String result = "{[Body:toString] parentTable = " + parentTable.getTableName();
		result += "}";
		return result;
	}

	public HashSet<Tuple> getTuples() {
		HashSet<Tuple> tuples = new HashSet<Tuple>();
		tuples.addAll(rows);
		return tuples;
	}

}
