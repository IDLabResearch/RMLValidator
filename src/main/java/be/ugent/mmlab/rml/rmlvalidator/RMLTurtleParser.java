/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package be.ugent.mmlab.rml.rmlvalidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.input.BOMInputStream;

import info.aduna.text.ASCIIUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.RDFParserBase;
import org.openrdf.rio.helpers.TurtleParserSettings;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.rio.turtle.TurtleUtil;

/**
 * RDF parser for <a href="http://www.dajobe.org/2004/01/turtle/">Turtle</a>
 * files. This parser is not thread-safe, therefore its public methods are
 * synchronized.
 * <p>
 * This implementation is based on the 2006/01/02 version of the Turtle
 * specification, with slight deviations:
 * <ul>
 * <li>Normalization of integer, floating point and boolean values is dependent
 * on the specified datatype handling. According to the specification, integers
 * and booleans should be normalized, but floats don't.</li>
 * <li>Comments can be used anywhere in the document, and extend to the end of
 * the line. The Turtle grammar doesn't allow comments to be used inside triple
 * constructs that extend over multiple lines, but the author's own parser
 * deviates from this too.</li>
 * <li>The localname part of a prefixed named is allowed to start with a number
 * (cf. <a href="http://www.w3.org/TR/turtle/">the W3C Turtle Working
 * Draft</a>).</li>
 * </ul>
 * 
 * @author Arjohn Kampman
 */
public class RMLTurtleParser extends TurtleParser {

	/*-----------*
	 * Variables *
	 *-----------*/
    
        // Log
        private static Log log = LogFactory.getLog(RMLMappingFactory.class);

	private LineNumberReader lineReader;

	private PushbackReader reader;

	private Resource subject;

	private URI predicate;

	private Value object;

	/*--------------*
	 * Constructors *
	 *--------------*/

	/**
	 * Creates a new TurtleParser that will use a {@link ValueFactoryImpl} to
	 * create RDF model objects.
	 */
	public RMLTurtleParser() {
		super();
	}

	/**
	 * Creates a new TurtleParser that will use the supplied ValueFactory to
	 * create RDF model objects.
	 * 
	 * @param valueFactory
	 *        A ValueFactory.
	 */
	public RMLTurtleParser(ValueFactory valueFactory) {
		super(valueFactory);
	}

	/*---------*
	 * Methods *
	 *---------*/

	public RDFFormat getRDFFormat() {
		return RDFFormat.TURTLE;
	}

	@Override
	public Collection<RioSetting<?>> getSupportedSettings() {
		Set<RioSetting<?>> result = new HashSet<RioSetting<?>>(super.getSupportedSettings());
		result.add(TurtleParserSettings.CASE_INSENSITIVE_DIRECTIVES);
		return result;
	}

	/**
	 * Implementation of the <tt>parse(InputStream, String)</tt> method defined
	 * in the RDFParser interface.
	 * 
	 * @param in
	 *        The InputStream from which to read the data, must not be
	 *        <tt>null</tt>. The InputStream is supposed to contain UTF-8 encoded
	 *        Unicode characters, as per the Turtle specification.
	 * @param baseURI
	 *        The URI associated with the data in the InputStream, must not be
	 *        <tt>null</tt>.
	 * @throws IOException
	 *         If an I/O error occurred while data was read from the InputStream.
	 * @throws RDFParseException
	 *         If the parser has found an unrecoverable parse error.
	 * @throws RDFHandlerException
	 *         If the configured statement handler encountered an unrecoverable
	 *         error.
	 * @throws IllegalArgumentException
	 *         If the supplied input stream or base URI is <tt>null</tt>.
	 */
	public synchronized void parse(InputStream in, String baseURI)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (in == null) {
			throw new IllegalArgumentException("Input stream must not be 'null'");
		}
		// Note: baseURI will be checked in parse(Reader, String)

