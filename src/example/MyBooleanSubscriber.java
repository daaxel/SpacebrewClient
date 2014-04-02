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
package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.sbg.icts.spacebrew.client.BooleanSubscriber;

/**
 * A boolean subscriber that simple logs the incoming boolean value.
 * 
 * @author Axel Baumgartner
 */
public class MyBooleanSubscriber implements BooleanSubscriber
{
	Logger	log	= LoggerFactory.getLogger(MyBooleanSubscriber.class);

	public MyBooleanSubscriber()
	{

	}

	@Override
	public void receive(boolean value)
	{
		log.info("MyBooleanSubscriber receives: {} ", value);
	}
}
