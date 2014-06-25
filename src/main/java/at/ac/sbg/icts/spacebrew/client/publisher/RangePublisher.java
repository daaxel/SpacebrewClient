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
 * A range publisher offering simple linear transformation and low-pass
 * filtering for input values.
 * 
 * @author Axel Baumgartner
 */
public class RangePublisher extends Publisher
{
	/**
	 * The default value
	 */
	protected int			defaultValue;

	/**
	 * The minimum and maximum values of input values
	 */
	private float			minValue				= 0;
	private float			maxValue				= 0;

	/**
	 * Values determined by the SpaceBrew protocol
	 */
	public static final int	MIN_RANGE_VALUE			= 0;
	public static final int	MAX_RANGE_VALUE			= 1023;

	/**
	 * The last value that was given as input
	 */
	private float			inValue					= 0;

	/**
	 * The last low-pass filtered value
	 */
	private float			lowPassFilteredValue	= 0;

	/**
	 * The last value that was sent to the server
	 */
	private int				outValue				= 0;

	private boolean			lowPassFilterActive		= false;

	/**
	 * The smoothing factor alpha with (0 < alpha < 1) for the low-pass filter
	 */
	private float			lowPassAlpha			= 0.2f;

	/**
	 * Adds a range publisher with its name to the client.
	 * 
	 * @param name The name of this publisher
	 * @param defaultValue The default value
	 * @param client The client through which messages are sent
	 */
	public RangePublisher(String name, int defaultValue, SpacebrewClient client)
	{
		super(name, client);
		this.defaultValue = defaultValue;
		client.addPublisher(name, defaultValue);
	}

	/**
	 * @param minValue The minimum of the input values
	 */
	public void setMinValue(float minValue)
	{
		this.minValue = minValue;
	}

	/**
	 * @param maxValue The maximum of the input values
	 */
	public void setMaxValue(float maxValue)
	{
		this.maxValue = maxValue;
	}

	/**
	 * @return The minimum of the input values
	 */
	public float getMinValue()
	{
		return minValue;
	}

	/**
	 * @return The maximum of the input values
	 */
	public float getMaxValue()
	{
		return maxValue;
	}

	/**
	 * @return The last value that was given as input
	 */
	public float getInValue()
	{
		return inValue;
	}

	/**
	 * @return The last low-pass filtered value
	 */
	public float getLowPassFilteredValue()
	{
		return lowPassFilteredValue;
	}

	/**
	 * The last value that was sent to the server
	 */
	public int getOutValue()
	{
		return outValue;
	}

	/**
	 * Sends a range message to the server. The value is transformed if If
	 * <code>minValue</code> and <code>maxValue</code> are set.
	 * 
	 * @param value The value to publish
	 */
	public void publish(int value)
	{
		this.publish((float) value);
	}

	/**
	 * Sends a range message to the server. The value is transformed if If
	 * <code>minValue</code> and <code>maxValue</code> are set.
	 * 
	 * @param value The value to publish
	 */
	public void publish(float value)
	{
		inValue = value;

		if (client.isConnected())
		{
			float tmpValue = inValue;

			if (lowPassFilterActive)
			{
				lowPassFilteredValue = lowPassFilter(inValue, lowPassFilteredValue);
				tmpValue = lowPassFilteredValue;
			}

			if (minValue != maxValue)
			{
				tmpValue = rangeify(tmpValue);
			}

			if (value < MIN_RANGE_VALUE)
			{
				tmpValue = MIN_RANGE_VALUE;
			}
			else if (value > MAX_RANGE_VALUE)
			{
				tmpValue = MAX_RANGE_VALUE;
			}

			int tmp2Value = (int) tmpValue;

			if (updateFilterActive && tmp2Value == outValue)
			{
				return;
			}

			outValue = tmp2Value;
			client.publish(getName(), outValue);
		}
	}

	/**
	 * Linear converts a float value from the sensor range to the range allowed
	 * in a Spacebrew message.
	 * 
	 * @param value The input value to convert.
	 * @return The value in the new range.
	 */
	public int rangeify(float value)
	{
		return (int) (((value - minValue) * (MAX_RANGE_VALUE - MIN_RANGE_VALUE)) / (maxValue - minValue))
				+ MIN_RANGE_VALUE;
	}

	/**
	 * Activates the low-pass filter.
	 */
	public void activateLowPassFilter()
	{
		lowPassFilterActive = true;
	}

	/**
	 * Deactivates the low-pass filter.
	 */
	public void deactivateLowPassFilter()
	{
		lowPassFilterActive = false;
	}

	/**
	 * @param lowPassAlpha The smoothing factor alpha with (0 < alpha < 1) for
	 *            the low-pass filter. Use a high factor for small smoothing, a
	 *            low factor for high smoothing.
	 */
	public void setLowPassAlpha(float lowPassAlpha)
	{
		this.lowPassAlpha = lowPassAlpha;
	}

	/**
	 * @return The smoothing factor alpha for the low-pass filter
	 */
	public float getLowPassAlpha()
	{
		return lowPassAlpha;
	}

	/**
	 * Low-pass filter after
	 * http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
	 * 
	 * @param newValue The new value
	 * @param oldValue The old value before the new value
	 * @return The low-pass filtered value
	 */
	private float lowPassFilter(float newValue, float oldValue)
	{
		return lowPassAlpha * newValue + (1 - lowPassAlpha) * oldValue;
	}
}
