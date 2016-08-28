# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).


--------
###[0.4.0](N/A) - 2016-08-28
####Added
* ProjectsUtil.loadProjectFiles()
* JsonObject.fromJsonFile(File) overload
* MainEcliseClasspathUtils loadProjectPackageLibFiles(), getProjectsContainingPkgs(), and printProjectsContainingPkgs()

####Changed
* Added PackageSet constructor validation parameter to check that project directory names match package-lib.json project names
* Renamed remote git repository from DependencyShift to DependencyManagement


--------
###[0.3.2](https://github.com/TeamworkGuy2/DependencyShift/commit/4bcc58188ee1fd5b2ec58698fb862969d488e62d) - 2016-08-21
####Changed
* Renamed RepositoryInfo -> RepositorySet
* Updated some dependencies to latest version
* Added JCollectionBuilders and JTuples dependencies


--------
###[0.3.1](https://github.com/TeamworkGuy2/DependencyShift/commit/69ccd66b039da9767c1335c47b85048cd561f990) - 2016-08-17
####Changed
* Updated some dependency paths to match latest versions
* RepositoryInfo is now generic and RepositoryStructure is more generic
* Renamed MainFindGit -> MainGitUtils
  * Added .gitignore lookup and modify functionality to allow easy management of a common .gitignore file across all projects


--------
###[0.3.0](https://github.com/TeamworkGuy2/DependencyShift/commit/858ada40619bfefe96df900b81d75320450a14ce) - 2016-08-14
####Added
* Proper support for project 3rd party library dependencies (from .classpath files)
  * Added JsonObject as an interface (with static methods) for classes that can read/write JSON data
  * Added LibraryJson for modeling a 3rd party jar file dependency
  * Added DependencyInfo as a parent interface for any type of project dependency (currently PackageJson and LibraryJson)
  * Added LibrarySet for tracking and looking up a project's dependencies by name and version
* Basic git status lookup
  * Added GitStatus for listing modified files in a project using 'git status'

####Changed
* Merged and renamed EclipseClasspath* files to ensure one source for each piece of info and operation
  * EclipseClasspathFile moved into EclipseClasspathDoc and EclipseClasspathEntries
  * Renamed EclipseClasspathReplace -> EclipseClasspathXmlManipulator
* Moved PackageJson and NameVersion to new twg2.dependency.models package

####Removed
* PackageInfo - PackageJson now has all the same fields
* EclipseClasspathFile - use EclipseClasspathDoc instead


--------
###[0.2.1](https://github.com/TeamworkGuy2/DependencyShift/commit/16004a75df34557c28a460650952e2e061c05243) - 2016-08-07
####Added
* MainFindGit for finding projects using or not using git
* new ClasspathExampleFile and ClasspathReplaceFile tests

####Changed
* Renamed EclipseClasspathUtils -> MainEclipseClasspathUtils
* ClassPathEntry now implements Comparable
* EclipseClasspathDoc addClassPathEntry() and removeClassPathEntry()
* NodeUtil methods for filtering, mapping, and insertAfter()


--------
###[0.2.0](https://github.com/TeamworkGuy2/DependencyShift/commit/a5a7c3de2fcdbdbd41ffdd26b26681f8fc9451dd) - 2016-06-21
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