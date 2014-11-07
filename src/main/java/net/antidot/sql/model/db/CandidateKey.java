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
 * SQL model : Candidate Key
 * 
 * Represents candidate keys of a database according to W3C database model.
 * A candidate key of a relation is a minimal superkey for that relation;
 * that is, a set of attributes such that :
 * 		1) the relation does not have two distinct tuples (i.e. rows or
 * 		   records in common database language) with the same values for these
 * 		   attributes (which means that the set of attributes is a superkey)
 * 		
 * 		2) there is no proper subset of these attributes for which (1)
 * 		   holds (which means that the set is minimal).
 * 
 * Reference : Direct Mapping Definition, 
 * A Direct Mapping of Relational Data to RDF W3C Working Draft 24 March 2011 
 *
 */
package net.antidot.sql.model.db;

import java.util.ArrayList;

public class CandidateKey implements Key {

	/**
	 * Describe type of key. For instance, a key can be primary (unique for a table and allow to identify a row)  or foreign (allow to specify an entity of another table)
	 */
	public enum KeyType {
		PRIMARY, 
		FOREIGN,
		REFERENCE;
	}

	private ArrayList<String> columnNames;
	private String sourceTable;
	private KeyType type;

	public CandidateKey(ArrayList<String> columnNames) {
		if (columnNames == null)
			throw new IllegalStateException(
					"[CandidateKey:CandidateKey] This list of column names doesn't exist.");
		this.columnNames = new ArrayList<String>();
		this.columnNames.addAll(columnNames);
		this.sourceTable = null;
		this.type = null;
	}

	public CandidateKey(ArrayList<String> columnNames, KeyType type) {
		this(columnNames);
		this.type = type;
	}

	public CandidateKey(ArrayList<String> columnNames, String sourceTable) {
		this(columnNames);
		this.sourceTable = sourceTable;
	}

	public CandidateKey(ArrayList<String> columnNames, String sourceTable,
			KeyType type) {
		this(columnNames, sourceTable);
		this.type = type;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public KeyType getKeyType() {
		return type;
	}

	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public String toString() {
		String result = "{[CandidateKey:toString]";
		result += " columnNames = " + columnNames + "; sourceTable = "
				+ sourceTable + "; type = " + type +"}";
		return result;
	}

	/**
	 * Return true if the type key is the same of given type. 
	 * @param type
	 * @return
	 */
	public boolean isType(KeyType type) {
		return (this.type == type);
	}
	
	public boolean matchSameColumns(Key key){
		boolean result = columnNames.size() == key.getColumnNames().size();
		for (String columnName : key.getColumnNames()){
			result &= columnNames.contains(columnName);
		}
		return result;
	}


}
