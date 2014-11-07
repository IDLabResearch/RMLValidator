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
 * XSD Data Converter
 *
 * Collection of useful tool-methods used for convert XSD data. 
 * 
 ****************************************************************************/
package net.antidot.semantic.xmls.xsd;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XSDDataConverter {

	// Log
	private static Log log = LogFactory.getLog(XSDDataConverter.class);

	/**
	 * Convert a Date object into a string in valid xsd:time format.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToXSDTime(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timeZone : " + timeZone);
		String result = null;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		result = df.format(date);
		// xsd:date requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] result : " + result);
		return result;
	}

	/**
	 * Convert a Date object into a string in valid xsd:date format.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToXSDDate(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timeZone : " + timeZone);
		String result = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		result = df.format(date);
		// xsd:date requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] result : " + result);
		return result;
	}

	/**
	 * Convert a Date object into a string in valid xsd:dateTime.
	 * 
	 * @param date
	 * @param timeZone
	 *            (optional, null otherwise)
	 * @return
	 */
	public static String dateToISO8601(Date date, String timeZone) {
		if (log.isDebugEnabled())
			log.debug("[XSDDataConverter:dateToXSDTime] date : " + date
					+ " timezone : " + timeZone);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String result = df.format(date);
		// xsd:dateTime requires a ":" in timeZone
		if (timeZone != null)
			result += timeZone;
		if (log.isDebugEnabled())
			log.debug("[SQLConnector:dateToISO8601] result : " + result);
		return result;
	}

}
