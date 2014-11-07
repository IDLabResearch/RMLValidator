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
 * Direct Mapping : Tuple
 * 
 * Interface which defines a generic tuple. 
 * A "tuple" is a representation of an entity stored in a table in database.
 * 
 * A "tuple" is always included in a "body" and has some fields associated 
 * with "values". A "value" is represented by its string format.
 * 
 ****************************************************************************/
package net.antidot.sql.model.db;

import java.util.SortedMap;


public interface Tuple {
	
	/**
	 * Get body which contains this tuple.
	 * @return
	 */
	public StdBody getParentBody();

	/**
	 * Set body which contains this tuple.
	 * @param parentBody
	 */
	public void setParentBody(StdBody parentBody);

	/**
	 * Get values contained in this tuple.
	 * @return
	 */
	public SortedMap<String, byte[]> getValues();

	/**
	 * Set values contained in this tuple.
	 * @param values
	 */
	public void setValues(SortedMap<String, byte[]> values);

}
