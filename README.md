# Spacebrew Client 0.2.1
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

## Minimum Required JDK
* Java 1.6
* Android 1.6 (API 4)

## API Dependencies
* [java_websocket](http://github.com/TooTallNate/Java-WebSocket)
* [json-simple-1.1.1](http://code.google.com/p/json-simple)
* [slf4j-api-1.7.2](http://www.slf4j.org)
* [slf4j-simple-1.7.2](http://www.slf4j.org) Required only if no other logger is used 

## Usage
For detailed examples please take a look at [src/test/java/Example.java](https://github.com/daaxel/SpacebrewClient/blob/master/src/test/java/Example.java). A most basic example class using the library may look like this:

```java

	import at.ac.sbg.icts.spacebrew.client.*;

	public class Example implements SpacebrewClientCallback
	{
		public void main(String[] args)
		{
			SpacebrewClient client = new SpacebrewClient(this, "ws://sandbox.spacebrew.cc:9000", "SpacebrewClient", "A simple Java client");
			client.connect();
			client.addPublisher("output","");
			client.publish("string", "Hello world!");
   			client.addSubscriber("input", SpacebrewMessage.TYPE_STRING, "receive");
		}
        
		public void receive(String message)
		{
			System.out.println("Received: " + message);
		}
        
		@override
		public void onOpen()
		{
			System.out.println("Connection to server opened.");
		}

		@override
		public void onError()
		{
			System.out.println("Error occurred.");
		}
        
		@override
		public void onClose()
		{
			System.out.println("Connection to server closed.");
		}
	}

```
## FAQ

If you get the error ``"Could not pass incoming spacebrew message to callback, exception occurred 
while calling callback method for subscriber... "`` then the code you wrote when receiving a message as 
a subscriber throws an exception. Check the method you specified as callback. If you configure slf4j to use the debug log level, you will be 
able to see the stacktrace of the cause. If you use SimpleLogger you can do this quickly by adding this line to your code:

```java
System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
```
 



## Support
Contact the author at <axel.baumgartner@sbg.ac.at>.

## License
See [LICENSE.txt](https://github.com/daaxel/SpacebrewClient/blob/master/LICENSE.txt)
