==================================
Elasticsearch RDF River Plugin
==================================

Introduction
============
This River Plugin for Elasticsearch allows to harvest metadata from
SPARQL endpoints, TDB store or plain RDF files into Elasticsearch. It is provided as a
plugin.

This plugin is the modified version of the EEA RDF River Plugin which is available at https://github.com/eea/eea.elasticsearch.river.rdf. The plugin was modified to meet the University of Bergen Library's requirements. 

In this river, many options have been introduced including the support for harvesting data directly from `TDB <https://jena.apache.org/documentation/tdb/>`_ , ability to update only part of the indexed document and more. Please see the documentation for more details.


.. contents::

Installation
============

Prerequisites:

* ElasticSearch 1.x 

* Java 8 Runtime Environment or later

Binaries for this plugin are available at:

https://github.com/ubbdst/elasticsearch-river/releases

In order to install the plugin, you first need to have
`Elasticsearch <http://www.elasticsearch.org/download/>`_ installed. Just
download the latest release and extract it.

To install the RDF River plugin run from the previously extracted directory::

./bin/plugin --url https://github.com/eea/eea.elasticsearch.river.rdf/releases/download/$PLUGIN_VERSION/eea-rdf-river-plugin-$PLUGIN_VERSION.zip -i eea-rdf-river

Note:
 *raw.github.com* urls are deprecated and will be checked out from the source tree. Please use the url described above
 to install the plugin. You can find all of the legacy archives previously available in the source tree `here <https://github.com/eea/eea.elasticsearch.river.rdf/releases/download/v1.1/legacy-releases.zip>`_. 

Main features
=============

1. Indexing RDFs given by their URIs
2. Indexing triples retrieved from a SPARQL endpoint or from TDB, through SELECT queries
3. Indexing triples retrieved from a SPARQL endpoint or from TDB, through CONSTRUCT queries
4. Indexing triples retrieved from a SPARQL endpoint or from TDB, through DESCRIBE queries
5. Customizable index and type names
6. Blacklist of unnecessary properties
7. Whitelist of required properties
8. Normalization of properties from different namespaces
9. Normalization of missing properties

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
'uris' may contain any list of URIs.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "uris" : ["http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/measurementmethod/rdf"]
    }
 }'


From a SPARQL endpoint
++++++++++++++++++++++

The river is given a SPARQL endpoint and a list of queries. Each query response is indexed into ElasticSearch.
The SPARQL query can be a SELECT query or a CONSTRUCT query. All the queries are of the same type. 

The SELECT query should always require a triple (?s ?p ?o) where ?s is the subject,
?p is the predicate and ?o is the object. The names and order are required for relevant
results.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> SELECT ?s ?p ?o WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "select"
   }
 }'

CONSTRUCT queries are more simple.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : [
          "CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}",
          "CONSTRUCT {?s ?p ?o} WHERE { ?s a <http://www.eea.europa.eu/portal_types/AssessmentPart#AssessmentPart> ; ?p ?o}"
      ],
      "queryType" : "construct"
   }
 }'

DESCRIBE queries can be written as such:

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : [
        "DESCRIBE ?r WHERE { ?r a <http://www.eea.europa.eu/portal_types/AssessmentPart#AssessmentPart> }
      ],
      "queryType" : "describe"
   }
 }'

Note:
    DESCRIBE queries can produce larger results than other types of queries,
    making the river plugin run out of memory.
 
**Tips**: `See how to optimize your queries / avoid endpoint timeout <http://taskman.eionet.europa.eu/projects/zope/wiki/HowToWriteOptimalSPARQLQueries>`_


From TDB
++++++++++++++++++++++

Data can also be harvested directly from TDB storage. It is exactly the same as from SPARQL endpoint discussed above except you will have to specify tdbLocation instead of endpoint in the river settings. Note that the key tdbLocation holds the path to the TDB directory.


::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "tdbLocation" : "path/to/tdb",
      "query" : [
        "DESCRIBE ?r WHERE { ?r a <http://www.eea.europa.eu/portal_types/AssessmentPart#AssessmentPart> }
      ],
      "queryType" : "describe"
   }
 }'

From URIs, TDB and SPARQL endpoint
++++++++++++++++++++++++++++++++++

All supported parameters are optional. Moreover, it is possible to index metadata
from a SPARQL endpoint and several unrelated URIs.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "uris" : ["http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/measurementmethod/rdf"],
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "tdbLocation" : "/path/to/tdb"
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> CONSTRUCT {?s ?p ?o} WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "construct"
   }
 }'

Updating
===========
You can update a partial document to ElasticSearch without full re-indexing. 
Given a document ID, document will be merged to the existing document with this ID, if original document does not exist, no update will be performed and the DocumentMissingException will be thrown.

This is useful if you want to update only part of the document. For example by adding a new field to the existing document.

