#!/usr/bin/env bash

# exit if command fails
set -e

if [ -z "$1" ]
then
	echo 'Version parameter not given. Invoke as e.g. ./bump-version.sh 1.0.0'.
	exit 1
fi

# function to read `y` (yes) or `n` (no).
function yes_or_no {
	while true; do
		read -p "$* [y/n]: " yn
		case $yn in
			[Yy]*) return 1  ;;
			[Nn]*) echo "Aborted" ; return 0 ;;
		esac
	done
}


VERSION=$1
echo "Changing version to $VERSION"

echo 'Updating pom.xml...'
mvn versions:set -DnewVersion=$VERSION

echo 'Updating README.md...'
sed -i -e "s|<version>.*<\/version>|<version>$VERSION</version>|" README.md

if [ ! "$(yes_or_no 'Do you also want to add the version to CHANGELOG.md?')" ]
then
	changefrog -n $VERSION
fi
