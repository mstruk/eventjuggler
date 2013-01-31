Data Services
=============

Export data to XML (using DBUnit)
---------------------------------

curl http://localhost:8080/eventjuggler-rest/data > export.xml


Import data from XML (using DBUnit)
-----------------------------------

curl -H "Content-Type: application/xml" -X POST -d @export.xml http://localhost:8080/eventjuggler-rest/data


Clear database
--------------

curl http://localhost:8080/eventjuggler-rest/data/clear


Steal data from Meetup
----------------------

curl 'http://localhost:8080/eventjuggler-rest/data/steal&key=<meetup api key>&category=<category>&page=<number of events to steal>'

* Note - Use '34' for tech category


Event Services
=============

TODO
