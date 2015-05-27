/*******************************************************************************
 * Copyright (c) 2014 Axel Baumgartner. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Lesser Public License v2.1 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * Contributors: Axel Baumgartner - initial API and implementation
 ******************************************************************************/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.sbg.icts.spacebrew.client.SpacebrewClient;
import at.ac.sbg.icts.spacebrew.client.SpacebrewClientCallback;
import at.ac.sbg.icts.spacebrew.client.SpacebrewMessage;
import at.ac.sbg.icts.spacebrew.client.publisher.BooleanPublisher;
import at.ac.sbg.icts.spacebrew.client.publisher.RangePublisher;
import at.ac.sbg.icts.spacebrew.client.publisher.StringPublisher;

/**
 * Demonstrates how to use {@link SpacebrewClient}. Every second new data is
 * sent. The boolean publisher "switch" alternates between true/false, the range
 * publishers "counter" count from 0 to 1023 and the string publisher "time"
 * sends the current time. The boolean publisher "boolean" repeats the output
 * from the "switch" publisher and the string publisher "string" repeats the
 * output from the "time" publisher. Finally the "range" publisher sends a
 * random, low-pass filtered value. Connect the publishers and subscribers with
 * each other to quickly see what the publishers send and the subscribers
 * receive. Dependencies:
 * <ul>
 * <li><a href="http://github.com/TooTallNate/Java-WebSocket">java_websocket</a>
 * </li>
 * <li><a href="http://code.google.com/p/json-simple/">json-simple-1.1.1</a></li>
 * <li><a href="http://www.slf4j.org">slfwj-api-1.7.2</a></li>
 * <li><a href="http://www.slf4j.org">slf4j-simple-1.7.2</a> (only required if
 * no other Logger is used)</li>
 * </ul>
 * 
 * @author Axel Baumgartner
 */
public class Example implements SpacebrewClientCallback
{
	// Provides logging facilities.
	private final Logger			log	= LoggerFactory.getLogger(SpacebrewClient.class);

	// The client instance we will send and receive data from
	private final SpacebrewClient	client;

	// Some publishers demonstrating the use of abstract publishers
	BooleanPublisher				myBooleanPublisher;
	RangePublisher					myRangePublisher;
	StringPublisher					myStringPublisher;

	/**
	 * Starts the Spacebrew client.
	 * 
	 * @param args Unused
	 */
	public static void main(String args[])
	{
		new Example();
	}

	/**
	 * Starts a SpacebrewClient, adds exemplary subscribers and publishers and
	 * connects the client to the server.
	 */
	public Example()
	{
		// String serverUri = "ws://sandbox.spacebrew.cc:9000";
		String serverUri = "ws://spacebrew.icts.sbg.ac.at:9000";

		/*
		 * Create a SpacebrewClient instance. Does not connect to the server by
		 * itself.
		 */
		client = new SpacebrewClient(this, serverUri, "Example Java Spacebrew Client",
				"An example client with fake subscribers and simple publishers.");

		// add some subscribers
		client.addSubscriber("switch", SpacebrewMessage.TYPE_BOOLEAN, "switchInput");
		client.addSubscriber("counter", SpacebrewMessage.TYPE_RANGE, "counterInput");
		client.addSubscriber("time", SpacebrewMessage.TYPE_STRING, "timeInput");

		/**
		 * This subscriber demonstrates that subscribers can have the same name
		 * but differing types.
		 */
		client.addSubscriber("counter", SpacebrewMessage.TYPE_STRING, "counterStringInput");

		client.addPublisher("switch", false);
		client.addPublisher("counter", 0);
		client.addPublisher("time", "");

		/**
		 * This publisher demonstrates that publishers can have the same name
		 * but differing types.
		 */
		client.addPublisher("counter", "");

		/**
		 * Actually connect to the server and start publishing and subscribing
		 * for data.
		 */
		client.connect();
	}

	Object	test	= null;

	/**
	 * Called by SpacebrewClient as a callback method for the "switch"
	 * subscriber.
	 * 
	 * @param input The received boolean message
	 */
	public void switchInput(boolean input)
	{
		log.info("The switch turns: " + (input ? "on" : "off"));
		log.info("" + test.getClass());
	}

	/**
	 * Called by {@code SpacebrewClient} as a callback method for the
	 * "counter" subscriber with type range.
	 * 
	 * @param input The received range message
	 */
	public void counterInput(int input)
	{
		log.info("The counter counts to: " + input);
	}

	/**
	 * Called by {@code SpacebrewClient} as a callback method for the
	 * "counter" subscriber with type string.
	 * 
	 * @param input The received string message
	 */
	public void counterStringInput(String input)
	{
		log.info("The counter says: " + input);
	}

