# DataIO

The DataIO library opens the given files and allows to read them using an Iterator, a Stream or an RxJava's Observable.
This library doesn't load the full file in memory (this is not the case for XML and ODS files).

## Setup
The easiest is to include DataIO as a maven dependency:

```xml
<dependency>
    <groupId>be.ugent.idlab.knows</groupId>
    <artifactId>dataio</artifactId>
    <version>2.1.0</version>
</dependency>
```
Check the [maven central repository](https://central.sonatype.com/search?q=be.ugent.idlab.knows&namespace=be.ugent.idlab.knows&name=dataio)
for the latest version.

Or if you want to build & install locally:

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
Interface which is an implementation of an Iterator<Record>, which overrides the remove and forEachRemaining as these function are trivial for each implementation.

#### Important note on JSONPath
As JSONPath is not yet standardized, compatibility issues may arise. We follow the implementation of JsonSurfer, with following additions

- A combination of the child operators will be reduced to a single child operator. In practice, this means that `$.['child']` becomes `$['child']`.
- In test cases, you may find a construction like `$.[*]`. This construction will also be reduced to `$[*]`.

### SourceStream
Interface and implementations for streaming the records from sources.

## Flow
Implementation of the Flow interface, using RxJava under the hood. Implementations of RxJava's ``Observable`` for the different records are provided.

#### Functions

open(args): this function opens the corresponding files (using an Access object) and initiates the iterator and other needed values to allow the creation of records (eg CSVSourceIterator initiates a header value). 

### Record
Interface which generalizes the access to data

#### Functions
     
get(String value); returns a list of objects associated to the given string value.

getDataType(String value) returns the IRI of the datatype of a reference in the record

## Magic properties

### _PATH
_PATH is a magic property that can be used to obtain a reverse path through the document to reach a specific object. 
Index notation can be used to grab a specific element.

Suppose a file people.json
```json
{
  "people": [
    {
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumbers": [
        "0123-4567-8888",
        "0123-4567-8910"
      ]
    }
  ]
}
```
And suppose a JSON path ``$.people.[*]``, then a specific path for the first "people" object would be ``$.people.[0]``.
This object's ``_PATH`` property would resolve to ``[0,people]``, and ``_PATH[1]`` would resolve to ``people``.
