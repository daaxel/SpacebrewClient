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
package at.ac.sbg.icts.spacebrew.client;

/**
 * Interface for a string subscriber.
 * 
 * @author Axel Baumgartner
 */
public interface StringSubscriber
{
	/**
	 * Receives a string message from the server.
	 * 
	 * @param value The value of the string message
	 */
	public void receive(String value);
}
