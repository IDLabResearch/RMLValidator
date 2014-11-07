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
 * R2RML Exception : R2RML Data Error
 *
 * A data error is a condition of the data in the input 
 * database that would lead to the generation of an
 * invalid RDF term, such as an invalid IRI or an
 * ill-typed literal.
 * 
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.exception;

public class R2RMLDataError extends Exception {

	private static final long serialVersionUID = 1L;
	
	public R2RMLDataError(String message) {
		super(message);
	}

}