Updating a document can simply be done by setting the flag updateDocuments to true in the river settings.

Please see below:-

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "updateDocuments" : true,
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> SELECT ?s ?p ?o WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "select"
   }
 }'
 
By setting the flag updateDocuments to true in the river settings, 
you are telling the river to query and then merge the documents to the existing one based on their ID.





Other options
=============

There are several other options available for the index operation. They can be added no matter of the other settings.

includeResourceURI
++++++++++++++++++

Each resource is indexed into ElasticSearch with the _id property set to its URI. This is very convenient because it 
is well known that URIs are unique. Some applications however cannot extract the URI from the _id field, so whenever
"includeResourceUri" is set on "true", a new property is added to each resource: 
"http://www.w3.org/1999/02/22-rdf-syntax-ns#about", having the value equal to the resource's URI.

The default value for "includeResourceURI" is true.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "uris" : ["http://dd.eionet.europa.eu/vocabulary/aq/individualexceedances/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf"],
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> CONSTRUCT {?s ?p ?o} WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "construct",
      "includeResourceURI" : false
   }
 }'
 
Bulk Actions
++++++++++++

River index data to ElasticSearch in bulk, you can set this bulk size depending on your dataset and resources.
By default, bulk size is set to 100.  
 
You can set the bulk size in the river settings as follows:-
 
::

  curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
    "bulkActions" : 10000
   }
 }'
 
 Note that the key "bulkActions" holds long value.

language and addLanguage 
++++++++++++++++++++++++

When "addLanguage" is set on "true", all the languages of the String Literals will be included in the output of a 
new property, "language". If "language" is a required property, one that has to describe all the objects, a default 
language should be set for when there are no String Literals or they do not have languages defined. This can be done
when indexing the data by setting "language" to be the default language. 

The default value for "addLanguage" is true and for "language", "en".

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "uris" : ["http://dd.eionet.europa.eu/vocabulary/aq/individualexceedances/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf"],
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> CONSTRUCT {?s ?p ?o} WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "construct",
      "addLanguage" : true,
      "language" : "it"
   }
 }'
 
 
uriDescription
++++++++++++++

The value of each predicate (the object) can only be a Literal or a Resource. When it is a Resource (URI) it is 
very difficult to obtain information from it, if the information is not indexed in ElasticSearch. Whenever 
"uriDescription" is set, the URIs are replaced by the resource's label. The label is the first of the properties 
given as arguments for "uriDescription", for which the resource has an object.

Note: 
 We have excluded the possibility of getting labels from SPARQL endpoint because it was error-prone due to HTTP Exceptions.
 Currently, this feature is possible only if you are using TDB as your data storage. We have seen that using TDB to get labels    from a single JVM is somewhat efficient in the sense that you are capable of doing as many queries as possible in less than a    second without getting HTTP Exceptions or socket bind exceptions as contrasted to SPARQL endpoints.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "uris" : ["http://dd.eionet.europa.eu/vocabulary/aq/individualexceedances/rdf",
                "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/rdf"],
      "tdbLocation" : "/var/lib/tdb",
      "query" : ["PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX cr: <http://cr.eionet.europa.eu/ontologies/contreg.rdf#> CONSTRUCT {?s ?p ?o} WHERE { ?s a cr:SparqlBookmark ; ?p ?o}"],
      "queryType" : "construct",
      "addLanguage" : true,
      "uriDescription" : ["http://www.w3.org/2000/01/rdf-schema#label", "http://purl.org/dc/terms/title"]
   }
 }'

Note:
 "uriDescription" is used in Sync queries to *automatically* retrieve descrpition for resources.
 When using "uriDescription" without query optimization, the index speed will increase. A good practice when
 using this feature is:
 
 * Add the uriDescription fields in synchronization indices
 * Add the uriDescription fields in index creation queries *AND* rewrite your queries so the SPARQL endpoint
   responds with Literals rather than Resources:
::

 SELECT ?s ?p ?o WHERE { $COND }
 
can be rewritten as:

::
 
 SELECT ?s ?p ?o WHERE {
   {
     $COND . FILTER(isLiteral(?o))
   } UNION { 
     ?s ?p ?o1 .
     $COND -- applied on ?o1 instead of ?o
     ?o1 <http://purl.org/dc/terms/title> ?o
   }
 }
 
This optimization ensures that the query will return Literals which are indexed faster than Resources.


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
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}"],
      "queryType" : "construct",
      "proplist" : ["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"],
      "listtype" : "white"
   }
 }'
 
BlackMap
========

Sometimes the user might not be interested to index some obvious or useless information. 
A good example can be the situation in which all the classes have a single superclass. If all
the objects belong to this superclass, then there is no point in adding this information.

A blackMap contains all the pairs property - list of objects that are not meant to be indexed. 