		try {
			parse(new InputStreamReader(new BOMInputStream(in, false), "UTF-8"), baseURI);
		}
		catch (UnsupportedEncodingException e) {
			// Every platform should support the UTF-8 encoding...
			throw new RuntimeException(e);
		}
	}

	/**
	 * Implementation of the <tt>parse(Reader, String)</tt> method defined in the
	 * RDFParser interface.
	 * 
	 * @param reader
	 *        The Reader from which to read the data, must not be <tt>null</tt>.
	 * @param baseURI
	 *        The URI associated with the data in the Reader, must not be
	 *        <tt>null</tt>.
	 * @throws IOException
	 *         If an I/O error occurred while data was read from the InputStream.
	 * @throws RDFParseException
	 *         If the parser has found an unrecoverable parse error.
	 * @throws RDFHandlerException
	 *         If the configured statement handler encountered an unrecoverable
	 *         error.
	 * @throws IllegalArgumentException
	 *         If the supplied reader or base URI is <tt>null</tt>.
	 */
	public synchronized void parse(Reader reader, String baseURI)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (reader == null) {
			throw new IllegalArgumentException("Reader must not be 'null'");
		}
		if (baseURI == null) {
			throw new IllegalArgumentException("base URI must not be 'null'");
		}

		if (rdfHandler != null) {
			rdfHandler.startRDF();
		}

		lineReader = new LineNumberReader(reader);
		// Start counting lines at 1:
		lineReader.setLineNumber(1);

		// Allow at most 8 characters to be pushed back:
		this.reader = new PushbackReader(lineReader, 8);

		// Store normalized base URI
		setBaseURI(baseURI);

		reportLocation();

		try {
			int c = skipWSC();

			while (c != -1) {
				parseStatement();
				c = skipWSC();
			}
		}
		finally {
			clear();
		}

		if (rdfHandler != null) {
			rdfHandler.endRDF();
		}
	}

	protected void parseStatement()
		throws IOException, RDFParseException, RDFHandlerException
	{

		StringBuilder sb = new StringBuilder(8);

		int c;
		// longest valid directive @prefix
		do {
			c = read();
			if (c == -1 || TurtleUtil.isWhitespace(c)) {
				unread(c);
				break;
			}
			sb.append((char)c);
		}
		while (sb.length() < 8);

		String directive = sb.toString();

		if (directive.startsWith("@") || directive.equalsIgnoreCase("prefix")
				|| directive.equalsIgnoreCase("base"))
		{
			parseDirective(directive);
			skipWSC();
			// SPARQL BASE and PREFIX lines do not end in .
			if (directive.startsWith("@")) {
				verifyCharacterOrFail(read(), ".");
			}
		}
		else {
			unread(directive);
			parseTriples();
			skipWSC();
			verifyCharacterOrFail(read(), ".");
		}
	}

	protected void parseDirective(String directive)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (directive.equalsIgnoreCase("prefix") || directive.equals("@prefix")) {
			parsePrefixID();
		}
		else if (directive.equalsIgnoreCase("base") || directive.equals("@base")) {
			parseBase();
		}
		else if (directive.equalsIgnoreCase("@prefix")) {
			if (!this.getParserConfig().get(TurtleParserSettings.CASE_INSENSITIVE_DIRECTIVES)) {
				reportFatalError("Cannot strictly support case-insensitive @prefix directive in compliance mode.");
			}
			parsePrefixID();
		}
		else if (directive.equalsIgnoreCase("@base")) {
			if (!this.getParserConfig().get(TurtleParserSettings.CASE_INSENSITIVE_DIRECTIVES)) {
				reportFatalError("Cannot strictly support case-insensitive @base directive in compliance mode.");
			}
			parseBase();
		}
		else if (directive.length() == 0) {
			reportFatalError("Directive name is missing, expected @prefix or @base");
		}
		else {
			reportFatalError("Unknown directive \"" + directive + "\"");
		}
	}

	protected void parsePrefixID()
		throws IOException, RDFParseException, RDFHandlerException
	{
		skipWSC();

		// Read prefix ID (e.g. "rdf:" or ":")
		StringBuilder prefixID = new StringBuilder(8);

		while (true) {
			int c = read();

			if (c == ':') {
				unread(c);
				break;
			}
			else if (TurtleUtil.isWhitespace(c)) {
				break;
			}
			else if (c == -1) {
				throwEOFException();
			}

			prefixID.append((char)c);
		}

		skipWSC();

		verifyCharacterOrFail(read(), ":");

		skipWSC();

		// Read the namespace URI
		URI namespace = parseURI();

		// Store and report this namespace mapping
		String prefixStr = prefixID.toString();
		String namespaceStr = namespace.toString();

		setNamespace(prefixStr, namespaceStr);

		if (rdfHandler != null) {
			rdfHandler.handleNamespace(prefixStr, namespaceStr);
		}
	}

	protected void parseBase()
		throws IOException, RDFParseException, RDFHandlerException
	{
		skipWSC();

		URI baseURI = parseURI();

		setBaseURI(baseURI.toString());
	}

	protected void parseTriples()
		throws IOException, RDFParseException, RDFHandlerException
	{
		int c = peek();

		// If the first character is an open bracket we need to decide which of
		// the two parsing methods for blank nodes to use
		if (c == '[') {
			c = read();
			skipWSC();
			c = peek();
			if (c == ']') {
				c = read();
				subject = createBNode();
				skipWSC();
				parsePredicateObjectList();
			}
			else {
				unread('[');
				subject = parseImplicitBlank();
			}
			skipWSC();
			c = peek();

			// if this is not the end of the statement, recurse into the list of
			// predicate and objects, using the subject parsed above as the subject
			// of the statement.
			if (c != '.') {
				parsePredicateObjectList();
			}
		}
		else {
			parseSubject();
			skipWSC();
			parsePredicateObjectList();
		}

		subject = null;
		predicate = null;
		object = null;
	}

	protected void parsePredicateObjectList()
		throws IOException, RDFParseException, RDFHandlerException
	{
		predicate = parsePredicate();

		skipWSC();

		parseObjectList();

		while (skipWSC() == ';') {
			read();

			int c = skipWSC();

			if (c == '.' || // end of triple
					c == ']') // end of predicateObjectList inside blank node
			{
				break;
			}
			else if (c == ';') {
				// empty predicateObjectList, skip to next
				continue;
			}

			predicate = parsePredicate();

			skipWSC();

			parseObjectList();
		}
	}

	protected void parseObjectList()
		throws IOException, RDFParseException, RDFHandlerException
	{
		parseObject();

		while (skipWSC() == ',') {
			read();
			skipWSC();
			parseObject();
		}
	}

	protected void parseSubject()
		throws IOException, RDFParseException, RDFHandlerException
	{
		int c = peek();

		if (c == '(') {
			subject = parseCollection();
		}
		else if (c == '[') {
			subject = parseImplicitBlank();
		}
		else {
			Value value = parseValue();

			if (value instanceof Resource) {
				subject = (Resource)value;
			}
			else {
				reportFatalError("Illegal subject value: " + value);
			}
		}
	}

	protected URI parsePredicate()
		throws IOException, RDFParseException
	{
		// Check if the short-cut 'a' is used
		int c1 = read();

		if (c1 == 'a') {
			int c2 = read();

			if (TurtleUtil.isWhitespace(c2)) {
				// Short-cut is used, return the rdf:type URI
				return RDF.TYPE;
			}

			// Short-cut is not used, unread all characters
			unread(c2);
		}
		unread(c1);

		// Predicate is a normal resource
		Value predicate = parseValue();
		if (predicate instanceof URI) {
			return (URI)predicate;
		}
		else {
			reportFatalError("Illegal predicate value: " + predicate);
			return null;
		}
	}

	protected void parseObject()
		throws IOException, RDFParseException, RDFHandlerException
	{
		int c = peek();

		if (c == '(') {
			object = parseCollection();
		}
		else if (c == '[') {
			object = parseImplicitBlank();
		}
		else {
			object = parseValue();
		}

		reportStatement(subject, predicate, object);
	}

	/**
	 * Parses a collection, e.g. <tt>( item1 item2 item3 )</tt>.
	 */
	protected Resource parseCollection()
		throws IOException, RDFParseException, RDFHandlerException
	{
		verifyCharacterOrFail(read(), "(");

		int c = skipWSC();

		if (c == ')') {
			// Empty list
			read();
			return RDF.NIL;
		}
		else {
			BNode listRoot = createBNode();

			// Remember current subject and predicate
			Resource oldSubject = subject;
			URI oldPredicate = predicate;

			// generated bNode becomes subject, predicate becomes rdf:first
			subject = listRoot;
			predicate = RDF.FIRST;

			parseObject();

			BNode bNode = listRoot;

			while (skipWSC() != ')') {
				// Create another list node and link it to the previous
				BNode newNode = createBNode();
				reportStatement(bNode, RDF.REST, newNode);

				// New node becomes the current
				subject = bNode = newNode;

				parseObject();
			}

			// Skip ')'
			read();

			// Close the list
			reportStatement(bNode, RDF.REST, RDF.NIL);

			// Restore previous subject and predicate
			subject = oldSubject;
			predicate = oldPredicate;

			return listRoot;
		}
	}

	/**
	 * Parses an implicit blank node. This method parses the token <tt>[]</tt>
	 * and predicateObjectLists that are surrounded by square brackets.
	 */
	protected Resource parseImplicitBlank()
		throws IOException, RDFParseException, RDFHandlerException
	{
		verifyCharacterOrFail(read(), "[");

		BNode bNode = createBNode();

		int c = read();
		if (c != ']') {
			unread(c);

			// Remember current subject and predicate
			Resource oldSubject = subject;
			URI oldPredicate = predicate;

			// generated bNode becomes subject
			subject = bNode;

			// Enter recursion with nested predicate-object list
			skipWSC();

			parsePredicateObjectList();

			skipWSC();

			// Read closing bracket
			verifyCharacterOrFail(read(), "]");

			// Restore previous subject and predicate
			subject = oldSubject;
			predicate = oldPredicate;
		}

		return bNode;
	}

	/**
	 * Parses an RDF value. This method parses uriref, qname, node ID, quoted
	 * literal, integer, double and boolean.
	 */
	protected Value parseValue()
		throws IOException, RDFParseException
	{
		int c = peek();

		if (c == '<') {
			// uriref, e.g. <foo://bar>
			return parseURI();
		}
		else if (c == ':' || TurtleUtil.isPrefixStartChar(c)) {
			// qname or boolean
			return parseQNameOrBoolean();
		}
		else if (c == '_') {
			// node ID, e.g. _:n1
			return parseNodeID();
		}
		else if (c == '"' || c == '\'') {
			// quoted literal, e.g. "foo" or """foo""" or 'foo' or '''foo'''
			return parseQuotedLiteral();
		}
		else if (ASCIIUtil.isNumber(c) || c == '.' || c == '+' || c == '-') {
			// integer or double, e.g. 123 or 1.2e3
			return parseNumber();
		}
		else if (c == -1) {
			throwEOFException();
			return null;
		}
		else {
			reportFatalError("Expected an RDF value here, found '" + (char)c + "'");
			return null;
		}
	}

	/**
	 * Parses a quoted string, optionally followed by a language tag or datatype.
	 */
	protected Literal parseQuotedLiteral()
		throws IOException, RDFParseException
	{
		String label = parseQuotedString();

		// Check for presence of a language tag or datatype
		int c = peek();

		if (c == '@') {
			read();

			// Read language
			StringBuilder lang = new StringBuilder(8);

			c = read();
			if (c == -1) {
				throwEOFException();
			}

			boolean verifyLanguageTag = getParserConfig().get(BasicParserSettings.VERIFY_LANGUAGE_TAGS);
			if (verifyLanguageTag && !TurtleUtil.isLanguageStartChar(c)) {
				reportError("Expected a letter, found '" + (char)c + "'",
						BasicParserSettings.VERIFY_LANGUAGE_TAGS);
			}

			lang.append((char)c);

			c = read();
			while (!TurtleUtil.isWhitespace(c)) {
				// SES-1887 : Flexibility introduced for SES-1985 and SES-1821 needs
				// to be counterbalanced against legitimate situations where Turtle
				// language tags do not need whitespace following the language tag
				if (c == '.' || c == ';' || c == ',' || c == ')' || c == ']' || c == -1) {
					break;
				}
				if (verifyLanguageTag && !TurtleUtil.isLanguageChar(c)) {
					reportError("Illegal language tag char: '" + (char)c + "'",
							BasicParserSettings.VERIFY_LANGUAGE_TAGS);
				}
				lang.append((char)c);
				c = read();
			}

			unread(c);

			return createLiteral(label, lang.toString(), null, lineReader.getLineNumber(), -1);
		}
		else if (c == '^') {
			read();

			// next character should be another '^'
			verifyCharacterOrFail(read(), "^");

			// Read datatype
			Value datatype = parseValue();
			if (datatype instanceof URI) {
				return createLiteral(label, null, (URI)datatype, lineReader.getLineNumber(), -1);
			}
			else {
				reportFatalError("Illegal datatype value: " + datatype);
				return null;
			}
		}
		else {
			return createLiteral(label, null, null, lineReader.getLineNumber(), -1);
		}
	}

	/**
	 * Parses a quoted string, which is either a "normal string" or a """long
	 * string""".
	 */
	protected String parseQuotedString()
		throws IOException, RDFParseException
	{
		String result = null;

		int c1 = read();

		// First character should be '"' or "'"
		verifyCharacterOrFail(c1, "\"\'");

		// Check for long-string, which starts and ends with three double quotes
		int c2 = read();
		int c3 = read();

		if ((c1 == '"' && c2 == '"' && c3 == '"') || (c1 == '\'' && c2 == '\'' && c3 == '\'')) {
			// Long string
			result = parseLongString(c2);
		}
		else {
			// Normal string
			unread(c3);
			unread(c2);

			result = parseString(c1);
		}

		// Unescape any escape sequences
		try {
			result = TurtleUtil.decodeString(result);
		}
		catch (IllegalArgumentException e) {
			reportError(e.getMessage(), BasicParserSettings.VERIFY_DATATYPE_VALUES);
		}

		return result;
	}

	/**
	 * Parses a "normal string". This method requires that the opening character
	 * has already been parsed.
	 */
	protected String parseString(int closingCharacter)
		throws IOException, RDFParseException
	{
		StringBuilder sb = new StringBuilder(32);

		while (true) {
			int c = read();

			if (c == closingCharacter) {
				break;
			}
			else if (c == -1) {
				throwEOFException();
			}

			sb.append((char)c);

			if (c == '\\') {
				// This escapes the next character, which might be a '"'
				c = read();
				if (c == -1) {
					throwEOFException();
				}
				sb.append((char)c);
			}
		}

		return sb.toString();
	}

	/**
	 * Parses a """long string""". This method requires that the first three
	 * characters have already been parsed.
	 */
	protected String parseLongString(int closingCharacter)
		throws IOException, RDFParseException
	{
		StringBuilder sb = new StringBuilder(1024);

		int doubleQuoteCount = 0;
		int c;

		while (doubleQuoteCount < 3) {
			c = read();

			if (c == -1) {
				throwEOFException();
			}
			else if (c == closingCharacter) {
				doubleQuoteCount++;
			}
			else {
				doubleQuoteCount = 0;
			}

			sb.append((char)c);

			if (c == '\\') {
				// This escapes the next character, which might be a '"'
				c = read();
				if (c == -1) {
					throwEOFException();
				}
				sb.append((char)c);
			}
		}

		return sb.substring(0, sb.length() - 3);
	}

	protected Literal parseNumber()
		throws IOException, RDFParseException
	{
		StringBuilder value = new StringBuilder(8);
		URI datatype = XMLSchema.INTEGER;

		int c = read();

		// read optional sign character
		if (c == '+' || c == '-') {
			value.append((char)c);
			c = read();
		}

		while (ASCIIUtil.isNumber(c)) {
			value.append((char)c);
			c = read();
		}

		if (c == '.' || c == 'e' || c == 'E') {

			// read optional fractional digits
			if (c == '.') {

				if (TurtleUtil.isWhitespace(peek())) {
					// We're parsing an integer that did not have a space before the
					// period to end the statement
				}
				else {
					value.append((char)c);

					c = read();

					while (ASCIIUtil.isNumber(c)) {
						value.append((char)c);
						c = read();
					}

					if (value.length() == 1) {
						// We've only parsed a '.'
						reportFatalError("Object for statement missing");
					}

					// We're parsing a decimal or a double
					datatype = XMLSchema.DECIMAL;
				}
			}
			else {
				if (value.length() == 0) {
					// We've only parsed an 'e' or 'E'
					reportFatalError("Object for statement missing");
				}
			}

			// read optional exponent
			if (c == 'e' || c == 'E') {
				datatype = XMLSchema.DOUBLE;
				value.append((char)c);

				c = read();
				if (c == '+' || c == '-') {
					value.append((char)c);
					c = read();
				}

				if (!ASCIIUtil.isNumber(c)) {
					reportError("Exponent value missing", BasicParserSettings.VERIFY_DATATYPE_VALUES);
				}

				value.append((char)c);

				c = read();
				while (ASCIIUtil.isNumber(c)) {
					value.append((char)c);
					c = read();
				}
			}
		}

		// Unread last character, it isn't part of the number
		unread(c);

		// String label = value.toString();
		// if (datatype.equals(XMLSchema.INTEGER)) {
		// try {
		// label = XMLDatatypeUtil.normalizeInteger(label);
		// }
		// catch (IllegalArgumentException e) {
		// // Note: this should never happen because of the parse constraints
		// reportError("Illegal integer value: " + label);
		// }
		// }
		// return createLiteral(label, null, datatype);

		// Return result as a typed literal
		return createLiteral(value.toString(), null, datatype, lineReader.getLineNumber(), -1);
	}

	protected URI parseURI()
		throws IOException, RDFParseException
	{
		StringBuilder uriBuf = new StringBuilder(100);

		// First character should be '<'
		int c = read();
		verifyCharacterOrFail(c, "<");

		// Read up to the next '>' character
		while (true) {
			c = read();

			if (c == '>') {
				break;
			}
			else if (c == -1) {
				throwEOFException();
			}

			if (c == ' ') {
				reportFatalError("IRI included an unencoded space: '" + c + "'");
			}

			uriBuf.append((char)c);

			if (c == '\\') {
				// This escapes the next character, which might be a '>'
				c = read();
				if (c == -1) {
					throwEOFException();
				}
				if (c != 'u' && c != 'U') {
					reportFatalError("IRI includes string escapes: '\\" + c + "'");
				}
				uriBuf.append((char)c);
			}
		}

		if (c == '.') {
			reportFatalError("IRI must not end in a '.'");
		}

		String uri = uriBuf.toString();

		// Unescape any escape sequences
		try {
			// FIXME: The following decodes \n and similar in URIs, which should be
			// invalid according to test <turtle-syntax-bad-uri-04.ttl>
			uri = TurtleUtil.decodeString(uri);
		}
		catch (IllegalArgumentException e) {
			reportError(e.getMessage(), BasicParserSettings.VERIFY_DATATYPE_VALUES);
		}

		return super.resolveURI(uri);
	}

	/**
	 * Parses qnames and boolean values, which have equivalent starting
	 * characters.
	 */
	protected Value parseQNameOrBoolean()
		throws IOException, RDFParseException
	{
		// First character should be a ':' or a letter
		int c = read();
		if (c == -1) {
			throwEOFException();
		}
		if (c != ':' && !TurtleUtil.isPrefixStartChar(c)) {
			reportError("Expected a ':' or a letter, found '" + (char)c + "'",
					BasicParserSettings.VERIFY_RELATIVE_URIS);
		}

		String namespace = null;

		if (c == ':') {
			// qname using default namespace
			namespace = getNamespace("");
		}
		else {
			// c is the first letter of the prefix
			StringBuilder prefix = new StringBuilder(8);
			prefix.append((char)c);

			int previousChar = c;
			c = read();
			while (TurtleUtil.isPrefixChar(c)) {
				prefix.append((char)c);
				previousChar = c;
				c = read();
			}

			if (c != ':') {
				// prefix may actually be a boolean value
				String value = prefix.toString();

				if (value.equals("true") || value.equals("false")) {
					return createLiteral(value, null, XMLSchema.BOOLEAN, lineReader.getLineNumber(), -1);
				}
			}
			else {
				if (previousChar == '.') {
					// '.' is a legal prefix name char, but can not appear at the end
					reportFatalError("prefix can not end with with '.'");
				}
			}

			verifyCharacterOrFail(c, ":");

			namespace = getNamespace(prefix.toString());
		}

		// c == ':', read optional local name
		StringBuilder localName = new StringBuilder(16);
		c = read();
		if (TurtleUtil.isNameStartChar(c)) {
			if (c == '\\') {
				localName.append(readLocalEscapedChar());
			}
			else {
				localName.append((char)c);
			}

			int previousChar = c;
			c = read();
			while (TurtleUtil.isNameChar(c)) {
				if (c == '\\') {
					localName.append(readLocalEscapedChar());
				}
				else {
					localName.append((char)c);
				}
				previousChar = c;
				c = read();
			}

			// Unread last character
			unread(c);

			if (previousChar == '.') {
				// '.' is a legal name char, but can not appear at the end, so is
				// not actually part of the name
				unread(previousChar);
				localName.deleteCharAt(localName.length() - 1);
			}
		}
		else {
			// Unread last character
			unread(c);
		}

		// if (c == '.') {
		// reportFatalError("Blank node identifier must not end in a '.'");
		// }

		// Note: namespace has already been resolved
		return createURI(namespace + localName.toString());
	}

	private char readLocalEscapedChar()
		throws RDFParseException, IOException
	{
		int c = read();

		if (TurtleUtil.isLocalEscapedChar(c)) {
			return (char)c;
		}
		else {
			throw new RDFParseException("found '" + (char)c + "', expected one of: "
					+ Arrays.toString(TurtleUtil.LOCAL_ESCAPED_CHARS));
		}
	}

	/**
	 * Parses a blank node ID, e.g. <tt>_:node1</tt>.
	 */
	protected BNode parseNodeID()
		throws IOException, RDFParseException
	{
		// Node ID should start with "_:"
		verifyCharacterOrFail(read(), "_");
		verifyCharacterOrFail(read(), ":");

		// Read the node ID
		int c = read();
		if (c == -1) {
			throwEOFException();
		}
		else if (!TurtleUtil.isNameStartChar(c)) {
			reportError("Expected a letter, found '" + (char)c + "'", BasicParserSettings.PRESERVE_BNODE_IDS);
		}

		StringBuilder name = new StringBuilder(32);
		name.append((char)c);

		// Read all following letter and numbers, they are part of the name
		c = read();
		while (TurtleUtil.isNameChar(c)) {
			name.append((char)c);
			c = read();
		}

		unread(c);

		return createBNode(name.toString());
	}

	protected void reportStatement(Resource subj, URI pred, Value obj)
		throws RDFParseException, RDFHandlerException
	{
		Statement st = createStatement(subj, pred, obj);
		if (rdfHandler != null) {
			rdfHandler.handleStatement(st);
		}
	}

	/**
	 * Verifies that the supplied character <tt>c</tt> is one of the expected
	 * characters specified in <tt>expected</tt>. This method will throw a
	 * <tt>ParseException</tt> if this is not the case.
	 */
        @Override
	protected void verifyCharacterOrFail(int c, String expected)
		//throws RDFParseException
	{
		if (c == -1) {
                    log.error("[RMLTurtleParser:verifyCharacterOrFail] exception");
			//throwEOFException();
		}
		else if (expected.indexOf((char)c) == -1) {
			StringBuilder msg = new StringBuilder(32);
			msg.append("Expected ");
			for (int i = 0; i < expected.length(); i++) {
				if (i > 0) {
					msg.append(" or ");
				}
				msg.append('\'');
				msg.append(expected.charAt(i));
				msg.append('\'');
			}
			msg.append(", found '");
			msg.append((char)c);
			msg.append("'");
log.error("[RMLTurtleParser:verifyCharacterOrFail] fatal error! ");
log.error("[RMLTurtleParser:verifyCharacterOrFail] " + msg.toString());
			//reportFatalError(msg.toString());
		}
	}

	/**
	 * Consumes any white space characters (space, tab, line feed, newline) and
	 * comments (#-style) from <tt>reader</tt>. After this method has been
	 * called, the first character that is returned by <tt>reader</tt> is either
	 * a non-ignorable character, or EOF. For convenience, this character is also
	 * returned by this method.
	 * 
	 * @return The next character that will be returned by <tt>reader</tt>.
	 */
	protected int skipWSC()
		throws IOException, RDFHandlerException
	{
		int c = read();
		while (TurtleUtil.isWhitespace(c) || c == '#') {
			if (c == '#') {
				processComment();
			}

			c = read();
		}

		unread(c);

		return c;
	}

	/**
	 * Consumes characters from reader until the first EOL has been read. This
	 * line of text is then passed to the {@link #rdfHandler} as a comment.
	 */
	protected void processComment()
		throws IOException, RDFHandlerException
	{
		StringBuilder comment = new StringBuilder(64);
		int c = read();
		while (c != -1 && c != 0xD && c != 0xA) {
			comment.append((char)c);
			c = read();
		}

		// c is equal to -1, \r or \n.
		// In case c is equal to \r, we should also read a following \n.
		if (c == 0xD) {
			c = read();

			if (c != 0xA) {
				unread(c);
			}
		}
		if (rdfHandler != null) {
			rdfHandler.handleComment(comment.toString());
		}
		reportLocation();
	}

	protected int read()
		throws IOException
	{
		return reader.read();
	}

	protected void unread(int c)
		throws IOException
	{
		if (c != -1) {
			reader.unread(c);
		}
	}

	protected void unread(String directive)
		throws IOException
	{
		for (int i = directive.length() - 1; i >= 0; i--) {
			reader.unread(directive.charAt(i));
		}
	}

	protected int peek()
		throws IOException
	{
		int result = read();
		unread(result);
		return result;
	}

	protected void reportLocation() {
		reportLocation(lineReader.getLineNumber(), -1);
	}

	/**
	 * Overrides {@link RDFParserBase#reportWarning(String)}, adding line number
	 * information to the error.
	 */
	@Override
	protected void reportWarning(String msg) {
		reportWarning(msg, lineReader.getLineNumber(), -1);
	}

	/**
	 * Overrides {@link RDFParserBase#reportError(String)}, adding line number
	 * information to the error.
	 */
	@Override
	protected void reportError(String msg, RioSetting<Boolean> setting)
		throws RDFParseException
	{
		reportError(msg, lineReader.getLineNumber(), -1, setting);
	}

	/**
	 * Overrides {@link RDFParserBase#reportFatalError(String)}, adding line
	 * number information to the error.
	 */
	@Override
	protected void reportFatalError(String msg)
		//throws RDFParseException
	{
            try {
                reportFatalError(msg, lineReader.getLineNumber(), -1);
            } catch (RDFParseException ex) {
                Logger.getLogger(RMLTurtleParser.class.getName()).log(Level.SEVERE, null, ex);
            }
	}

	/**
	 * Overrides {@link RDFParserBase#reportFatalError(Exception)}, adding line
	 * number information to the error.
	 */
	@Override
	protected void reportFatalError(Exception e)
		throws RDFParseException
	{
		reportFatalError(e, lineReader.getLineNumber(), -1);
	}

	protected void throwEOFException()
		throws RDFParseException
	{
		throw new RDFParseException("Unexpected end of file");
	}
}
