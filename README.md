Reladomo Kata 
=============
[![][travis img]][travis]
[![][license-apache img]][license-apache]

A [kata](https://en.wikipedia.org/wiki/Kata) is an exercise in martial arts. 
A [code kata](http://codekata.com/) is an exercise in programming which helps hone your skills through practice and repetition. 
The Reladomo Kata is a fun way to help you learn idiomatic Reladomo usage. 
This particular kata is set up as a series of unit tests which fail. 
Your task is to make them pass, using Reladomo.

Pre-requisites
--------------
1) [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

2) [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or [Eclipse](https://www.eclipse.org/downloads/)

3) [Apache Maven](https://maven.apache.org/) 

Initialize Kata
---------------
Clone this repo or simply download and extract the master [zip file](https://github.com/goldmansachs/reladomo-kata/archive/master.zip) 
then follow the instructions below for your IntelliJ IDEA. We currently do not have a setup process for Eclipse and Netbeans. 

### IntelliJ IDEA users

Once you have extracted the downloaded zip file, it comes with all the necessary project files. 
You can open the project from "File" => "Open..." => choose "reladomo-kata" folder which you just extracted. 
You might see a dialog to suggest importing as a Maven project, please import it as a Maven project.
If you do not get the option to import it as a Maven project, you can do so from the Maven tool window.
Setup the project JDK from "File" => "Project Structure" => Setup the Project SDK
Run the Maven clean and install. It should generate the necessary files.
Run the unit tests under main-kata/src/test/java/kata/test.
Happy coding!

### Eclipse users

Once you have extracted the downloaded zip file, it is ready to be opened in your Eclipse Workspace. 
You can open the project from "File" => "Open Projects from File System..." => choose "reladomo-kata" folder which you just extracted. 
The project should automatically be imported as a Maven project. In case it is not imported as a Maven project, you can import it by right click on reladomo-kata => "Maven" => "Configure..."

##### Setup the project JDK: 
Maven will automatically select Java 8 for you, make sure you have your JDK 8 installed and properly configured in Eclipse "Preferences" => "Installed JREs".
If not follow the below steps:
 
1) Navigate to "Window" => "Preferences" => "Installed JREs" => "Add" => "Standard VM". 

2) For "JRE home:" click on "Directory..." find your Java 8 JDK directory and click "OK", all other fields should auto-populate. 

3) Click on "Finish".

4) Make sure the JDK you just setup, is checked off under the "Installed JREs".

##### Run the Maven build 
1) Right click on "reladomo-kata-parent".
 
2) Navigate to "Run As" => "Maven build". 

3) Under "Goals", enter the command: `-DskipTests=true clean install`

4) Click on "Run".

It should generate all the necessary files.
Run the unit tests under main-kata/src/test/java/kata/test.
Happy coding!

### NetBeans users
We haven't come up with an instruction for NetBeans yet.

Kata Presentation
-----------------
### Slides available online
You can access the slides for the [Main Kata](https://goldmansachs.github.io/reladomo-kata/main-kata-presentation/ReladomoKata.xhtml) and [Mini Kata](https://goldmansachs.github.io/reladomo-kata/mini-kata-presentation/MiniKataPresentation.xhtml) online.

### IntelliJ IDEA users
Run the Maven task "site" to generate slides for the kata.
It should generate the presentation.
Navigate to main-kata/target/presentation/reladomokata and open the ReladomoKata.xhtml in your favorite browser.

### Eclipse users
1) Right click on "reladomo-kata-parent".

2) Navigate to "Run As" => "Maven build".

3) Under "Goals", enter the command: `site`

4) Click on "Run".

It should generate the presentation.
Navigate to main-kata/target/presentation/reladomokata and open the ReladomoKata.xhtml in your favorite browser.

[travis]:https://travis-ci.org/goldmansachs/reladomo-kata
[travis img]:https://travis-ci.org/goldmansachs/reladomo-kata.svg?branch=master

[license-apache]:LICENSE.txt
[license-apache img]:https://img.shields.io/badge/License-Apache%202-blue.svg

Feature: f6
Feature: f7
Feature: f11