::

 curl -XPUT 'localhost:9200/_river/asspart/_meta' -d '{
   "type": "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "queryType" : "construct",
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE { ?s a <http://www.eea.europa.eu/portal_types/AssessmentPart#AssessmentPart> . ?s ?p ?o}"],
      "blackMap" : {"http://www.w3.org/1999/02/22-rdf-syntax-ns#type":["Tracked File"]}
   }
 }'
 
WhiteMap
========

Sometimes the user might only be interested to index some information. A whiteMap contains 
all the pairs property - list of objects that are meant to be indexed. 

::

 curl -XPUT 'localhost:9200/_river/asspart/_meta' -d '{
   "type": "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "queryType" : "construct",
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE { ?s a <http://www.eea.europa.eu/portal_types/AssessmentPart#AssessmentPart> . ?s ?p ?o}"],
      "whiteMap" : {"http://www.w3.org/1999/02/22-rdf-syntax-ns#type":["Assessment Part"]}
   }
 }'
 
Normalization
=============

This feature allows the users to rename properties or objects or to state that two
of these are the same, even if their namespaces are different.

Properties Normalization
++++++++++++++++++++++++

'NormProp' contains pairs of property-replacement. The properties are replaced
with the given values and if one resource has both properties their values are
grouped in a list.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}"],
      "queryType" : "construct",
      "normProp" : {
            "http://purl.org/dc/elements/1.1/format" : "format",
            "http://purl.org/dc/elements/1.1/type" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
            "http://example.org/pntology/typeOfData" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
      }
   }
 }'

The data indexed with the previous river will lack the property
http://purl.org/dc/elements/1.1/format, because it will be replaced with "format".
Moreover, all the values of the http://purl.org/dc/elements/1.1/type and
http://example.org/pntology/typeOfData properties of each resource will be grouped
under http://www.w3.org/1999/02/22-rdf-syntax-ns#type.

Objects Normalization
+++++++++++++++++++++

'NormObj', similar with 'NormProp', contains pairs of object-replacement. Objects are 
replaced with given values no matter of the property whose value they represent.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}"],
      "queryType" : "construct",
      "normObj" : {
            "Organisation" : "Organization",
            "Quick Event" : "Event"
      }
   }
 }'

Missing Property Normalization
++++++++++++++++++++++++++++++

'NormMissing' contains pairs of property-default. If the property is missing
in a certain document, it will be indexed with the default value provided in
this dict.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "query" : ["CONSTRUCT {?s ?p ?o} WHERE {?s  a <http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat> ; ?p ?o}"],
      "queryType" : "construct",
      "normMissing" : {
            "http://purl.org/dc/elements/1.1/spatial" : "Other",
      }
   }
 }'


 
 
Synchronization with an endpoint
================================

It is possible to query an endpoint for the latest changes and only index these instead of 
all the resources. This can be specified by setting the value of 'indexType' to 'sync' instead
of 'full', which is the default one. A value for 'startTime' should be provided because the plugin 
queries the endpoint for updates that occured after that moment in time. In case no value is provided, 
the time of the last index operation will be considered. 

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "indexType" : "sync",
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "startTime" : "20131206T15:00:00"
   }
 }'
 
There are two possible settings for the sync river:
 * syncConditions
 * syncTimeProp
 
SyncConditions
++++++++++++++

This property allows the user to add extra filters when synchronizing with the endpoint. 
Therefore, the river will only index some information, updated after a point in time, instead
of all the triples. This property is very useful when only some triples should be indexed. 
The resource being indexed is always "?resource". 

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "indexType" : "sync",
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "syncConditions": "{{?resource a <http://www.eea.europa.eu/portal_types/DataFile#DataFile>} UNION {?resource a <http://www.eea.europa.eu/portal_types/Image#Image>}}"
   }
 }'
 
SyncTimeProp
++++++++++++

Different endpoints may have different properties to present the time when some triple is harvested. 
SyncTimeProp sets this property to some known URI so the sync river will only index those triples that
have a higher value for this property than the startTime value. 

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "indexType" : "sync",
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "syncTimeProp": "http://cr.eionet.europa.eu/ontologies/contreg.rdf#lastRefreshed"
   }
 }'
 
SyncOldData
+++++++++++

Sometimes some information is harvested and later on it is modified but it no longer fit into the 
first selection criteria. The initial resource wil no longer be needed into the dataset. However, 
due to the SyncConditions only resources that fit the initial (or new) conditions are synchronized,
so the old data should be deleted as well. When this property is set to True, the modified resources
that no longer match the conditions are deleted.

::

 curl -XPUT 'localhost:9200/_river/rdf_river/_meta' -d '{
   "type" : "eeaRDF",
   "eeaRDF" : {
      "indexType" : "sync",
      "endpoint" : "http://semantic.eea.europa.eu/sparql",
      "syncOldData": true
   }
 }'
 

 
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

