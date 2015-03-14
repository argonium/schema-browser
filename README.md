# SchemaBrowser
SchemaBrowser is a Java GUI (Swing) application that makes it easy to quickly browse the structure of tables from a database schema.  Users can search either by table name or column name (see the columns in a table, or the tables that reference a column by name).

The application uses a snapshot of a schema, so no connection is required to the database when SchemaBrowser is running.  To produce a snapshot, use the DBConn application (described in its own repository on this site) to connect to a database, run the 'export schema tables.xml' command, and then copy tables.xml to the data/ directory in SchemaBrowser before building the application.

To build the application, use Ant to run 'ant clean dist'.  This will produce schema.jar.  To run the application, use 'java -jar schema.jar', or double-click the JAR file from the desktop.

The source code is released under the MIT license.
