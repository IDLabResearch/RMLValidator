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

package net.antidot.sql.model.core;

// JDBC driver types	
public class DriverType {	    
    public static DriverType MysqlDriver = new DriverType("com.mysql.jdbc.Driver");
    public static DriverType PostgreSQL = new DriverType("org.postgresql.Driver");

    private String driverName;
    
    public DriverType(String driverName) {
	this.driverName=  driverName;
    }
    
    public String getDriverName() {
	return driverName;
    }
    
    @Override
    public String toString() {
	return getDriverName();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((driverName == null) ? 0 : driverName.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof DriverType)) {
	    return false;
	}
	DriverType other = (DriverType) obj;
	if (driverName == null) {
	    if (other.driverName != null) {
		return false;
	    }
	}
	else if (!driverName.equals(other.driverName)) {
	    return false;
	}
	return true;
    }
}
