/***************************************************************************
 *
 * SQL Data Validator
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Tools
 * 
 * Fichier			:	SQLToolkit.java
 *
 * Description		:   Collection of useful tool-methods used for valid
 * 						SQL data. 
 * 
 * References 		:   [SQL2] ISO/IEC 9075-2:2008 SQL - Part 2: Foundation 
 * 						(SQL/Foundation). International Organization
 * 						for Standardization, 27 January 2009.
 * 
 * 						[TURTLE] Turtle - Terse RDF Triple Language, 
 * 						Dave Beckett, Tim Berners-Lee.
 * 						World Wide Web Consortium, 14 January 2008
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.sql.model.tools;

public class SQLDataValidator {
	
	/**
	 * Check if a name is a valid SQL identifer.
	 * A SQL identifier is the name of a SQL object, such as a column, table,
	 * view, schema, or catalog. [SQL2]
	 */
	public static boolean isValidSQLIdentifier(String sqlIdentifier){
		//TODO
		return true;
	}
	
	/**
	 * Check if a SQL query is valid. 
	 * The value of rr:sqlQuery must conform to the production 
	 * <direct select statement: multiple rows> in [SQL2]
	 * with an optional trailing semicolon character and optional
	 *  surrounding white space (excluding comments) as defined in [TURTLE].
	 */
	public static boolean isValidSQLQuery(String sqlQuery){
		//TODO
		return true;
	}

}
