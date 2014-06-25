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
 * Interface contract for objects that want to act as a callback object for
 * {@link WebSocketClientImpl} objects.
 * 
 * @author Axel Baumgartner
 */
public interface WebSocketClientImplCallback
{
	/**
	 * Called when the connection was opened.
	 */
	public void onOpen();

	/**
	 * Called when the connection was closed.
	 */
	public void onClose();

	/**
	 * Called when a message has been received.
	 * 
	 * @param message The received message
	 */
	public void onMessage(String message);

	/**
	 * Called when an exception occurred during the lifetime of the connection.
	 * 
	 * @param exception The <code>Exception</code> that caused the error
	 */
	public void onError(Exception exception);
}
