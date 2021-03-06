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
 * Copyright (c) 2006 by the
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
package de.irf.it.rmg.core.teikoku.metrics;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;


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
/**
 * @author alexander
 *
 */
final public class AverageResponseTime extends ResponseTime {
	/**
	 * Holds the calculators for this metric's average values.
	 * 
	 */
	private AverageHelper ah;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public AverageResponseTime() {
		super();
		this.ah = new AverageHelper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.ResponseTime#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = super.getLatestValuesPrototype();

		/*
		values[0] = "% timestamp ";
		values[1] = "| job name ";
		values[2] = "| current ART";
		values[3] = "| p_j";
		values[4] = "| z_j"; //size
		values[5] = "| r_j"; */ 
		
		
		values[0] = "";
		values[1] = "";
		values[2] = "";
		values[3] = "";
		values[4] = "";
		values[5] = "";
		
		return values;
	}
	
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			//if (job.getReleasedSite()==this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			//}
		}	
	}
	
	private void handleEvent(Job job, long timestamp){
		
		Object[] values=super.getLatestValuesPrototype();
		
		values[0] = timestamp;
		values[1] = ((SWFJob) job).getJobNumber();
		values[2] = this.calculateResponseTime(job);
		values[3] = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		values[4] = job.getResources().size();
		values[5] = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		super.setLatestValues(values);
		
		super.manualMakePermanent();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.ResponseTime#calculateResponseTime(de.irf.it.rmg.core.teikoku.Job)
	 */
	@Override
	protected double calculateResponseTime(Job j) {
		return ah.calculateAverage(super.calculateResponseTime(j));
	}
}
