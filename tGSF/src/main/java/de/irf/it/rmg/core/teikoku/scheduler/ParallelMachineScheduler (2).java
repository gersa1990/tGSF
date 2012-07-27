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
package de.irf.it.rmg.core.teikoku.scheduler;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.DequeuingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.sim.kuiga.Clock;
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
public class ParallelMachineScheduler extends AbstractScheduler
		implements Scheduler, StrategyListener {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(ParallelMachineScheduler.class);

	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 */
	public ParallelMachineScheduler()
			throws InstantiationException {
		// super(site);
	}
	
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public ParallelMachineScheduler(ParallelMachineScheduler another) {
		super(another);
	}

	/**
	 * Creates a copy 
	 */
	@Override
	public Scheduler clone() {
		return new ParallelMachineScheduler(this);
	}
	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.Scheduler
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#activate()
	 */
	public void activate() { 

		
		if (!super.getQueue().isEmpty())
		{
			
			//super.getLocalStrategy().decide(super.getQueue(), super.getSchedule());
			super.getQueue().reorder();
			super.getLocalStrategy().setSLAAccepter(this.getLocalAccepter());
			super.getLocalStrategy().decide(super.getQueue(), super.getSchedule());
			
	
		}
	}
	
	
	public void reactivate() {
		Schedule schedule = super.getSchedule();
		Set<Job> scheduledJobs = schedule.getScheduledJobs();
		Iterator<Job> schIterator = scheduledJobs.iterator();
		
		
		
		if (schIterator.hasNext())
		{
		while(schIterator.hasNext())
		{
			Job currentJob = schIterator.next();
			
			if (currentJob.getLifecycle().getLastCondition() == State.STARTED )
				super.createEvents(currentJob, State.INTERRUPTED);
			else
			{
				/*try {
					super.getSite().getScheduler().getSchedule().removeJob(currentJob);
					
				} catch (IllegalOccupationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				if(currentJob.getLifecycle().getLastCondition() != State.QUEUED)
				{
					currentJob.getLifecycle().addEpisode(State.INTERRUPTED);
				}
			
			}
			super.getQueue().add(currentJob);
			
			
			//super.getSite().getActivityBroker().getListOfManagedJobs().add(currentJob);
			
		}
			
		}
		
			super.createJobsInterruptionEvent();
	}
	
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener#advise(de.irf.it.rmg.core.teikoku.common.Job,
	 *      de.irf.it.rmg.core.teikoku.common.Job)
	 */
	public void advise(Job jobToSchedule, Slot possibleSlot)
			throws DecisionVetoException {
		if (possibleSlot != null) {
		/*	try {
				//try to dequeue Job
				super.dequeue(this, jobToSchedule);
			} catch (DequeuingVetoException e) {
				//Check whether job was aborted on this site
				Period[] periods=jobToSchedule.getLifecycle().findPeriodsFor(State.ABORTED);
				boolean cond=false;
				if (periods!=null){
					Site[] sites=jobToSchedule.getProvenance().findSitesAt(periods[periods.length-1].getAdvent());
					for (Site site: sites){
						if (site==this.getSite()){
							cond=true;
							getQueue().remove(jobToSchedule);
							break;
						}
					}
				}
				if (!cond){
					e.printStackTrace();
				}	
			}*/
			try{
				/*
				 * adapt job to slot
				 */
				Instant startTime = possibleSlot.getAdvent();
				Period duration = new Period(startTime, possibleSlot.getCessation());
				jobToSchedule.setDuration(duration);
				jobToSchedule.setResources(possibleSlot.getResources().createSubSetWith(
						jobToSchedule.getDescription().getNumberOfRequestedResources()));

				
				Instant now = Clock.instance().now();
				
				/*
				 * add job to schedule
				 */
				
				super.getSchedule().addJob(jobToSchedule);
				
				if (jobToSchedule.getLifecycle().getLastCondition() == State.QUEUED)
				{
					jobToSchedule.getLifecycle().addEpisode(State.SCHEDULED);
				
					// TODO: move 
					if(startTime.compareTo(now)==0)
					super.createEvents(jobToSchedule,State.STARTED);
					

				}
				if (jobToSchedule.getLifecycle().getLastCondition() == State.INTERRUPTED)
				{
					jobToSchedule.getLifecycle().addEpisode(State.RESUMED);
					
					if(startTime.compareTo(now)==0)
					super.createEvents(jobToSchedule, State.STARTED);
				}
				
			} // try
			catch (InvalidTimestampException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalOccupationException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalScheduleException e) {
				e.printStackTrace();
			} // catch 
		} // if
		else { //This is a big modification when SLA is used
			try {
				
				System.out.println("The job can not meet guaranteed time and was rejected");
				
				jobToSchedule.getLifecycle().addEpisode(State.INTERRUPTED);
				
		
				super.dequeue(this, jobToSchedule);
				
			} catch (DequeuingVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleAbortion(Job job) throws AbortionNotHandledException{
		//resubmit the job
		this.putNextJob(job);
	}
	
	@Override
	public void handleCompletion(Job job){
		getLocalStrategy().handleCompletion(job);
	}
}
