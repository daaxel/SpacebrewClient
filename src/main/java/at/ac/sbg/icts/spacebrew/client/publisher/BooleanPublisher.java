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
package at.ac.sbg.icts.spacebrew.client.publisher;

import at.ac.sbg.icts.spacebrew.client.SpacebrewClient;

/**
 * A boolean publisher.
 * 
 * @author Axel Baumgartner
 */
public class BooleanPublisher extends Publisher
{
	/**
	 * The default value of this publisher
	 */
	protected boolean	defaultValue	= false;

	/**
	 * The last value that was sent to the server
	 */
	protected boolean	outValue		= false;

	/**
	 * Adds a boolean publisher with its name to the client.
	 * 
	 * @param name The name of this publisher
	 * @param defaultValue The default value
	 * @param client The client through which messages are sent
	 */
	public BooleanPublisher(String name, boolean defaultValue, SpacebrewClient client)
	{
		super(name, client);
		this.defaultValue = defaultValue;
		client.addPublisher(name, defaultValue);
	}

	/**
	 * Sends a boolean message.
	 * 
	 * @param value The boolean message to send
	 */
	public void publish(boolean value)
	{
		if (updateFilterActive && value == outValue)
		{
			return;
		}

		outValue = value;

		client.publish(getName(), outValue);
	}

	/**
	 * @return The default value
	 */
	public boolean getDefaultValue()
	{
		return this.defaultValue;
	}

	/**
	 * @return The last value that was sent to the server
	 */
	public boolean getOutValue()
	{
		return outValue;
	}
}
