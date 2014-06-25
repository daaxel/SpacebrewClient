/*******************************************************************************
 * Copyright (c) 2014 Axel Baumgartner. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Lesser Public License v2.1 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * Contributors: Axel Baumgartner - initial API and implementation
 ******************************************************************************/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.sbg.icts.spacebrew.client.RangeSubscriber;

/**
 * A simple range subscriber that logs the incoming boolean value.
 * 
 * @author Axel Baumgartner
 */
public class MyRangeSubscriber implements RangeSubscriber
{
	Logger	log	= LoggerFactory.getLogger(MyRangeSubscriber.class);

	public MyRangeSubscriber()
	{

	}

	@Override
	public void receive(int value)
	{
		log.info("MyRangeSubscriber receives: {} ", value);
	}
}
