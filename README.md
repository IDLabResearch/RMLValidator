RMLValidator
============

Validator for[R2]RML mapping documents. 

It is implemented in Java based on [RDFUbit](https://github.com/AKSW/RDFUnit) and [DB2Triples](https://github.com/antidot/db2triples/)

Installation
------------
You can install RMLValidator using Maven, so make sure you have installed it first: http://maven.apache.org/download.cgi and java 1.7

    mvn clean install

Usage
-----
The validator can be run using Maven. You can run a mapping process by executing the following command.
    
    mvn exec:java -Dexec.args=-m "<mapping_file>"

Or using the JAR file

    java -jar target/RMLValidator-1.0.jar -m "<mapping_file>"

With 
    
    <mapping_file> = The RML mapping file conform with the [RML specification](http://semweb.mmlab.be/ns/rml)
        
For instance, to run example1, execute the following command by replacing the paths to the files with the local paths:

    mvn exec:java -Dexec.args="-m /path/to/the/mapping/document/example.rml.ttl"

Remark
-----

On OSX, it might be needed to export JAVA_HOME=$(/usr/libexec/java_home)

More Information
----------------

More information about the solution can be found at http://rml.io

This application is developed by Multimedia Lab http://www.mmlab.be

Copyright 2014, Multimedia Lab - Ghent University - iMinds

License
-------

The RMLProcessor is released under the terms of the [MIT license](http://opensource.org/licenses/mit-license.html).
