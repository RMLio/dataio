# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Changed
- Use `Charset` instead of `String` for character encoding wherever possible.

### Fixed
- CSVWSourceIterator stopped after a certain number of bytes were parsed (GitLab [issue 21](https://gitlab.ilabt.imec.be/rml/proc/dataio/-/issues/21)). 
  The bug originated from `CSVNullInjector`, it has completely been rewritten into `NewCSVNullInjector`.

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

[1.0.4]: https://github.com/RMLio/dataio/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/RMLio/dataio/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/RMLio/dataio/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/RMLio/dataio/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/RMLio/dataio/releases/tag/v1.0.0