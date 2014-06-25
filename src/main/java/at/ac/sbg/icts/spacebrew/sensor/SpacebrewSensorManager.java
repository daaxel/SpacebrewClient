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

/**
 * Collects <link>SpacebrewSensor</link> instances to activate and deactivate
 * them at the same time.
 * 
 * @author Axel Baumgartner
 */
public abstract class SpacebrewSensorManager
{
	/**
	 * Holds all sensors
	 */
	protected HashMap<String, SpacebrewSensor>	sensors	= new HashMap<String, SpacebrewSensor>();

	/**
	 * True if all sensor are active.
	 */
	private boolean								active	= false;

	/**
	 * Activates all sensors.
	 */
	public void activate()
	{
		for (SpacebrewSensor sensor : sensors.values())
		{
			sensor.activate();
		}
		active = true;
	}

	/**
	 * Deactivates all sensors.
	 */
	public void deactivate()
	{
		for (SpacebrewSensor sensor : sensors.values())
		{
			sensor.deactivate();
		}
		active = false;
	}

	/**
	 * @return true if the sensors are activated
	 */
	public boolean activated()
	{
		return this.active;
	}

	/**
	 * @return The sensors this manager holds.
	 */
	public HashMap<String, SpacebrewSensor> getSensors()
	{
		return sensors;
	}

	/**
	 * @param name The name of the sensor
	 * @return The specified sensor
	 */
	public SpacebrewSensor getSensor(String name)
	{
		return sensors.get(name);
	}
}
