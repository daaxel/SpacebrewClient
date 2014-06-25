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
 * Represents a Spacebrew message.
 * 
 * @author Axel Baumgartner
 */
public class SpacebrewMessage
{
	/**
	 * The three possible types of Spacebrew messages.
	 */
	public static final String	TYPE_BOOLEAN	= "boolean";
	public static final String	TYPE_RANGE		= "range";
	public static final String	TYPE_STRING		= "string";

	/**
	 * The name of the message.
	 */
	public String				name;

	/**
	 * The type of the message (i.e. <code>SpacebrewMessage.TYPE_BOOLEAN</code>,
	 * <code>SpacebrewMessage.TYPE_RANGE</code> or
	 * <code>SpacebrewMessage.TYPE_STRING</code>)
	 */
	public String				type;

	/**
	 * The default value of the message.
	 */
	public String				defaultValue;

	/**
	 * The <code>boolean</code> value of the message if it is a boolean message.
	 */
	public boolean				boolValue;

	/**
	 * The <code>int</code> value of the message if it is a range message.
	 */
	public int					intValue;

	/**
	 * The <code>String</code> value of the message if it is a string message.
	 */
	public String				stringValue;
}
