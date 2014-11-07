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
 * SQL model : Body
 *
 * Represents body of a database. 
 * The body contains all tuples of a table.
 * 
 */
package net.antidot.sql.model.db;

import java.util.HashSet;

public interface Body {

	/**
	 * Return tuples of a table.
	 * @return
	 */
	public HashSet<Tuple> getTuples();

	/**
	 * Return table which contains this body.
	 * @return
	 */
	public Table getParentTable();
}
