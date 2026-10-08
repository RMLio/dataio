# DataIO Handbook

Written for a CS student who wants to understand DataIO as code.

## Contents

- [DataIO Handbook](#dataio-handbook)
  - [Contents](#contents)
  - [Preface](#preface)
  - [Agent request contract (for AI agents/LLMs)](#agent-request-contract-for-ai-agentsllms)
  - [Architecture and package layout](#architecture-and-package-layout)
  - [Build and test](#build-and-test)
  - [Test organization](#test-organization)
  - [Release process](#release-process)

## Preface

DataIO (`be.ugent.idlab.knows:dataio`) is a Java 17 library that opens data sources and
exposes their contents as `Record`s through three interfaces: an `Iterator` (`SourceIterator`),
a Java `Stream` (`SourceStream`) and an RxJava `Observable` (`SourceObservable`). It reads
CSV, CSVW, Excel, ODS, HTML, JSON, JSON Lines and XML, from local files, remote files,
relational databases, SPARQL endpoints, Web of Things descriptions and WebSockets. It reads
sources incrementally; XML and ODS files are the exception and are loaded in memory. It is
used as a dependency by RML engines (e.g. the RMLMapper).

DataIO is a library only: it has no command-line interface or `main` entry point, and it
performs no mapping or RDF generation itself.

Where things live:

- `README.md`: user-facing overview of the interfaces, iterator reuse via `reset(Access)`,
  JSONPath conventions and the `_PATH` magic property.
- `CHANGELOG.md`: Keep a Changelog format, Semantic Versioning.
- `pom.xml`: the Maven build; the `release` profile publishes to Maven Central.
- `src/main/java/be/ugent/idlab/knows/dataio/`: library source.
- `src/test/java/be/ugent/idlab/knows/dataio/`: JUnit 5 tests.
- `src/test/resources/`: test input files, grouped per format or access type.
- `.gitlab-ci.yml`: GitLab CI (CHANGELOG check, Javadoc check, unit tests, Maven Central deploy).
- `bump-version.sh`: release helper.
- `RELEASE.md`: release steps.
- `TODO.md`: open tasks.

## Agent request contract (for AI agents/LLMs)

<!-- software-handbook contract: 2026-10-08 -->

Every implementation request handled by an AI agent/LLM follows these constraints:

- If the request is a feature or bugfix:
  - fix the specific failing case or issue named in the request;
  - preserve existing passing behavior unless explicitly asked not to;
  - add or update a regression test when needed.
- Make the smallest coherent patch. A documentation error found along the way is fixed in the same patch.
- Leave the code leaner after every request: remove what the change makes redundant (duplicate tests, parameters and options that no longer do anything, helpers that duplicate each other, comments that only repeat the code), and reuse shared functionality instead of adding a local variant. Use compiler warnings (`mvn compile`), SpotBugs (`mvn compile spotbugs:check`, see Build and test) and IDE inspection to find unused code, and keep Javadoc valid, because CI runs a Javadoc check.
- Fix a transient environment problem (a stale PATH, a shell or editor that needs a restart) in the environment, by restarting or reconfiguring it; add no code that works around it.
- **Push back** when a request would violate an established principle (e.g. breaking test hermeticity). Explain the principle and suggest a documentation-only fix instead of silently implementing the harmful change.
- Update this handbook so the change is documented as well as implemented.
  - Document only the latest state, integrated in the surrounding narrative (principles, behavior, rationale), including the choices made and why.
  - This contract holds only general rules for handling a request; project-specific guidance goes in the chapter on that topic.
- Do not stop at making tests green; align the implementation with the specification or intended design, and document the semantic reason in this handbook.
- Never remove or change existing tests (code or fixtures) without explicit permission. A change to an existing fixture (expected output, input, or data) is validated by the maintainer before it is kept, also when a tool writes it: propose the change with its reason, and keep it only after approval.
- Update `CHANGELOG.md` for implementation changes: keep `## Unreleased` a short summary of what changed since the last release. A feature that is new since the last release is one Added line, which later fixes update instead of getting lines of their own; lines are for what a user of the last release notices.
- Check whether `README.md` needs updates for user-visible behavior or workflow changes, and update it when needed.
- Write documentation (this handbook, READMEs, `TODO.md`, `CHANGELOG.md`, code comments) as plain positive statements: say what is true and leave out the contrast ("X, not Y"). Keep a negative only when it is the point itself, such as a prohibition, a warning, or a known limitation.
- If there are difficulties during fulfillment, document them in the most appropriate existing handbook location (create a new chapter only when truly necessary) so future requests start with better context.
- A preference or principle that the maintainer states while handling a request is documented so that every later request follows it: a general one in this contract (and in the software-handbook skill it comes from), a project-specific one in the handbook chapter it belongs to. When it is unclear which, ask.
- When a request is a list of feedback (such as a `TODO.md`), clean up after handling it: remove the items that are done, keep every open item as a clear task (an open question or an offered follow-up is an open item), and remove temporary files created along the way.

## Architecture and package layout

All source is under `be.ugent.idlab.knows.dataio`. The data path is
`Access` -> `SourceIterator` / `SourceStream` / `SourceObservable` -> `Record`.

| Package | Role |
| --- | --- |
| `access` | `Access` (serializable): opens a source and returns an `InputStream`, plus datatypes and content type. Implementations: `LocalFileAccess`, `RemoteFileAccess`, `HTTPRequestAccess`, `RDBAccess` (with `DatabaseType`), `SPARQLEndpointAccess`, `SPARQLLocalFileAccess`, `WoTAccess`, `WebSocketAccess`, `VirtualAccess`. `COMPRESSION` lists supported compressions. |
| `compression` | Compression helpers (`Compression`, `Compressor`). |
| `iterators` | `SourceIterator` (abstract; `Iterator<Record>`, `Serializable`, `AutoCloseable`) and one implementation per format: CSV, CSVW, Excel, HTML, JSON, JSON Lines, ODS, XML. `csvw/` holds the CSVW configuration and its builder; `xpath/` holds the namespace resolver for the XML iterator. |
| `streams` | `SourceStream` and per-format implementations (CSV, CSVW, Excel, JSON, ODS, XML). |
| `flow` | `base/SourceObservable` and per-format RxJava observables in `observables/` (CSV, CSVW, Excel, JSON, ODS, XML). |
| `record` | `Record` (value lookup via `get`, datatype via `getDataType`), `RecordValue`, and one record class per format. |
| `export` | `Export` and `FileExport`. |
| `exceptions` | CSV header/row validation exceptions. |
| `utils` | `Utils`, `NAMESPACES`, `NewCSVNullInjector`. |

Iterators, streams and observables are serializable; the `serializability` tests guard this.

`SourceIterator.reset(Access)` points an existing iterator at another source so that
expensive setup (parser, compiled expression) is reused. The XML, JSON and CSVW iterators
implement it; the others throw `UnsupportedOperationException`. A reset iterator is stateful
and belongs to a single thread. The README documents the measured gains.

## Build and test

- Java 17 (`java.version` in `pom.xml`); Maven.
- Build and install locally: `mvn install`.
- All tests: `mvn test`.
- One test class: `mvn test -Dtest=CSVIteratorTest`; a nested class: `-Dtest='DatabaseTest$PostgreSQLTest'`.
- Surefire's default excludes are replaced by an empty exclude so that JUnit 5 `@Nested`
  classes run.
- Tests that need Docker (Testcontainers): `DatabaseTest` (PostgreSQL, MSSQL, MySQL, Oracle
  containers) and `HTTPRequestTest$SolidTests` (Community Solid Server image). Other servers
  run in-process: Fuseki for `SPARQLTest`, the JDK `HttpServer` for `WoTTest` and
  `HTTPRequestTest`, and a Java-WebSocket server for `WebSocketAccessTest`.
- SpotBugs is the linter: `spotbugs-maven-plugin` 4.10.3.0
  with SpotBugs 4.10.3 is declared under `<pluginManagement>` in `pom.xml`. It is bound to no
  lifecycle phase, so builds and CI never fail on findings; run it on demand with
  `mvn compile spotbugs:check`. Its findings are mostly `EI_EXPOSE_REP`/`EI_EXPOSE_REP2`
  (records and CSVW configuration hold caller-owned collections) and `CT_CONSTRUCTOR_THROW`.
  No formatter is configured.
- The tests run on Linux or in a container. Docker must be running for the Testcontainers
  tests (with Docker Engine 29 or later, add `-Dapi.version=1.44`). On Windows, the
  file-access tests fail because of `file:` paths (`/C:/…`). Full suite in a container:
  `docker run --rm -v "$PWD":/src -w /src -v /var/run/docker.sock:/var/run/docker.sock -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal maven:3.9-eclipse-temurin-21 mvn -B verify -Dapi.version=1.44`.

CI (`.gitlab-ci.yml`) has stages `lint`, `unittests` and `deploy`. The lint stage comes from
the included templates (shared `rml/util/ci-templates` project): a check that `CHANGELOG.md`
is updated, and a Javadoc check, so Javadoc must build cleanly. The
`General` unit test job runs on `maven:3-eclipse-temurin-17` with Docker-in-Docker and runs
`mvn -Dtest="$TEST" test` once per entry of an explicit test-class matrix, on every branch
except `main`. A new test class runs in CI only after it is added to that matrix; currently
`SourceIteratorResetTest`, `WebSocketAccessTest`, `ExportTest` and `CSVNullInjectorTest` are
outside the matrix.

## Test organization

- Test classes mirror the main packages: `access`, `iterator`, `stream`, `flow`, `records`,
  `export`, `serializability`, `utils`.
- `cores/` holds shared base classes: `TestCore` (shared evaluators such as `evaluate_0000`,
  record comparison, local access creation, serialization simulation), `LocalAccessTestCore`,
  `StreamTestCore` and `ObservableTestCore`. Per-format tests reuse these evaluators so that
  iterator, stream and observable tests check the same expectations against the same inputs.
- `src/test/resources/` groups inputs per format (`csv`, `csvw`, `excel`, `ods`, `html`,
  `json`, `xml`, `stax`) and per access type (`access` with `compression`, `local_access`,
  `sparql`, `wot`, `db_setup` with SQL setup scripts per database, `community_solid_server`
  with server config and pod contents). File names carry the case they cover, e.g.
  `1001_header_short.csv`, `0002_BOM.csv`.
- Logging during tests is configured by `simplelogger.properties` and `logback-test.xml`.

## Release process

Step-by-step instructions are in [RELEASE.md](RELEASE.md); this section explains the tooling.

`bump-version.sh` accepts a version `X.Y.Z` or `testrelease-*` and stops on any other
format. A `testrelease-*` version gets a tag with that bare name. The tag is meant to
trigger the Maven Central deploy job, defined in the shared CI templates. That build uses
the `release` profile in `pom.xml`: sources jar, Javadoc jar, GPG signing and
`central-publishing-maven-plugin`.
