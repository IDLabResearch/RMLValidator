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
 * Direct Mapping : Key
 * 
 * Interface which defines a generic key. 
 * A "key" is a representation of an element which etablish links between 
 * tables in a database.
 * 
 ****************************************************************************/
package net.antidot.sql.model.db;

import java.util.ArrayList;

public interface Key {
	
	/**
	 * Returns table name which contains this key.
	 * @return
	 */
	public String getSourceTable();


	/**
	 * Set the source table name.
	 * @param  sourceTable
	 */
	public void setSourceTable(String sourceTable);

	/**
	 * Returns column names which compose the key.
	 * @return
	 */
	public ArrayList<String> getColumnNames();

	/**
	 * Return true if the key has the same column names than another key.
	 * @param key
	 * @return
	 */
	public boolean matchSameColumns(Key key);
	
}
