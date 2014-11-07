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
 * SQL model : Table
 *
 * Represents table of a database.
 * A table is a set of data elements (values).
 * A table has a specified number of columns,but can have any number of tuples.
 * Each tuple is identified by the values appearing in a particular column subset which has been 
 * identified as a key.
 * 
 */
package net.antidot.sql.model.db;

import java.util.ArrayList;

public interface Table {

	/**
	 * Return name of this table.
	 * @return
	 */
	public String getTableName();

	/**
	 * Set the name of this table.
	 * @param tableName
	 */
	public void setTableName(String tableName);
	
	/**
	 * Reurn header object associated with this table.
	 * @return
	 */
	public Header getHeader();

	/**
	 * Get keys associated with this table.
	 * @return
	 */
	public ArrayList<Key> getKeys();

	/**
	 * Get body associated with this table.
	 * @return
	 */
	public Body getBody();

	/**
	 * Return the unique primary key of this table.
	 * @return
	 */
	public Key getPrimaryKey();


}
