/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014 Dennis Sheirer
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package source.tuner.frequency;

import sample.Listener;


/**
 * Interface for broadcasting changes to frequency, bandwidth, sample rate,
 * and actual sample rate values to all registered listeners.
 * <p>
 * Note: broadcasted frequency should be the uncorrected, or displayable
 * frequency value.  All frequency correction aspects should be handled within
 * the device that implements the tuning of the frequency.
 */
public interface IFrequencyChangeListener
{
  Listener<FrequencyChangeEvent> getFrequencyChangeListener();
}
