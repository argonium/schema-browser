# SchemaBrowser
SchemaBrowser is a Java GUI (Swing) application that makes it easy to quickly browse the structure of tables from a database schema.  Users can search either by table name or column name (see the columns in a table, or the tables that reference a column by name).

![Schema Browser](http://argonium.github.io/sb.png)

The application uses a snapshot of a schema, so no connection is required to the database when SchemaBrowser is running.  To produce a snapshot, use the DBConn application (described in [its own repository](https://github.com/argonium/dbconn)) to connect to a database, run the 'export schema tables.xml' command, and then copy tables.xml to the data/ directory in SchemaBrowser before building the application.

To build the application, use Ant to run 'ant clean dist'.  This will produce schema.jar.  To run the application, use 'java -jar schema.jar', or double-click the JAR file from the desktop.

Right-clicking on a table or column name on the left-hand side gives you six options:

1. Copy this table
1. Copy selected tables
1. Copy all tables
1. Copy this table and metadata
1. Copy selected tables and metadata
1. Copy all tables and metadata

If you've selected a column, the options will contain 'column' instead of 'table'.

Selecting one of these options will copy the table / column (and optionally the list of columns in the table, or the tables containing the column) to the clipboard.  If you hold down the SHIFT key while selecting the option, the output is in Markdown format.

Sample output:

    ACTORS
    #   Column       Column Type   Can Be Null?   Primary Key?
    1   ACTOR_ID     serial(10)    No             Yes
    2   ACTOR_NAME   varchar(64)   No             No
    3   ROLE_TYPE    varchar(64)   Yes            No
    4   MEMBER_ID    int4(10)      Yes            No
    5   LAST_ROLE    timestamp     Yes            No

The source code is released under the MIT license.
