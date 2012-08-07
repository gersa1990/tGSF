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
package de.irf.it.rmg.core.teikoku.kernel.events;


import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public class JobCanceledEvent extends Event {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Job canceledJob;

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @param abortedJob
	 * @param location
	 */
	public JobCanceledEvent(final Instant timestamp, final Job abortedJob,
			final Site location) {

		super(timestamp, location.getUUID().toString(), location);
		this.canceledJob = abortedJob;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final public Job getCanceledJob() {
		return this.canceledJob;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param abortedJob
	 */
	final public void setAbortedJob(final Job abortedJob) {
		this.canceledJob = abortedJob;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.sim.kuiga.Event
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.sim.kuiga.Event#getOrdinal()
	 */
	@Override
	protected int getOrdinal() {
		return EventType.JOB_CANCELED.ordinal();
	}
}

