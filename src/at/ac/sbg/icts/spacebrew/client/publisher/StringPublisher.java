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
		client.send(getName(), value);
	}
}
