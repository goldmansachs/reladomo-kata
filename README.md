Reladomo Kata 
=============
A [kata](https://en.wikipedia.org/wiki/Kata) is an exercise in martial arts. 
A [code kata](http://codekata.com/) is an exercise in programming which helps hone your skills through practice and repetition. 
The Reladomo Kata is a fun way to help you learn idiomatic Reladomo usage. 
This particular kata is set up as a series of unit tests which fail. 
Your task is to make them pass, using Reladomo.

Pre-requisites
--------------
1) [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

2) [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

3) [Apache Maven](https://maven.apache.org/) 

Initialize Kata
---------------
Clone this repo or simply download and extract the master [zip file](https://github.com/goldmansachs/reladomo-kata/archive/master.zip) 
then follow the instructions below for your IntelliJ IDEA. We currently do not have a setup process for Eclipse and Netbeans. 

### IntelliJ IDEA users

Once you have extracted the downloaded zip file, it comes with all the necessary project files. 
You can open the project from "File" => "Open..." => choose "reladomo-kata" folder. 
You might see a dialog to suggest importing as a Maven project, please import it as a Maven project.
If you do not get the option to import it as a Maven project, you can do so from the Maven tool window.
Setup the project JDK from "File" => "Project Structure" => Setup the Project SDK
Run the Maven clean and install. It should generate the necessary files.
Run the unit tests under main-kata/src/test/java/kata/test.
Happy coding!

### Eclipse users
We haven't come up with an instruction for Eclipse yet. 

### NetBeans users
We haven't come up with an instruction for NetBeans yet.

Kata Presentation
-----------------
Run the Maven task "site" to generate slides for the kata. 
Navigate to main-kata/target/presentation/reladomokata and open the ReladomoKata.xhtml in your favorite browser.
