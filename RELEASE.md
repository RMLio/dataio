# Releasing dataio

Step-by-step instructions for publishing a release. [HANDBOOK.md](HANDBOOK.md) (Release process) explains how the tooling works.

## Before you start

- bash (Git Bash on Windows), Maven, Java 21, and `changefrog` (`npm install -g changefrog`), which writes the version section of `CHANGELOG.md`.
- Push access to `origin` (https://gitlab.ilabt.imec.be/rml/proc/dataio).
- You are on `development`, up to date with `origin/development`, with a clean working tree.
- The tests pass: `mvn verify`. The database tests start containers through Testcontainers, so Docker must be running (with Docker Engine 29 or later, add `-Dapi.version=1.44`). On Windows, 31 file-access tests fail because of `file:` paths (`/C:/…`), so run the full suite on Linux or in a container, e.g. `docker run --rm -v "$PWD":/src -w /src -v /var/run/docker.sock:/var/run/docker.sock -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal maven:3.9-eclipse-temurin-21 mvn -B verify -Dapi.version=1.44`.
- `## Unreleased` in `CHANGELOG.md` lists everything since the last release.
- Pick the version with [Semantic Versioning](https://semver.org/). `pom.xml` holds the next patch as `-SNAPSHOT` (e.g. `2.4.1-SNAPSHOT`): release that patch, or a higher minor or major version when the changelog has new features or breaking changes.

## Release

1. Run `./bump-version.sh 2.4.1` (with your version) and answer `y` to both questions. The script
   - sets the version in `pom.xml` and the version in `README.md`;
   - turns `## Unreleased` into the version section of `CHANGELOG.md`;
   - commits "Update version to <version>", pushes `development`, and creates and pushes the tag `v<version>` (e.g. `v2.4.1`);
   - moves `pom.xml` to the next patch `-SNAPSHOT`, and commits and pushes "Prepare for next development cycle".
2. Move `main` to the release: `git push origin v2.4.1^{commit}:main`. `main` always points at the latest release; the push succeeds only as a fast-forward.
3. The tag pipeline (https://gitlab.ilabt.imec.be/rml/proc/dataio/-/pipelines) builds the release with the `release` Maven profile, signs it and deploys it to Maven Central. Check that its deploy job succeeds; the new version then appears at https://repo1.maven.org/maven2/be/ugent/idlab/knows/dataio/ (this can take up to an hour).

## After the release

- GitLab mirrors the branches and tags to GitHub (https://github.com/RMLio/dataio); check that the tag is there.
- Update the consumers: algebraic-mapping-operators and MappingWeaver-java (both `dataio.version`): set the property default to the new release, one MR per repo, AMO first. rmlmapper-java pins its own dataio version.

## When something goes wrong

- The script stops at the first failing command. When it stops before pushing, fix the cause, discard its changes (`git reset --hard origin/development`) and run it again. When it stops between pushing `development` and pushing the tag, finish by hand: create and push the tag on the version commit, then set the next `-SNAPSHOT` (`mvn versions:set -DnewVersion=<next>-SNAPSHOT -DgenerateBackupPoms=false`), commit and push.
- Once the tag is pushed, keep it: fix the cause and retry the failed pipeline job. When the released code itself is broken, release the next patch version.
