# Spacebrew Client 0.1.0
This is a Java client API library for [Spacebrew](http://docs.spacebrew.cc/). Spacebrew allows flexible routing of simple messages between WebSocket based clients. Created by Axel Baumgartner for the [HCI & Usability Unit at the ICT&S Center, University of Salzburg, Austria](http://www.icts.sbg.ac.at).

Modified from [spacebrew-processing-library](http://labatrockwell.github.io/spacebrew-processing-library) by [Brett Renfer and Julio Terra](http://rockwellgroup.com/lab).

## Features
* Reworked implementation from the original Spacebrew Processing library
* Tries to connect until the server is online
* Automatically reconnects on disconnect
* Notifies of connection status via log
* Exposes onOpen(), onClose() and onError() events
* Allows to add or remove publishers and subscribers while connected
* Allows to use publishers and subscribers with the same name but differing types
* Ensures that incoming range messages lie within the allowed interval of [0,1023]
* Offers another way to implement publishers by objects that extend abstract publishers
* Offers another way to implement subscribers by objects that implement the subscriber interfaces
* Offers an abstract range subscriber with simple low-pass filtering and linear transformation of input values

## Contents
* SpacebrewClient.jar - The redistributable JAR file for the API.
* SpacebrewClient-sources.jar - The sources for the API.
* example - An example to demonstrate use of the API. 

## Minimum Required JDK
* Java 1.6
* Android 1.6 (API 4)

## API Dependencies
* [java_websocket](http://github.com/TooTallNate/Java-WebSocket)
* [json-simple-1.1.1](http://code.google.com/p/json-simple)
* [slf4j-api-1.7.2](http://www.slf4j.org)
* [slf4j-simple-1.7.2](http://www.slf4j.org) Required only if no other logger is used 

## Usage
See src/example/Example.java


## Support
Contact the author at <axel.baumgartner@sbg.ac.at>.


## License
See ``license.tx
