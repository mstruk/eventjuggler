EventJuggler
============


Prerequisites
=============

- Java Development Kit 1.6
- Recent Git client
- Recent Maven 3
- JBoss AS 7.1.1.Final


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

There are some functional tests that runs Arquillian. These tests require JBoss AS to run and are not 
executed by default.

To run the tests with a managed JBoss AS, use the following command:

export JBOSS_HOME=<JBOSS HOME>
mvn -Parq-jbossas-managed test

To run with a remote JBoss AS, start JBoss AS before running the tests, and use this command: 

mvn -Parq-jbossas-remote test
