/*******************************************************************************
 * Copyright (c) 2014 Axel Baumgartner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Axel Baumgartner - initial API and implementation
 ******************************************************************************/
package at.ac.sbg.icts.spacebrew.client;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client implementation of the Spacebrew protocol. Connects to a Spacebrew
 * server via WebSocket, allows to send data via publishers
 * {@link #addPublisher(String, boolean)} and to receive data via subscribers
 * {@link #addSubscriber(String, String, String)}.
 * <p/>
 * Modified from <a
 * href="http://labatrockwell.github.io/spacebrew-processing-library/"
 * >spacebrew-processing-library</a> by <a
 * href="http://rockwellgroup.com/lab">Brett Renfer and Julio Terra<a/> .
 * <p/>
 * Dependencies:
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
public class SpacebrewClient implements WebSocketClientImplCallback
{
	/**
	 * Provides logging facilities.
	 */
	private Logger												log					= LoggerFactory
																							.getLogger(SpacebrewClient.class);

	/**
	 * The object that implements the callback methods.
	 */
	private SpacebrewClientCallback								callback;

	/**
	 * The WebSocket client used to communicate with the server.
	 */
	private WebSocketClientImpl									webSocketClient;

	/**
	 * The URI of the Spacebrew server to connect to.
	 */
	private String												serverUri;

	/**
	 * Name of your application as it will appear in the Spacebrew.
	 * administration.
	 */
	private String												name;

	/**
	 * A description of what your application does as it appears in the
	 * Spacebrew administration.
	 */
	private String												description			= "";

	/**
	 * The URI of the Spacebrew server currently connected to.
	 */
	private String												currentServerUri;

	/**
	 * True if this client is currently connected to a Spacebrew server.
	 */
	private boolean												connected;

	/**
	 * Holds all publishers this client offers (publisherName, (type,
	 * messageTemplate)).
	 */
	private HashMap<String, HashMap<String, SpacebrewMessage>>	publishers			= new HashMap<String, HashMap<String, SpacebrewMessage>>();

	/**
	 * Lists which subscribers this client offers (subscriberName, (type,
	 * messageTemplate)).
	 */
	private HashMap<String, HashMap<String, SpacebrewMessage>>	subscribers			= new HashMap<String, HashMap<String, SpacebrewMessage>>();

	/**
	 * Holds the callback methods for all subscribers this client offers
	 * (subscriberName, (methodName, method)).
	 */
	private HashMap<String, HashMap<String, Method>>			subscriberMethods	= new HashMap<String, HashMap<String, Method>>();

	/**
	 * Holds the callback objects for all subscribers this client offers
	 * (subscriberName, callback).
	 */
	private HashMap<String, HashMap<String, Object>>			subscriberObjects	= new HashMap<String, HashMap<String, Object>>();

	/**
	 * The time in milliseconds after which a lost connection is reopened. When
	 * 0 no reconnect will happen.
	 */
	private long												timeout				= 1000;

	/**
	 * True while the client is disconnecting from our side.
	 */
	private boolean												disconnecting;

	/**
	 * True while the client is trying to connect.
	 */
	private boolean												connecting;

	/**
	 * True while the client is reconnecting.
	 */
	private boolean												reconnecting;

	/**
	 * @param callback The object that will receive messages via callback
	 *            methods.
	 * @param serverUri The complete URI of the Spacebrew server to connect to
	 * @param name The name of your application as it will appear in the
	 *            Spacebrew administration.
	 */
	public SpacebrewClient(SpacebrewClientCallback callback, String serverUri, String name)
	{
		this.callback = callback;
		this.serverUri = serverUri;
		this.name = name;
	}

	/**
	 * @param callback The object that will receive messages via callback
	 *            methods.
	 * @param serverUri The complete URI of the Spacebrew server to connect to
	 * @param name The name of your application as it will appear in the
	 *            Spacebrew administration.
	 * @param description The description of your application as it will appear
	 *            in the Spacebrew administration
	 */
	public SpacebrewClient(SpacebrewClientCallback callback, String serverUri, String name, String description)
	{
		this.callback = callback;
		this.serverUri = serverUri;
		this.name = name;
		this.description = description;
	}

	/**
	 * Sets the URI of the server to connect to. If the client is already
	 * connected, this URI will be used on the next connection attempt.
	 * 
	 * @param serverUri The complete URI of the server to connect to on the next
	 *            connection attempt
	 */
	public void setServerUri(String serverUri)
	{
		this.serverUri = serverUri;
	}

	/**
	 * @return The complete URI of the server to connect to on the next
	 *         connection attempt
	 */
	public String getServerURI()
	{
		return serverUri;
	}

	/**
	 * @return The URI of the server currently connected to
	 */
	public String getCurrentServerURI()
	{
		return currentServerUri;
	}

	/**
	 * @param name The name of this client as it will appear in the Spacebrew
	 *            administration
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return The name of this client as it will appear in the Spacebrew
	 *         administration
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param description The description of this client as it will appear in the Spacebrew
	 *            administration
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return The description of this client as it will appear in the Spacebrew
	 *         administration
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the timeout the client should wait before reconnecting in
	 * milliseconds. Set to 0 to never reconnect.
	 * 
	 * @param timeout The timeout in milliseconds
	 */
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}

	/**
	 * @return The time the client should wait before reconnecting in
	 *         milliseconds. 0 means it never reconnects.
	 */
	public long getTimeout()
	{
		return timeout;
	}

	/**
	 * Connects to the Spacebrew server and updates the server with the
	 * currently registered subscribers and publishers this client offers.
	 */
	public void connect()
	{
		if (!connected)
		{
			try
			{
				log.info("Connecting to server with URI: {}", serverUri);
				webSocketClient = new WebSocketClientImpl(this, serverUri);
				webSocketClient.connect();
				currentServerUri = serverUri;
			}
			catch (Exception e)
			{
				log.error("Could not connect to server with URI: {}", serverUri);
				log.debug("Exception: " + e.getMessage());
			}
		}
	}

	/**
	 * Closes the connection to the Spacebrew server.
	 */
	public void disconnect()
	{
		if (connected)
		{
			log.info("Disconnecting from server with URI: {}", currentServerUri);
			disconnecting = true;
			webSocketClient.close();
			webSocketClient = null;
		}
	}

	/**
	 * Disconnects and immediately reconnects to the Spacebrew server.
	 */
	public void reconnect()
	{
		reconnecting = true;
		disconnect();
	}

	/**
	 * Updates the server about the current subscribers and publishers this
	 * client offers. Called automatically when a connection was successfully
	 * opened.
	 */
	@SuppressWarnings("unchecked")
	private void sendConfig()
	{
		if (!connected)
		{
			return;
		}

		JSONObject configPart = new JSONObject();
		configPart.put("name", name);
		configPart.put("description", description);

		JSONArray publishes = new JSONArray();

		HashMap<String, SpacebrewMessage> temp;
		SpacebrewMessage message;
		SortedSet<String> keys;
		SortedSet<String> typeKeys;

		keys = new TreeSet<String>(publishers.keySet());
		for (String key : keys)
		{
			temp = publishers.get(key);

			typeKeys = new TreeSet<String>(temp.keySet());
			for (String typeKey : typeKeys)
			{
				message = temp.get(typeKey);

				JSONObject publish = new JSONObject();
				publish.put("name", message.name);
				publish.put("type", message.type);
				publish.put("default", message.defaultValue);

				publishes.add(publish);
			}
		}

		JSONObject publishPart = new JSONObject();
		publishPart.put("messages", publishes);
		configPart.put("publish", publishPart);

		JSONArray subscribes = new JSONArray();

		keys = new TreeSet<String>(subscribers.keySet());
		for (String key : keys)
		{
			temp = subscribers.get(key);

			typeKeys = new TreeSet<String>(temp.keySet());
			for (String typeKey : typeKeys)
			{
				message = temp.get(typeKey);
				JSONObject subscribe = new JSONObject();
				subscribe.put("name", message.name);
				subscribe.put("type", message.type);

				subscribes.add(subscribe);
			}
		}

		JSONObject subscribePart = new JSONObject();
		subscribePart.put("messages", subscribes);
		configPart.put("subscribe", subscribePart);

		JSONObject configMessage = new JSONObject();
		configMessage.put("config", configPart);

		send(configMessage);
	}

	/**
	 * Creates a boolean publisher and adds it to <code>publishers</code>.
	 * Updates the server about the new publisher if the client is connected.
	 * 
	 * @param name The name of the publisher
	 * @param defaultValue The default starting value
	 */
	public void addPublisher(String name, boolean defaultValue)
	{
		addPublisher(name, SpacebrewMessage.TYPE_BOOLEAN, defaultValue + "");
	}

	/**
	 * Creates a range publisher and adds it to <code>publishers</code>. Updates
	 * the server about the new publisher if the client is connected.
	 * 
	 * @param name The name of the publisher
	 * @param defaultValue The default starting value
	 */
	public void addPublisher(String name, int defaultValue)
	{
		addPublisher(name, SpacebrewMessage.TYPE_RANGE, defaultValue + "");
	}

	/**
	 * Creates a String publisher and adds it to <code>publishers</code>.
	 * Updates the server about the new publisher if the client is connected.
	 * 
	 * @param name The name of the publisher
	 * @param defaultValue The default starting value
	 */
	public void addPublisher(String name, String defaultValue)
	{
		addPublisher(name, SpacebrewMessage.TYPE_STRING, defaultValue);
	}

	/**
	 * Creates a publisher and adds it to <code>publishers</code>. Updates the
	 * server about the new publisher if the client is connected.
	 * 
	 * @param name The name of the publisher
	 * @param type The type of the publisher (i.e.
	 *            <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 *            <code>SpacebrewMessage.TYPE_RANGE</code> or
	 *            <code>SpacebrewMessage.TYPE_STRING</code>)
	 * @param defaultValue The default starting value
	 */
	public void addPublisher(String name, String type, String defaultValue)
	{
		SpacebrewMessage message = new SpacebrewMessage();
		message.name = name;
		message.type = type;
		message.defaultValue = defaultValue;

		if (!publishers.containsKey(name))
		{
			publishers.put(name, new HashMap<String, SpacebrewMessage>());
		}
		publishers.get(name).put(type, message);

		sendConfig();
		log.debug("Added publisher with name \"{}\", type \"{}\" and default value \"{}\".", name, type, defaultValue);
	}

	/**
	 * Adds a subscriber that uses the generic callback object. Updates the
	 * server about the new subscriber if the client is connected.
	 * 
	 * @param name The name of the subscriber
	 * @param type The type of the subscriber (i.e.
	 *            <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 *            <code>SpacebrewMessage.TYPE_RANGE</code> or
	 *            <code>SpacebrewMessage.TYPE_STRING</code>)
	 * @param methodName The name of the method in the callback object
	 */
	public void addSubscriber(String name, String type, String methodName)
	{
		SpacebrewMessage message = new SpacebrewMessage();
		message.name = name;
		message.type = type.toLowerCase();

		if (!subscribers.containsKey(name))
		{
			subscribers.put(name, new HashMap<String, SpacebrewMessage>());
		}
		subscribers.get(name).put(type, message);

		Method method = null;
		if (type.equals(SpacebrewMessage.TYPE_BOOLEAN))
		{
			try
			{
				method = callback.getClass().getMethod(methodName, new Class[] { boolean.class });
			}
			catch (Exception e)
			{
				log.error(
						"Could not add subscriber with name \"{}\" and type \"{}\", callback does not implement method \"{}\"!",
						name, type, methodName);
			}
		}
		else if (type.equals(SpacebrewMessage.TYPE_RANGE))
		{
			try
			{
				method = callback.getClass().getMethod(methodName, new Class[] { int.class });
			}
			catch (Exception e)
			{
				log.error(
						"Could not add subscriber with name \"{}\" and type \"{}\", callback does not implement method \"{}\"!",
						name, type, methodName);
			}
		}
		else if (type.equals(SpacebrewMessage.TYPE_STRING))
		{
			try
			{
				method = callback.getClass().getMethod(methodName, new Class[] { String.class });
			}
			catch (Exception e)
			{
				log.error(
						"Could not add subscriber with name \"{}\" and type \"{}\", callback does not implement method \"{}\"!",
						name, type, methodName);
			}
		}

		if (method != null)
		{
			if (!subscriberMethods.containsKey(name))
			{
				subscriberMethods.put(name, new HashMap<String, Method>());
			}

			subscriberMethods.get(name).put(type, method);

			sendConfig();
			log.debug("Added subscriber with name \"{}\", type \"{}\" and callback method \"{}\".", name, type,
					method.getName());
		}
	}

	/**
	 * Adds a boolean subscriber that uses a specific callback object. Updates
	 * the server about the new subscriber if the client is connected.
	 * 
	 * @param name The name of the subscriber
	 * @param callback The callback object for boolean messages
	 */
	public void addSubscriber(String name, BooleanSubscriber callback)
	{
		addSubscriber(name, SpacebrewMessage.TYPE_BOOLEAN, callback);
	}

	/**
	 * Adds a range subscriber that uses a specific callback object. Updates the
	 * server about the new subscriber if the client is connected.
	 * 
	 * @param name The name of the subscriber
	 * @param callback The callback object forrange messages
	 */
	public void addSubscriber(String name, RangeSubscriber callback)
	{
		addSubscriber(name, SpacebrewMessage.TYPE_RANGE, callback);
	}

	/**
	 * Adds a string subscriber that uses a specific callback object. Updates
	 * the server about the new subscriber if the client is connected.
	 * 
	 * @param name The name of the subscriber
	 * @param callback The callback object for string messages
	 */
	public void addSubscriber(String name, StringSubscriber callback)
	{
		addSubscriber(name, SpacebrewMessage.TYPE_STRING, callback);
	}

	/**
	 * Adds a subscriber which uses its own callback object.
	 * 
	 * @param name The name of the subscriber
	 * @param type The type of the subscriber (i.e.
	 *            <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 *            <code>SpacebrewMessage.TYPE_RANGE</code> or
	 *            <code>SpacebrewMessage.TYPE_STRING</code>)
	 * @param subscriber The callback object
	 */
	private void addSubscriber(String name, String type, Object subscriber)
	{
		SpacebrewMessage message = new SpacebrewMessage();
		message.name = name;
		message.type = type;

		if (!subscribers.containsKey(name))
		{
			subscribers.put(name, new HashMap<String, SpacebrewMessage>());
		}
		subscribers.get(name).put(type, message);

		if (!subscriberObjects.containsKey(name))
		{
			subscriberObjects.put(name, new HashMap<String, Object>());
		}
		subscriberObjects.get(name).put(type, subscriber);

		sendConfig();
		log.debug("Added subscriber with name \"{}\" and type \"{}\".", name, type);
	}

	/**
	 * Removes a specified publisher.
	 * 
	 * @param name The name of the publisher to remove
	 * @param type The type of the publisher to remove
	 */
	public void removePublisher(String name, String type)
	{
		if (publishers.containsKey(name))
		{
			publishers.get(name).remove(type);

			sendConfig();
			log.debug("Removed publisher with name \"{}\" and type \"{}\".", name, type);
		}
	}

	/**
	 * Removes a specified subscriber.
	 * 
	 * @param name The name of the subscriber to remove
	 * @param type The type of the subscriber to remove
	 */
	public void removeSubscriber(String name, String type)
	{
		if (subscribers.containsKey(name))
		{
			subscribers.get(name).remove(type);

			if (subscriberMethods.containsKey(name))
			{
				subscriberMethods.get(name).remove(type);
			}

			if (subscriberObjects.containsKey(name))
			{
				subscriberObjects.get(name).remove(type);

				sendConfig();
				log.debug("Removed subscriber with name \"{}\", type \"{}\".", name, type);
			}
		}
	}

	/**
	 * Sends a boolean message from a specified publisher.
	 * 
	 * @param name The name of the publisher
	 * @param value The value of the message
	 */
	public void send(String name, boolean value)
	{
		send(name, SpacebrewMessage.TYPE_BOOLEAN, value + "");
	}

	/**
	 * Sends a range message from a specified publisher.
	 * 
	 * @param name The name of the publisher
	 * @param value The value of the message
	 */
	public void send(String name, int value)
	{
		send(name, SpacebrewMessage.TYPE_RANGE, value + "");
	}

	/**
	 * Sends a string message from a specified publisher.
	 * 
	 * @param name The name of the publisher
	 * @param value The value of the message
	 */
	public void send(String name, String value)
	{
		send(name, SpacebrewMessage.TYPE_STRING, value);
	}

	/**
	 * Sends a message with a specified type from a specified publisher.
	 * 
	 * @param name The name of the publisher
	 * @param type The type of the subscriber (i.e.
	 *            <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 *            <code>SpacebrewMessage.TYPE_RANGE</code> or
	 *            <code>SpacebrewMessage.TYPE_STRING</code>)
	 * @param value The value of the message
	 */
	@SuppressWarnings("unchecked")
	public void send(String name, String type, String value)
	{
		if (publishers.containsKey(name))
		{
			JSONObject messagePart = new JSONObject();

			messagePart.put("clientName", this.name);
			messagePart.put("name", name);
			messagePart.put("type", type);
			messagePart.put("value", value);

			JSONObject message = new JSONObject();
			message.put("message", messagePart);

			send(message);
		}
		else
		{
			log.error("Could not send message, no publisher with name \"{}\" and type \"{}\" has been added!", name,
					type);
		}
	}

	/**
	 * Sends a JSON message to the server.
	 * 
	 * @param message The message to send
	 */
	private void send(JSONObject message)
	{
		if (connected)
		{
			webSocketClient.send(message.toString());
		}
		else
		{
			log.warn("Could not send message, not connected!");
		}
	}

	/**
	 * Callback method for the <code>WebsocketClient</code> object.
	 */
	@Override
	public void onOpen()
	{
		connected = true;
		connecting = false;

		log.info("Connection opened to server with URI: {}", currentServerUri);

		sendConfig();
		callback.onOpen();
	}

	/**
	 * Callback method for the <code>WebsocketClient</code> object.
	 */
	@Override
	public void onClose()
	{
		if (connected)
		{
			connected = false;
			disconnecting = false;
			log.info("Connection closed to server with URI: {}", currentServerUri);
		}

		if (reconnecting)
		{
			reconnecting = false;
			connect();
		}

		if (!disconnecting && timeout > 0)
		{
			if (connecting)
			{
				log.error("Could not connect to server with URI: {}", currentServerUri);
			}

			try
			{
				Thread.sleep(timeout);
				connect();
			}
			catch (InterruptedException ex)
			{
				// ignore
			}
		}
	}

	/**
	 * Callback method for the <code>WebsocketClient</code> object.
	 * 
	 * @param string The received message
	 */
	@Override
	public void onMessage(String string)
	{
		Object temp = JSONValue.parse(string);
		JSONObject container = (JSONObject) temp;

		JSONObject message = (JSONObject) container.get("message");

		String name = (String) message.get("name");
		String type = (String) message.get("type");

		if (subscriberMethods.containsKey(name))
		{
			try
			{
				Method method = subscriberMethods.get(name).get(type);

				try
				{
					if (type.equals(SpacebrewMessage.TYPE_BOOLEAN))
					{
						method.invoke(callback, Boolean.parseBoolean((String) message.get("value")));
					}
					else if (type.equals(SpacebrewMessage.TYPE_RANGE))
					{
						method.invoke(callback, sanitizeRangeMessage(message));
					}
					else if (type.equals(SpacebrewMessage.TYPE_STRING))
					{
						method.invoke(callback, (String) message.get("value"));
					}
				}
				catch (Exception e)
				{
					log.error(
							"Could not pass incoming spacebrew message to callback, an exception occured while calling the method for subscriber with name \"{}\" and type \"{}\"",
							name, type);
					log.error("Exception: {}", e.getCause());
				}
			}
			catch (Exception e)
			{
				log.error(
						"Could not pass incoming spacebrew message to callback, method for subscriber with name \"{}\" and type \"{}\" not available in callback!",
						name, type);
			}
		}

		if (subscriberObjects.containsKey(name))
		{
			Object subscriber = null;

			try
			{
				subscriber = subscriberObjects.get(name).get(type);
				if (type.equals(SpacebrewMessage.TYPE_BOOLEAN))
				{
					((BooleanSubscriber) subscriber).receive(Boolean.parseBoolean((String) message.get("value")));
				}
				else if (type.equals(SpacebrewMessage.TYPE_RANGE))
				{

					((RangeSubscriber) subscriber).receive(sanitizeRangeMessage(message));
				}
				else if (type.equals(SpacebrewMessage.TYPE_STRING))
				{
					((StringSubscriber) subscriber).receive((String) message.get("value"));
				}
			}
			catch (Exception e)
			{
				log.error(
						"Could not pass message to callback, exception occured while passing message to subscriber with name \"{}\"",
						name);
				log.debug("Exception: {}", e);
			}
		}
	}

	/**
	 * Callback method for <code>webSocketClient</code>.
	 * 
	 * @param exception The <code>Exception</code> that caused the error
	 */
	@Override
	public void onError(Exception exception)
	{
		log.error("Connection error occured!");
		log.debug("Exception: {}", exception);
	}

	/**
	 * @return True if this client is connected to a server
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Takes an incoming range message and produces an integer value in the
	 * interval [0,1023]. The Spacebrew server does not check the messages for
	 * the correct format, thus we check and sanitize it here.
	 * 
	 * @param message The unsanitized incoming range message
	 * @return The sanitized <code>int</code> value
	 */
	private int sanitizeRangeMessage(JSONObject message)
	{
		int value = 0;

		try
		{
			value = Integer.parseInt((String) message.get("value"));
		}
		catch (NumberFormatException e)
		{
			// ignore
		}

		if (value > 1023)
		{
			value = 1023;
		}
		else if (value < 0)
		{
			value = 0;
		}

		return value;
	}
}
