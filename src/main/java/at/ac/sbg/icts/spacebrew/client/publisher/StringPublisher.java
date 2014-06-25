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
 * A string publisher.
 * 
 * @author Axel Baumgartner
 */
public class StringPublisher extends Publisher
{
	/**
	 * The default value
	 */
	protected String	defaultValue	= "";
	protected String	outValue		= "";

	/**
	 * Constructor. Adds a string publisher with its name to the client.
	 * 
	 * @param name The name of this publisher
	 * @param defaultValue The default value
	 * @param client The client through which messages are sent
	 */
	public StringPublisher(String name, String defaultValue, SpacebrewClient client)
	{
		super(name, client);
		this.defaultValue = defaultValue;
		client.addPublisher(name, defaultValue);
	}

	/**
	 * Sends a string message to the server.
	 * 
	 * @param value The value to publish
	 */
	public void publish(String value)
	{
		if (updateFilterActive && value.equals(outValue))
		{
			return;
		}

		outValue = value;

		client.publish(getName(), outValue);
	}

	/**
	 * @return The last value that was sent to the server
	 */
	public String getOutValue()
	{
		return outValue;
	}
}
