==================================
EEA ElasticSearch RDF River Plugin
==================================

Introduction
============

The EEA RDF River Plugin for ElasticSearch allows to harvest metadata from
SPARQL endpoints or plain RDF files into ElasticSearch. It is provided as a
plugin.


.. contents::

Installation
============

Prerequisites:

* ElasticSearch 0.90.2

* Java 7 Runtime Environment

Binaries for this plugin are available at:

https://github.com/eea/eea.elasticsearch.river.rdf/blob/master/target/releases/eea-rdf-river-plugin-1.0.zip

In order to install the plugin, you first need to have
`Elasticsearch <http://www.elasticsearch.org/download/>`_ installed. Just
download the latest release and extract it. Add the plugin's binaries to the
elasticsearch-X.Y.Z/plugins/name_of_plugin/ directory, where X.Y.Z is the current
ElasticSearch version.

The same should be done when updating ElasticSearch to the latest version:
download the latest release and extract it. Copy the elasticsearch-x.y.z/plugins
directory to elasticsearch-X.Y.Z, where x.y.z is the previous ElasticSearch
version and X.Y.Z the current one. Replace the previous ElasticSearch directory
with the new one. Restart ElasticSearch.


Main features
=============

1. Indexing RDFs given by their URIs
2. Indexing triples retrieved from a SPARQL endpoint, through SELECT queries
3. Indexing triples retrieved from a SPARQL endpoint, through CONSTRUCT queries
4. Customizable index and type names
5. Blacklist of unnecessary properties
6. Whitelist of required properties
7. Normalization of properties from different namespaces

Indexing
========

Each river can index data into a specific index. The default index has the index name
'rdfdata' and the type name 'resource'.

Creating the RDF river can be done with:

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
   ...
   }
 }'

"eeaRDF" is the name of the river and should not be changed. It gives ElasticSearch
the information about which river to use. Otherwise, the data provided will not be
indexed. "rdf_river" is the name of the rdf river type. Any name can be chosen for
the type, as long as it is unique (it has not been used for a different river).

A new index name and type can be set with:

::

 "index" : {
        "index" : "newIndexName",
        "type" : "newTypeName"
    }


From URIs
+++++++++

The river is given a list of URIs from which triples are indexed into ElasticSearch.
'urls' may contain any list of URIs.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "urls" : ["http://dd.eionet.europa.eu/vocabulary/aq/individualexceedances/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/naturalsourcetype/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/measurementmethod/rdf"]
    }
 }'


From a SPARQL endpoint
++++++++++++++++++++++

The river is given a SPARQL endpoint and a query. The query response is indexed into ElasticSearch.
The SPARQL query can be a SELECT query or a CONSTRUCT query.

The SELECT query should always require a triple (?s ?p ?o) where ?s is the subject,
?p is the predicate and ?o is the object. The names and order are required for relevant
results.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> SELECT ?s ?p ?o WHERE { ?bookmark a cr:SparqlBookmark ; ?p ?o}",
      "queryType" : "select"
   }
 }'

From both URIs and SPARQL endpoint
++++++++++++++++++++++++++++++++++

All supported parameters are optional. Moreover, it is possible to index metadata
from a SPARQL endpoint and several unrelated URIs.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "urls" : ["http://dd.eionet.europa.eu/vocabulary/aq/individualexceedances/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf"],
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> CONSTRUCT {?s ?p ?o} WHERE { ?bookmark a cr:SparqlBookmark ; ?p ?o}",
      "queryType" : "construct"
   }
 }'



Blacklists and whitelists
=========================

Depending on the importance of the information, some properties can be skipped or kept.
A blacklist contains properties that should not be indexed with the data while a whitelist
contains all the properties that should be indexed with the data.

A 'proplist' can therefore be of two types: 'white' or 'black'. If the type is not provided,
the list is considered to be white.

The following query indexes only the rdf:type property of the resources.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : "CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}",
      "queryType" : "construct",
      "proplist" : ["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"],
      "listtype" : "white"
   }
 }'


Normalization
=============

This feature allows the users to rename properties or to state that two
properties are the same, for different namespaces.

'NormMap' contains pairs of property-replacement. The properties are replaced
with the given values and if one resource has both properties their values are
grouped in a list.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : "CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}",
      "queryType" : "construct",
      "normMap" : {
            "http://purl.org/dc/elements/1.1/format" : "format",
            "http://purl.org/dc/elements/1.1/type" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
            "http://example.org/pntology/typeOfData" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
      }
   }
 }'

The data indexed with the previous river will lack the property
http://purl.org/dc/elements/1.1/format, because it will be replaced with "format".
<<<<<<< HEAD
Moreover, all the values of the http://purl.org/dc/elements/1.1/type and
http://example.org/pntology/typeOfData properties of each resource will be grouped
under http://www.w3.org/1999/02/22-rdf-syntax-ns#type.

Security
========

Since ElasticSearch does not provide authentication or access control
functionalities, dropping or modifying indexes can be done without
authentication.

=======
Moreover, all the values of the http://purl.org/dc/elements/1.1/type and
http://example.org/pntology/typeOfData properties of each resource will be grouped
under http://www.w3.org/1999/02/22-rdf-syntax-ns#type.


Scheduling the harvest
======================

To schedule the data harvest just create a crontab with the desired interval. Cron
is a time-based job scheduler. It makes it possible to schedule commands or scripts
run periodically at fixed times, dates and intervals, through crontabs (cron table).
The basic format of a crontab consists of six fields, separated by spaces. These fields
must always be in the following order (with no empty fields):

::

 Minute Hour Day_of_Month Month Day_of_Week Command

The  accepted values for each field are:

* Minute: 0-59

* Hour: 0-23

* Day_of_Month: 1-31

* Month: 1-12 or Jan-Dec

* Day_of_Week: 0-6 or Sun-Sat

* Command: the command to run, including its parameters if any

The wildcard character replaces any possible value for the field it represents. It also
helps scheduling something to run every x times (minutes, hours, day, month, day of week)
with the syntax: "*\x".

In the example below, command is run every two months, on the 1st and 15th, at 20:00 (8:00 PM).

::

 # Minute   Hour   Day of Month       Month          Day of Week        Command
 # (0-59)  (0-23)     (1-31)    (1-12 or Jan-Dec)  (0-6 or Sun-Sat)
     0       20        1,15           */2               *           /{path}/command

The command to run should remove both the old river index and the indexed data, and add a new
index, as in the example below:

::

 curl -XDELETE 'localhost:9200/rdfdata'
 curl -XDELETE 'localhost:9200/_river/name_of_river'
 curl -XPUT 'localhost:9200/_river/name_of_river/_meta' -d '{
     "type" : "eeaRDF",
     "eeaRDF" : {
           ...
      }
 }'

Security
========

Since ElasticSearch does not provide authentication or access control
functionalities, dropping or modifying indexes can be done by anyone.
To keep the indexed information safe, the
`Jetty HTTP transport plugin <https://github.com/sonian/elasticsearch-jetty>`_
should be installed and configured.
>>>>>>> e8486779b90c8af1a0ada535d1207ae6a3f287fa

Source Code
===========

https://github.com/eea/eea.elasticsearch.river.rdf


Copyright and license
=====================

The Initial Owner of the Original Code is European Environment Agency (EEA).
All Rights Reserved.

The EEA ElasticSearch RDF River Plugin (the Original Code) is free software;
you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation;
either version 2 of the License, or (at your option) any later
version.
