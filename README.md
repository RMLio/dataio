# DataIO

The dataIO library opens the given files and allows to read them using a Source iterator.
This library doesn't load the full file in memory (this is not the case for XML files).

## Setup
Run the following command:

    mvn install

## Test
Run the following command:

    mvn test
## Interfaces

### Access
Interfaces which allows to open a file (remote, database, SPARQL or WoT) and get the corresponding input stream.

#### Functions
getInputStream: opens the file and returns an inputstream

getDataTypes: This method returns a map of datatypes.
References to values are mapped to their datatypes, if available.

getContentType: gives the content type of the access object.

### SourceIterator

Interface which is an implementation of an Iterator<Source>, which overrides the remove and forEachRemaining as these function are trivial for each implementation.

#### Functions

open(args): this function opens the corresponding files (using an Access object) and initiates the iterator and other needed values to allow the creation of sources (eg CSVSourceIterator initiates a header value). 

### Source
Interface which generalizes the access to data

#### Functions
     
get(String value); returns a list of objects associated to the given string value.

getDataType(String value) returns the IRI of the datatype of a reference in the source
