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
 * RDB2RDF Commons : RDF Prefixes
 *
 * List of useful prefix used in RDF IRIs. 
 *
 *
 ****************************************************************************/

package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;

public abstract class RDFPrefixes {
	
	// Namespaces
	public static HashMap<String, String> prefix = new HashMap<String, String>();
	
	static {
		prefix.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefix.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefix.put("xsd", "http://www.w3.org/2001/XMLSchema#");
	}

}
