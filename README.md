EventJuggler
============


Prerequisites
=============

- Java Development Kit 1.6
- Recent Git client
- Recent Maven 3
- JBoss AS 7.1.3.Final


Deploying to JBoss AS
=====================

The default configuration is to deploy everything in one ear. There's also a profile (standalone)
that makes it possible to deploy individul parts of the system as wars. The standalone profile
changes the configuration of which modules are deployed, and also includes services (ejb) into the
wars that require it.


Deploy everything in one ear (server)
-------------------------------------

mvn clean jboss-as:deploy


Deploy everything as separate wars
----------------------------------

mvn -Pstandalone clean jboss-as:deploy


Deploy individual parts
-----------------------

rest:
mvn -Pstandalone -f rest/pom.xml clean jboss-as:deploy

scaffold:
mvn -Pstandalone -f scaffold/pom.xml clean jboss-as:deploy

web-js:
mvn -Pstandalone -f web-js/pom.xml clean jboss-as:deploy

web-jsf:
mvn -Pstandalone -f web-jsf/pom.xml clean jboss-as:deploy


Import Data
===========

There is a data import and export service available. This uses DBUnit to import and export the 
database to XML.


Import data from XML (using DBUnit)
-----------------------------------

curl -H "Content-Type: application/xml" -X POST -d @export.xml http://localhost:8080/eventjuggler-rest/data


Export data to XML (using DBUnit)
---------------------------------

curl http://localhost:8080/eventjuggler-rest/data > export.xml


Clear database
--------------

curl http://localhost:8080/eventjuggler-rest/data/clear


Functional Tests
================

The testsuite contains a set of integration tests. The tests can be execute in either managed or remote mode.

To run the testsuite in managed mode, run:

    mvn -Pit-managed -Djboss.home=<PATH TO JBOSS AS> clean install

In managed mode you have to provide "-Djboss.zip" as it requires a full package (see the installation section).

To run the testsuite in remote mode, first start a JBoss AS with the EventJuggler Services sub-systems enabled, and run:

    mvn -Pit-remote clean install
