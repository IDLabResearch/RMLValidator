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
 * SQL : SQL Toolkit
 *
 * Collection of useful tool-methods used in SQL. 
 *
 *
 ****************************************************************************/
package net.antidot.sql.model.tools;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLToolkit {
	
	/**
	 * Check if the row at the cursor of rs1 is contained by rs2.
	 * @param rs1
	 * @param rs2
	 * @return
	 * @throws SQLException
	 */
	public static boolean containsTheSameRow(ResultSet rs1, ResultSet rs2) throws SQLException{
		ResultSetMetaData metas1 = rs1.getMetaData();
		ResultSetMetaData metas2 = rs2.getMetaData();
		if (metas1.getColumnCount() != metas2.getColumnCount()) return false;
		int index2 = rs2.getRow(); 
		// Init
		rs2.beforeFirst();
		boolean result = false;
		while (rs2.next()){
			boolean sameRow = true;
			for (int i = 1; i <= metas2.getColumnCount(); i++){
				sameRow &= rs1.getString(i).equals(rs2.getString(i));
			}
			if (sameRow) {
				result = true;
				break;
			}
		}
		// Restore last index
		rs2.beforeFirst();
		rs2.relative(index2);
		return result;
	}
}
