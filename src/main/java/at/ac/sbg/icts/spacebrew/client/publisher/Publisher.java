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
 * Base class for publishers. Publishers hold a client instance through which
 * they send their messages.
 * 
 * @author Axel Baumgartner
 */
public abstract class Publisher
{
	/**
	 * The name of the publisher
	 */
	private String				name				= "";

	/**
	 * The client instance through which messages are sent
	 */
	protected SpacebrewClient	client;

	/**
	 * If true only updated values will be published.
	 */
	protected boolean			updateFilterActive	= false;

	/**
	 * @param name The name of the publisher
	 * @param client The client instance through which messages are sent
	 */
	public Publisher(String name, SpacebrewClient client)
	{
		this.name = name;
		this.client = client;
	}

	/**
	 * @return The name of the publisher
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Activates the update filter.
	 */
	public void activateUpdateFilter()
	{
		updateFilterActive = true;
	}

	/**
	 * Deactivates the update filter.
	 */
	public void deactivateUpdateFilter()
	{
		updateFilterActive = false;
	}
}
