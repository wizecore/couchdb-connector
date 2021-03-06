# CouchDB Anypoint Connector

Allows operations in Apache CouchDB (http://couchdb.apache.org).
Uses lightcouch library underneath (http://www.lightcouch.org).

## Supported operations:

* __findById__ (find document by CouchDB id)
* __listView__ (list documents in view, selecting by key or by range)
* __remove__ (removes document by CouchDB id or specified document)
* __findByKey__ (finds document in view by key)
* __save__ (adds or modifies existing document)

# Mule supported versions

Mule 3.5.X CE (tested)
Other version may be supported.

# CouchDB supported versions

Any version will do, starting with 1.0. 
Referer to http://couchdb.apache.org for more information and supported features with each release.

# Installation 

For beta connectors you can download the source code.

## Source code installation: 

1. Add devkit to Anypoint Studio
2. Add project to Anypoint studio
3. Right click on project, Anypoint Connector -> Install or update
4. Restart studio when asked
5. Use in your flows

## Update site installation:

1. Download update site from releases.
2. Open Help -> Install new software and specify update site to install in studio
3. Restart studio
4. Use in your flows

## Usage in standalone Mule ESB

Take connector ZIP from releases and put into plugins directory in mule-standalone.

# Usage

Consult documentation bundled with plugin.
See doc/ folder or live at https://github.com/wizecore/couchdb-connector/tree/master/doc.

# Reporting Issues

We use GitHub Issues for tracking issues with this connector. 

You can report new issues at this link https://github.com/wizecore/couchdb-connector/issues.

(C) 2014-2015 Wizecore.
