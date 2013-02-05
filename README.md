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
database to XML. Some initial data is available in "initial-data.xml".


Import data from XML (using DBUnit)
-----------------------------------

curl -H "Content-Type: application/xml" -X POST -d @initial-data.xml http://localhost:8080/eventjuggler-rest/data


Export data to XML (using DBUnit)
---------------------------------

curl http://localhost:8080/eventjuggler-rest/data > export.xml


Clear database
--------------

curl http://localhost:8080/eventjuggler-rest/data/clear


Import data from Meetup
-----------------------

Until we have some sample data of our own it is possible to import data from Meetup. Before doing
this you have to sign-up to Meetup and get an api key from http://www.meetup.com/meetup_api/key/. 
You'll also need the id of the category of events you want to import (34 is tech), the list of
categories can be retrieved from http://api.meetup.com/2/categories?key=<api key>. After importing
an array containing the title of the imported events is returned. 

curl 'http://localhost:8080/eventjuggler-rest/data/meetup&key=<api key>&category=<category>&page=<number of events>'
