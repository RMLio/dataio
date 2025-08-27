# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [2.1.1] - 2025-08-27

### Fixed
- Use Maven Central Portal for publishing new releases.

## [2.1.0] - 2025-08-27

### Added
- Support for getting data from HTTP servers, with optional Solid authentication. At the moment only HTTP/1 works
- Compression to access; TO DO: tar archives don't work yet.
- A parameter `nulls` in the constructor of `CSVSourceIterator` to define null values for CSV fields.
- A method `getIndex` in `CSVWSourceIterator` to know the current record number.

## [2.0.3] - 2025-01-30

### Fixed
- Update Javadoc

## [2.0.2] - 2025-01-30

### Changed
- Explicitly declare checked exceptions instead of generic `Exception`.

## [2.0.1] - 2025-01-22

### Fixed
- Upgrade snowflake-jdbc to 3.21.0 to fix security CVEs.

## [2.0.0] - 2025-01-09

### Fixed
- Updated excel-streaming-reader to 5.0.2
- Updated fuseki to 5.0.0
- Updated json-path to 2.9.0
- Updated junit to 5.10.2
- Updated log4j-to-slf4j 2.23.1
- Updated mssql-jdbc to 12.6.3.jre11
- Updated mysql-connector-j to 9.0.0
- Updated ojdbc11 to 23.4.0.24.05
- Updated opencsv to 5.9
- Updated postgresql to 42.7.3
- Updated rxjava to 3.1.8
- Updated SaxonHE to 12.5
- Updated sfm-csv to 9.0.2
- Updated slf4j to 2.0.12
- Updated testcontainers to 1.20.3
- Log name of subclass in log messages of Record.
- Removed direct dependency on poi
- Removed dependency on xlsx-streamer
- Removed direct dependency on tika-parsers-standard-package
- Removed dependency on javax.activation-api

### Changed
- `Record.get(<reference>)` now returns a `RecordValue` object which wraps the actual result.
  It also provides methods to check for an empty (null) value, a 'reference not found' or an error. This is a breaking change to the API.
- A DATAIO_NULL value gets a number appended to avoid multiple null header clashes. E.g., DATAIO_NULL_12.

## [1.3.1] - 2024-08-12

### Fixed
- DATAIO_NULL value when CSV quoted string has multiple new lines (GitHub [issue 238](https://github.com/RMLio/rmlmapper-java/issues/238)).
- Typo in `bump-version.sh`

## [1.3.0] - 2024-05-21

### Changed
- Added small change for mimetypes in new RML.

## [1.2.0] - 2024-05-14

### Fixed
- Exclude `xml-apis` from pom file (See GitHub [issue 2](https://github.com/RMLio/dataio/issues/2)).
- Updated `mysql-connector-j` to 8.2.0
- Updated `ojdbc11` to 23.3.0.23.09

### Changed
- Removed Maven shade plugin See GitHub [issue 2](https://github.com/RMLio/dataio/issues/2)).

## [1.1.0] - 2024-01-17

### Changed
- Use `Charset` instead of `String` for character encoding wherever possible.

### Fixed
- CSVWSourceIterator stopped after a certain number of bytes were parsed (GitLab [issue 21](https://gitlab.ilabt.imec.be/rml/proc/dataio/-/issues/21)). 
  The bug originated from `CSVNullInjector`, it has completely been rewritten into `NewCSVNullInjector`.

### Changed
- JSONRecord: if reference starts with a dot, but not with '$' no longer adding an extra dot

### Added
- Test case for JSONPath starting with two dots and no '$'

## [1.0.4] - 2023-10-31

### Fixed
- Remove (unused) DistributionManagement in pom.xml

## [1.0.3] - 2023-10-31

### Fixed
- Version number + required info for publishing to maven central in pom.xml

## [1.0.2] - 2023-10-31

### Fixed
- Maven central deployment (again)

## [1.0.1] - 2023-10-31

### Fixed
- Maven central deployment

## [1.0.0] - 2023-10-30

### Added
- Prepare for publishing on the Maven central repository.

### Changed
- Require Java 17 (or more recent)
- Use SFM for CSV parsing

### Fixed
- Updated Maven Surefire plugin to 3.1.2
- Removed dependency on Logback
- Updated POI to 5.2.3
- Removed dependency on Commons CSV
- Updated all testcontainers to 1.18.3
- Updated mssql-jdbc to 12.2.1.jre11
- Updated ojdbc11 to 23.2.0.0
- Updated mybatis to 3.5.13
- Updated jOpenDocument to 1.3
- Updated xlsx-streamer to 2.2.0
- Updated rxjava to 3.1.6
- Added source encoding in `pom.xml`
- Removed unused dependency on jackson-annotations.
- Removed dependency on commons-io
- Removed unused code
- Use Jena to query local RDF files instead of Fuseki, eliminating need to start SPARQL endpoint.

### Security
- Updated OpenCSV to 5.8
- Updated JSonPath to 2.8.0
- Updated JSoup to 1.16.1
- Updated PostgreSQL driver to 42.6.0
- Updated mysql-connector to 8.1.0
- Updated jsurfer-jackson to 1.6.4
- Updated apache-any23-encoding to 2.7
- Updated excel-streaming-reader to 4.0.5
- Updated jena-fuseki-* to 4.9.0

[2.1.1]: https://github.com/RMLio/dataio/compare/v2.1.0...v2.1.1
[2.1.0]: https://github.com/RMLio/dataio/compare/v2.0.3...v2.1.0
[2.0.3]: https://github.com/RMLio/dataio/compare/v2.0.2...v2.0.3
[2.0.2]: https://github.com/RMLio/dataio/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/RMLio/dataio/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/RMLio/dataio/compare/v1.3.1...v2.0.0
[1.3.1]: https://github.com/RMLio/dataio/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/RMLio/dataio/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/RMLio/dataio/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/RMLio/dataio/compare/v1.0.4...v1.1.0
[1.0.4]: https://github.com/RMLio/dataio/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/RMLio/dataio/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/RMLio/dataio/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/RMLio/dataio/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/RMLio/dataio/releases/tag/v1.0.0
