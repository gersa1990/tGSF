/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
package de.irf.it.rmg.core.teikoku.job;

import de.irf.it.rmg.core.util.time.Distance;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface RuntimeInformation {

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Distance getWaitTime();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Distance getElapsedWaitTime();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Distance getRemainingWaitTime();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Distance getRunTime();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Distance getElapsedRunTime();

	/**
	 * This function returns the remaining execution time of the 
	 * job.
	 * 
	 * 
	 */
	Distance getRemainingRunTime();
	
	/**
	 * When used with preemptive jobs this function adds
	 * the time the job was processed before it was interrupted.
	 */
	void addRunningTime();
	
	/**
	 * Return the running time so far in seconds 
	 */
	public long getRunningTime();
	
	/**
	 * This function increments the number of interruptions of the job
	 */
	void countInterruption();
	
	/**
	 * This function returns the number of interruptions that the job has so far 
	 */
	
	int getNumberOfInterruptions();
	
	/**
	 * This function sets the interruption flag
	 * @param flag
	 */
	
	void setIterruptionFlag(boolean flag );
	
	/**
	 * This function returns the interruption flag
	 * @return
	 */
	boolean getInterruptionFlag ();
	
	/**
	 * This function calculates and holds the current job's laxity
	 * @return Job's laxity
	 */
	
	long getLaxity ();
	
	public Object clone();

}