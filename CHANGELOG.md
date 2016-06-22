# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).


--------
###[0.2.0](N/A) - 2016-06-21
####Changed
* Switched from versions.md to CHANGELOG.md format, see http://keepachangelog.com/
* Renamed packages to share 'twg2.dependency' package prefix
* JUnit tests moved to separate test/ directory

#### Fixed
* Added missing jtree-walker dependency


--------
###[0.1.0](https://github.com/TeamworkGuy2/DependencyShift/commit/fe1501fce545ace7bec54d2b1daeec92e06ba400) - 2016-02-05
####Added
* Initial commit, contains some refactored code moved from JParserTools, as well as new code for parsing Node.js NPM 'package.json' style files and comparing version numbers using [jsemver] (https://github.com/zafarkhaja/jsemver).