	/**
	 * Called by {@code SpacebrewClient} as a callback method for the
	 * "time" subscriber.
	 * 
	 * @param input The received string message
	 */
	public void timeInput(String input)
	{
		log.info("The time is now: " + input);
	}

	/**
	 * Callback method for the {@code SpacebrewClient} object. Will be
	 * called when the connection to the server has been established
	 * successfully.
	 */
	@Override
	public void onOpen()
	{
		log.info("Connection opened.");

		/**
		 * These subscribers demonstrate, that subscribers can be added even
		 * after the connection has been established.
		 */
		MyBooleanSubscriber myBooleanSubscriber = new MyBooleanSubscriber();
		client.addSubscriber("myBooleanSubscriber", myBooleanSubscriber);

		MyRangeSubscriber myRangeSubscriber = new MyRangeSubscriber();
		client.addSubscriber("myRangeSubscriber", myRangeSubscriber);

		MyStringSubscriber myStringSubscriber = new MyStringSubscriber();
		client.addSubscriber("myStringSubscriber", myStringSubscriber);

		/**
		 * The next publishers demonstrate, that publishers can be added even
		 * after the connection has been established.
		 */

		// will repeat the output from the "switch" publisher
		myBooleanPublisher = new BooleanPublisher("myBooleanPublisher", false, client);

		/**
		 * This publisher demonstrates the use of the linear transformation and
		 * low-pass filter in {@code RangePublisher}
		 */
		myRangePublisher = new RangePublisher("myRangePublisher", 0, client);
		myRangePublisher.setMinValue(0);
		myRangePublisher.setMaxValue(100);
		myRangePublisher.activateLowPassFilter();

		/*
		 * A small alpha value for the low-pass filter creates high smoothing.
		 * Because we smooth a random value, the output value will always try to
		 * approach the middle of the range interval [0,1023], which is 512.
		 */
		myRangePublisher.setLowPassAlpha(0.2f);

		// will repeat the output from the "time" publisher
		myStringPublisher = new StringPublisher("myStringPublisher", "", client);

		// TODO add examples for SpacebrewSensorManager and SpacebrewSensor

		// Do your stuff in a different thread to not block the main thread
		// (with a never ending loop like in this example).
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				loop();
			}
		};
		thread.start();
	}

	/**
	 * Callback method for the {@code SpacebrewClient} object. Will be
	 * called when the connection to the server has been closed.
	 */
	@Override
	public void onClose()
	{
		log.info("Connection closed.");
	}

	/**
	 * Callback method for the {@code SpacebrewClient} object. Will be
	 * called when an exception force closed the connection to the server.
	 */
	@Override
	public void onError()
	{
		log.info("An error occured.");
	}

	/**
	 * When the connection has been established, we send some values until the
	 * program terminates.
	 */
	private void loop()
	{
		// used for the "switch" publisher
		boolean booleanValue = false;

		// used for the "counter" publisher
		int rangeValue = 0;

		/**
		 * Loop modeled after http://www.java-gaming.org/index.php?topic=24220.0
		 */
		final double hertz = 1;
		final double NANO_SECOND = 1000000000;

		final double TIME_BETWEEN_UPDATES = NANO_SECOND / hertz;

		double now = System.nanoTime();
		double lastUpdateTime;

		while (true)
		{
			lastUpdateTime = System.nanoTime();

			if (client.isConnected())
			{
				client.publish("switch", booleanValue);
				myBooleanPublisher.publish(booleanValue);
				booleanValue = !booleanValue;

				client.publish("counter", rangeValue);
				client.publish("counter", Integer.toBinaryString(rangeValue));

				rangeValue++;
				if (rangeValue > 1023)
				{
					rangeValue = 0;
				}

				String time = System.currentTimeMillis() + "";
				client.publish("time", time);
				myStringPublisher.publish(time);

				// create a random value in the interval [0,100]
				int randomValue = (int) (Math.random() * 100);

				// the random value will be linear transformed to the interval
				// [0,1023] and a low-pass filter is used to smooth the output
				myRangePublisher.publish(randomValue);
			}

			// Yield until it has been at least the target time between updates
			// This saves the CPU from hogging.
			while (now - lastUpdateTime < TIME_BETWEEN_UPDATES)
			{
				Thread.yield();

				// This stops the app from consuming all your CPU. It makes this
				// slightly less accurate, but is worth it. You can remove this
				// line and it will still work (better),
				// your CPU just climbs on certain OSes. FYI on some OS's this
				// can cause pretty bad stuttering.
				try
				{
					Thread.sleep(1);
				}
				catch (Exception e)
				{
					// ignore
				}

				now = System.nanoTime();
			}
		}
	}
}
