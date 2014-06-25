/*******************************************************************************
 * Copyright (c) 2014 Axel Baumgartner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Axel Baumgartner - initial API and implementation
 ******************************************************************************/
package at.ac.sbg.icts.spacebrew.sensor;

import java.util.HashMap;

import at.ac.sbg.icts.spacebrew.client.SpacebrewClient;
import at.ac.sbg.icts.spacebrew.client.SpacebrewMessage;
import at.ac.sbg.icts.spacebrew.client.publisher.BooleanPublisher;
import at.ac.sbg.icts.spacebrew.client.publisher.Publisher;
import at.ac.sbg.icts.spacebrew.client.publisher.RangePublisher;

/**
 * A <code>SpacebrewSensor</code> is used to publish data from a hardware sensor
 * to Spacebrew. A single hardware sensor (e.g. accelerometer) may offer
 * different sensor values (e.g. acceleration in three axis) which are then
 * represented by individual publishers. For the sensor to work the following
 * three conditions need to apply which are represented as boolean states:
 * <ol>
 * <li>Available: The hardware components the sensor needs to work are available
 * on the device this software runs on.</li>
 * <li>Enabled: This sensor has been enabled by the user.</li>
 * <li>Active: The sensor is available, enabled and currently sending data.</li>
 * </ol>
 * 
 * @author Axel Baumgartner
 */
public abstract class SpacebrewSensor
{
	/**
	 * The client through which messages are sent
	 */
	protected SpacebrewClient								client;

	/**
	 * Holds all publishers this sensor offers (name, (type, publisher)).
	 */
	protected HashMap<String, HashMap<String, Publisher>>	publishers	= new HashMap<String, HashMap<String, Publisher>>();

	/**
	 * True if the hardware components the sensor needs to work are available on
	 * the device this software runs on.
	 */
	protected boolean										available	= false;

	/**
	 * True if this sensor has been enabled by the user.
	 */
	private boolean											enabled		= false;

	/**
	 * True if the sensor is currently trying to publish data.
	 */
	protected boolean										active		= false;

	/**
	 * The name of this sensor
	 */
	private final String									name;

	/**
	 * Constructor.
	 * 
	 * @param client The client through which messages are published
	 * @param name The name of the sensor
	 */
	public SpacebrewSensor(SpacebrewClient client, String name)
	{
		this.client = client;
		this.name = name;
	}

	/**
	 * Adds a publisher to this sensor.
	 * 
	 * @param publisher The publisher to add
	 */
	public void addPublisher(Publisher publisher)
	{
		String publisherName = publisher.getName();

		if (!publishers.containsKey(publisherName))
		{
			publishers.put(publisherName, new HashMap<String, Publisher>());
		}

		publishers.get(publisherName).put(getType(publisher), publisher);
	}

	/**
	 * Removes a publisher from this sensor.
	 * 
	 * @param publisher The publisher to remove
	 */
	public void removePublisher(Publisher publisher)
	{
		String publisherName = publisher.getName();

		if (publishers.containsKey(publisherName))
		{
			publishers.get(publisherName).remove(getType(publisher));
		}
	}

	/**
	 * Infers the type of a given publisher.
	 * 
	 * @param publisher The publisher to infer the type from
	 * @return The type of the publisher (i.e.
	 *         <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 *         <code>SpacebrewMessage.TYPE_RANGE</code> or
	 *         <code>SpacebrewMessage.TYPE_STRING</code>)
	 */
	private String getType(Publisher publisher)
	{
		if (publisher instanceof BooleanPublisher)
		{
			return SpacebrewMessage.TYPE_BOOLEAN;
		}
		else if (publisher instanceof RangePublisher)
		{
			return SpacebrewMessage.TYPE_BOOLEAN;
		}
		else
		{
			return SpacebrewMessage.TYPE_BOOLEAN;
		}
	}

	/**
	 * Returns true if the components the sensor needs to work at all are
	 * available.
	 * 
	 * @return True if the sensor is available
	 */
	public boolean isAvailable()
	{
		return available;
	}

	/**
	 * Used to enable the sensor by the user.
	 */
	public void enable()
	{
		this.enabled = true;
	}

	/**
	 * Used to disable the sensor by the user.
	 */
	public void disable()
	{
		this.enabled = false;
	}

	/**
	 * @return True if the sensor was enabled by the user
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @return The name of this sensor
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return True if the sensor is active (i.e. currently sending data).
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * Activates the sensor to start sending data.
	 */
	public final void activate()
	{
		if (available && enabled && !active)
		{
			onActivate();
			this.active = true;
		}
	}

	/**
	 * Deactivates the sensor and stops sending data.
	 */
	public final void deactivate()
	{
		if (active)
		{
			onDeactivate();
			this.active = false;
		}
	}

	/**
	 * Holds the implementation for the activation of the sensor and starts
	 * sending data (e.g. connect to a network server, initialize hardware,
	 * register a software listener, ...).
	 */
	public abstract void onActivate();

	/**
	 * Holds the implementation for the deactivation of the sensor and stops
	 * sending data. (e.g. disconnect from a network server, release hardware,
	 * unregister a software listener, ...).
	 */
	public abstract void onDeactivate();
}
