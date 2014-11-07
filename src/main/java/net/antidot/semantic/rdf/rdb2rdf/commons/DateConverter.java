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
 * RDB2RDF Commons : Date Converter
 *
 * Collection of useful tool-methods used for manipulate and convert date format.
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.antidot.semantic.xmls.xsd.XSDDataConverter;
import net.antidot.sql.model.type.SQLType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateConverter {
	
	// Log
	private static Log log = LogFactory.getLog(DateConverter.class);

	// Convert type methods

	/**
	 * Convert a SQL timestamp in a date format in a conform string date.
	 * 
	 * @param mySQLType
	 * @param timestamp
	 * @param timeZone
	 */
	public static String dateFormatToDate(SQLType sqlType,
			Long timestamp, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[DateConverter:dateFormatToDate] mySQLType : " + sqlType
					+ " timestamp : " + timestamp);
		// Constructs a Date object using the given milliseconds time value.
		// But, timestamp in MySQL is given in seconds.
		timestamp *= 1000;
		if (!sqlType.isDateType())
			throw new IllegalStateException(
					"[DateConverter:dateFormatToDate] MySQLType forbidden : it must be in a date format.");
		Date date = timestampToDate(timestamp);
		switch (sqlType) {
		case TIME:
			return XSDDataConverter.dateToXSDTime(date, timeZone);

		case DATE:
			return XSDDataConverter.dateToXSDDate(date, timeZone);

		case TIMESTAMP:
			return XSDDataConverter.dateToISO8601(date, timeZone);

		default:
			throw new IllegalStateException(
					"[DateConverter:dateFormatToDate] Unknown format date.");
		}
	}

	/**
	 * Convert a timestamp into a Date object.
	 */
	public static Date timestampToDate(Long timestamp) {
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] timestamp : " + timestamp);
		// Date object represents a unqiue time point like the timestamp
		// Timezone is not take into account here.
		Date date = new Date(timestamp);
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] converted Date : " + date);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddz");
		String test = df.format(date);
		if (log.isDebugEnabled())
			log.debug("[DateConverter:timestampToDate] timezone : " + test);
		return date;
	}
	

}
