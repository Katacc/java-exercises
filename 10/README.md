# Using an SQLite database with Java

Java has JDBC (Java DataBase Connectivity) which can be used to 
store and retrieve data to and from a compatible SQL database.

The easiest SQL database to work with is SQLite. It needs no server,
since the database is all in one file. The [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) driver is required.

For more information about JDBC, see [JDBC Basics](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html) or a dozen others like it.

## Creating the database

Use the SQLite command line interface to create the database file
and the tables:

    % sqlite3 events.sqlite3

This creates a new database with the given filename. To create the tables,
enter the SQL statements in create_tables.sql or use the SQLite shell:

    sqlite> .read create_tables.sql

To create the categories, use the SQL statements in the file insert_categories.sql.

The 10,000 fictional tech events are in the file fake10k.csv. Each of them
has the category ID 1, with the primary name as "test" and the secondary name
as "fake", so that they can easily be filtered out.

The event table in the database uses an automatically incremented primary key,
but those are not found in the CSV file. That is why we use a temporary table
to read in the events from the CSV file. The temporary table is created like
this:

    sqlite> create table temp_event (event_date DATE, event_description TEXT, category_id INTEGER);
 
To store the fake tech events into the temporary table, insert the contents
of the file:

    sqlite> .import --csv fake10k.csv temp_event

This will read the file fake10k.csv and insert the contents of the rows into
the temporary table. For more information, 
see the SQLite shell [documentation](https://www.sqlite.org/cli.html#importing_files_as_csv_or_other_formats).

Then insert the events from the temporary table to the actual event table:

    sqlite> insert into event(event_date, event_description, category_id) select * from temp_event;

Finally, clean up:

    sqlite> drop table temp_event;

## Installing the sqlite-jdbc driver

Insert the following dependency into your Maven pom.xml file:

    <dependency>
      <groupId>org.xerial</groupId>
      <artifactId>sqlite-jdbc</artifactId>
      <version>3.49.1.0</version>
    </dependency>

Synchronize the project if necessary.

## Connecting to the database

To connect to the database with JDBC you need a URL. The protocol is "jdbc",
followed by the database type (in our case "sqlite"). Because an SQLite database
is one flat file, you need to also have the filename in the URI. An example
could be

    jdbc:sqlite:events.sqlite3

where "events.sqlite3" is the file name (it could have also a path).

Use the `java.sql.DriverManager` class to connect to the database, 
and handle any `java.sql.SQLException`s.

Typically a [try-with-resources construct](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) is used to ensure that the database
connection is automatically closed.

## Retrieving data from the database

To retrieve data you need to use the SQL SELECT statement. Use the database
connection to create a `java.sql.Statement` with the query, then execute
the statement with the `executeQuery` method of the `Statement` object.

Executing the query gives you back a `java.sql.ResultSet` which you then 
need to examine and pick out the relevant parts. Typically you process the
whole result set by calling the `next` method until it returns `false`.

You could also use a `PreparedStatement`, especially if you need many
parameters in the SQL query.

## Inserting data into the database

To insert data into the database, you use a database connection and a
statement, but the SQL statement is iNSERT instead of SELECT, and you
need to call the `executeUpdate` method.

Note that in the case of the events database, you need to find out
the category ID of the event category. If the category does not exist
in the database, you need to create a new one. So, you will need to 
fetch the categories first, and then use an INSERT statement to add 
a new category if necessary, and save its category ID. 
Only after that can you insert the new event with the correct category.
If the event category is an existing one, you can just use its ID.
