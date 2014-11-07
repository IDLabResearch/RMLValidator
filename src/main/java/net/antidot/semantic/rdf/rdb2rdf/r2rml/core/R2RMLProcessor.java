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
 * R2RML : R2RML Mapper
 *
 * The R2RML Mapper constructs an RDF dataset from
 * 	a R2RML Mapping document and a MySQL database.  
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import net.antidot.sql.model.core.DriverType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

public abstract class R2RMLProcessor {
	
	// Log
	private static Log log = LogFactory.getLog(R2RMLProcessor.class);
	
	private static Long start = 0l;
	
	// JDBC used driver
	private static DriverType driver;
	
	public static DriverType getDriverType() {
	    return driver;
	}	
	
	/**
	 * Convert a database into a RDF graph from a database Connection
	 * and a R2RML instance (with native storage).
	 * @throws R2RMLDataError 
	 * @throws InvalidR2RMLSyntaxException 
	 * @throws InvalidR2RMLStructureException 
	 * @throws IOException 
	 * @throws RDFParseException 
	 * @throws RepositoryException 
	 */
	public static SesameDataSet convertDatabase(Connection conn,
			String pathToR2RMLMappingDocument, String baseIRI, String pathToNativeStore, DriverType driver) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException, R2RMLDataError, InvalidR2RMLStructureException, InvalidR2RMLSyntaxException, RepositoryException, RDFParseException, IOException {
		log.info("[R2RMLMapper:convertMySQLDatabase] Start Mapping R2RML...");
		// Init time
		start = System.currentTimeMillis();
		// Extract R2RML Mapping object
		R2RMLMapping r2rmlMapping = null;
		
		R2RMLProcessor.driver = driver;
		r2rmlMapping = R2RMLMappingFactory.extractR2RMLMapping(pathToR2RMLMappingDocument, driver);
		
		// Connect database
		R2RMLEngine r2rmlEngine = new R2RMLEngine(conn);
		SesameDataSet result =  r2rmlEngine.runR2RMLMapping(r2rmlMapping, baseIRI, pathToNativeStore);
		log.info("[R2RMLMapper:convertDatabase] Mapping R2RML done.");
		Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
		log.info("[R2RMLMapper:convertDatabase] Database extracted in "
				+ stop + " seconds.");
		log.info("[R2RMLMapper:convertDatabase] Number of extracted triples : " +
				result.getSize());
		
		R2RMLProcessor.driver = null;
		return result;
	}
	
	/**
	 * Convert a MySQL database into a RDF graph from a database Connection
	 * and a R2RML instance.
	 * @throws R2RMLDataError 
	 * @throws InvalidR2RMLSyntaxException 
	 * @throws InvalidR2RMLStructureException 
	 * @throws IOException 
	 * @throws RDFParseException 
	 * @throws RepositoryException 
	 */
	public static SesameDataSet convertDatabase(Connection conn,
			String pathToR2RMLMappingDocument,  String baseIRI, DriverType driver) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException, R2RMLDataError, InvalidR2RMLStructureException, InvalidR2RMLSyntaxException, RepositoryException, RDFParseException, IOException {
		return convertDatabase(conn, pathToR2RMLMappingDocument, baseIRI, null, driver);
	}


}
