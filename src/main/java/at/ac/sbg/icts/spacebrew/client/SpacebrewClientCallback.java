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
 * {@link SpacebrewClient} objects. Note that the callback methods for incoming
 * messages are not defined in this interface, but will be called by
 * <code>SpacebrewClient</code> via reflection.
 * 
 * @author Axel Baumgartner
 */
public interface SpacebrewClientCallback
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
	 * Called when an error occurred during the lifetime of the connection.
	 */
	public void onError();
}
