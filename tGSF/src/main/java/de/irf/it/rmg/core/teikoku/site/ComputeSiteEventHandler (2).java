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
package de.irf.it.rmg.core.teikoku.site;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Bootstrap;
import de.irf.it.rmg.core.teikoku.common.Reservation;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.exceptions.SubmissionException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobAbortedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedOnForeignSiteEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.ReservationEndEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.ReturnOfLentResourcesEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.AllocQueuedEvent;
import de.irf.it.rmg.core.teikoku.scheduler.AbstractScheduler;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.LISTStrategy;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.EventSink;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;
import de.irf.it.rmg.sim.kuiga.listeners.TimeChangeListener;
import de.irf.it.rmg.sim.kuiga.listeners.TypeChangeListener;
import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.scheduler.IndpJobScheduler;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.JobStartedEventRegister;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.LaxityInterruptionEvent;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.LaxityInterruptionEventRegister;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.SLAReviewEvent;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.StartSchedulingEvent;
import mx.uabc.lcc.teikoku.error.ResourceAvailabilityEvent;
import mx.uabc.lcc.teikoku.error.ResourceUnavailabilityEvent;
import mx.uabc.lcc.teikoku.error.SiteAvailabilityEvent;
import mx.uabc.lcc.teikoku.error.SiteUnavailabilityEvent;;


/**
 * @author Alex
 * @author Adan
 *
 */
@EventSink
public class ComputeSiteEventHandler implements TimeChangeListener,TypeChangeListener {

	private Site site;
	
	private boolean completedEventPresent=false;
	
	private Map<Job,JobCompletedEvent> jobComplEventMap = new HashMap<Job,JobCompletedEvent>(); 
	
	private Map<Job, JobStartedEvent> jobStartedEventMap = new HashMap<Job,JobStartedEvent>();
	
	private LaxityInterruptionEvent laxityInterruptionEvent;
	
	private Job previousStartedJob = null;
	
	private long testTime;
	
	final private static Log log = LogFactory.getLog("debugger");
	
	public ComputeSiteEventHandler(Site site) throws InvalidAnnotationException{
		this.site=site;
		/*
		 * Register the compute site event handler as eventsink to be notified for handling events in the
		 * main handling phase and for changes in time or eventtype
		 */
		Kernel.getInstance().registerEventSink(this);
		Kernel.getInstance().registerTypeChangeListener(this);
		Kernel.getInstance().registerTimeChangeListener(this);
		

	} //ComputeSiteEventHandler
	
	
	/**
	 * Overwritten realization of delivering the releasedEvent
	 * @see 
	 */
	@AcceptedEventType(value=JobReleasedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReleasedEvent(JobReleasedEvent event) {
		//Check the Tag
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			this.site.getLocalSubmissionComponent().deliverReleaseEvent(event);		
		}
	}
	
	
	@AcceptedEventType(value=JobQueuedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverQueuedEvent(JobQueuedEvent event){
		Job job=event.getQueuedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			if (job.getProvenance().getLastCondition()==this.site){
				
				
				((AbstractScheduler) this.site.getScheduler()).reactivate();
				//job is queued locally
				//((AbstractScheduler) this.site.getScheduler()).activate();
				
				/* INSTRUMENTATION: Set accounting information. Begin */
				ComputeSiteInformation info = null;
				info = (ComputeSiteInformation) this.site.getSiteInformation();
				info.preLRMSWaitingJobs--;
				if(job.getReleasedSite().getUUID().equals(this.site.getUUID())) {
					info.localPreLRMSWaitingJobs--;
					info.localWaitingJobs++;
				}
				info.waitingJobs++;
				/* INSTRUMENTATION: End */
			}
		}
	}
	
	
	@AcceptedEventType(value=JobStartedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverStartedEvent(JobStartedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			try {
				JobCompletedEvent jobCompletedEvent = 
						this.site.getExecutor().submit(event.getStartedJob());
				jobComplEventMap.put(event.getStartedJob(), jobCompletedEvent);
				
				/* INSTRUMENTATION: Set accounting information. Begin */
				Job job = event.getStartedJob();
				
				testTime = Clock.instance().now().timestamp();
				if(previousStartedJob != null && job.getRuntimeInformation().getInterruptionFlag())
				{
					
					if(!job.equals(previousStartedJob))
					{
						job.getRuntimeInformation().countInterruption();
					}
				}
				
				
				/*
				 * This part will help in the debuging phase
				 */
				String startedMsg = Clock.instance().now().timestamp() +"\tSTARTED\t"+ site.getName()
						+ "\t" + job.getName();
				
				log.trace(startedMsg);
				
				previousStartedJob = job;
				job.getRuntimeInformation().setIterruptionFlag(true);
				
				//Add interruption, every job has an interruption when started except if it
				//has been executed before
				
				ComputeSiteInformation info = null;
				info = (ComputeSiteInformation) this.site.getSiteInformation();
				info.waitingJobs--;
				if(job.getReleasedSite().getUUID().equals(this.site.getUUID())) {
					info.localWaitingJobs--;
					info.localRunningJobs++;
				}
				info.runningJobs++;
				
				/* INSTRUMENTATION: End */
				
				
			} // try
			catch (SubmissionException e) {
				e.printStackTrace();
			}
			//this.site.getScheduler().activate();
		}
	}
	
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverCompletedEvent(JobCompletedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			
			if(laxityInterruptionEvent != null)
			{
				Kernel.getInstance().RemoveLaxityInterruptionEvent(laxityInterruptionEvent);
				laxityInterruptionEvent = null;
			}
			
			Job completedJob = event.getCompletedJob();
			
			//Check whether the job is completed on this site or completed on another site
			if (completedJob.getProvenance().getLastCondition()==this.site){
				this.completedEventPresent=true;
				
				if (completedJob.getProvenance().getLastCondition()==this.site){
					Period realDuration = new Period(
							completedJob.getDuration().getAdvent(), event.getTimestamp());
					try {
						completedJob = this.site.getScheduler().getSchedule().shortenJobDuration(
								completedJob, realDuration);
						completedJob.getLifecycle().addEpisode(State.COMPLETED);
						this.site.getScheduler().getSchedule().removeJob(completedJob);
						this.site.getScheduler().handleCompletion(completedJob);

						//messageInConsole(completedJob);
						
						/* INSTRUMENTATION: Set accounting information. Begin */
						Job job = event.getCompletedJob();
						ComputeSiteInformation info = null;
						info = (ComputeSiteInformation) this.site.getSiteInformation();
						info.runningJobs--;
						
						/*
						 * Information use for debugging
						 */
						String startedMsg = Clock.instance().now().timestamp() +"\tCOMPLETED\t"+ site.getName()
								+ "\t" + job.getName();
						
						log.trace(startedMsg);
						
						if(job.getReleasedSite().getUUID().equals(this.site.getUUID())) {
							info.localRunningJobs--;
						}
						/* INSTRUMENTATION: End */				
						
						
						
						/* Validate if a foreign site requires notification. A foreign site is notified if
							-	The job source is not the current site.
							-	The job is part of a workflow, its job type should not be independent
							-	The foreign site has a Grid broker (this is only important for workflow scheduling)
						*/
						Site foreignSite = completedJob.getReleasedSite();
						if( foreignSite != this.site &&
							!completedJob.getJobType().equals(JobType.INDEPENDENT) &&
							foreignSite.getGridActivityBroker() != null) {
								Event jce = new JobCompletedOnForeignSiteEvent(Clock.instance().now(), completedJob, foreignSite);
								jce.getTags().put(foreignSite.getUUID().toString(), foreignSite);
								Kernel.getInstance().dispatch(jce );
						}
											
					} // try
					catch (IllegalScheduleException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
					catch (InvalidTimestampException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
					catch (IllegalOccupationException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
					
					//REACTIVATE starts the Scheduler to clean the schedule and events in order to start again
					((AbstractScheduler) this.site.getScheduler()).reactivate();
				
				}
				
			}
		}
	}

	
	@AcceptedEventType(value=JobAbortedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverAbortedEvent(JobAbortedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Job abortedJob = event.getAbortedJob();
			if (abortedJob.getProvenance().getLastCondition()==this.site){
				abortedJob.getLifecycle().addEpisode(State.ABORTED);
				try {
					this.site.getScheduler().getSchedule().removeJob(abortedJob);
					this.site.getScheduler().handleAbortion(abortedJob);
				} catch (IllegalOccupationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AbortionNotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	@AcceptedEventType(value=AllocQueuedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverAllocQueuedEvent(AllocQueuedEvent event){
		Job job=event.getQueuedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			if (job.getProvenance().getLastCondition()==this.site){
				//job is queued locally
				IndpJobScheduler sch = (IndpJobScheduler)  
					site.getGridActivityBroker().getIndependentJobScheduler();
				sch.onAllocQueueEvent(event);
			}
		}
	}
	
	
	@AcceptedEventType(value=JobCompletedOnForeignSiteEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverJobCompletedEventOnForeignSite(JobCompletedOnForeignSiteEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Job completedJob = event.getCompletedJob();
			Site foreignSite = completedJob.getReleasedSite();
			if( foreignSite == this.site ) {
				this.site.getGridActivityBroker().hadleJobCompletionOnForeignSite(event);
			}
		}
	}

	
	@AcceptedEventType(value=ReservationEndEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReservationEndEvent(ReservationEndEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			this.completedEventPresent=true;
		}
	}
	
	@AcceptedEventType(value=ReturnOfLentResourcesEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReturnOfLentResourcesEvent(ReturnOfLentResourcesEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Reservation r=event.getReservation();
			Schedule s=this.site.getScheduler().getSchedule();
			int size=r.getResources().size();
			
			//remove the reservation within the schedule-datastructure
			try {
				s.removeReservation(r);
			} catch (IllegalOccupationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Remove the real lent Objects within the siteInformation
			SiteInformation info=this.site.getSiteInformation();
			info.getLentResources().remove(r.getResources());
			List<Resource> rl=info.getLentResources();
			for (Resource res:rl){
				res.setOrdinal(res.getOrdinal()-size);
			}
			
			//Adjust all involved utilization changes
			s.removeLentResources(r.getResources());
		}
	}
	

	public void notifyTypeChange(Event fromEvent, Event toEvent) {
		if ((fromEvent.getClass()==JobCompletedEvent.class) && 
		((toEvent!=null) && (fromEvent.getTimestamp().equals(toEvent.getTimestamp()))) ||
		(toEvent==null) ||
		((fromEvent.getClass()==ReservationEndEvent.class) && (toEvent!=null) && (toEvent.getClass()==JobCompletedEvent.class) &&
				(fromEvent.getTimestamp().equals(toEvent.getTimestamp())))){
			//this.site.getScheduler().activate();
		}
	}
	
	
	public void notifyTimeChange(Instant fromTime, Instant toTime) {
		if (this.completedEventPresent){
			//this.site.getScheduler().activate();
			this.completedEventPresent=false;
		}
	}
	/*
	public void messageInConsole(Job job){
		System.out.println("Job " + job.getName()+ " c: " + Clock.instance().now().timestamp());
	}*/
	
	
	@AcceptedEventType(value=ResourceUnavailabilityEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverResourceUnavailbilityEvent(ResourceUnavailabilityEvent e)
	{
		if (e.getTags().containsKey(this.site.getUUID().toString())){
			site.getErrorManager().deliverResourceUnavailabilityEvent(e);
			 
			if(site.getErrorManager().getTimeBetweenErrors() > 0)
				site.getErrorManager().createNextResourceFailureEvent();

		} // End if
	}
	
	
	// @author Aritz
	@AcceptedEventType(value=ResourceAvailabilityEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverResourceAvailbilityEvent(ResourceAvailabilityEvent e)
	{
		//First check tag-existence
		if (e.getTags().containsKey(this.site.getUUID().toString())){
//			System.out.println("Check");
			try {
				site.getErrorManager().deliverResourceAvailabilityEvent(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("There are no available resources in " + site.getName());
			}
		}
	}
	
	// Site Fail
	
	// @author Aritz
	@AcceptedEventType(value=SiteUnavailabilityEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverSiteUnavailbilityEvent(SiteUnavailabilityEvent e)
	{
		if (e.getTags().containsKey(this.site.getUUID().toString())){
			site.getErrorManager().deliverSiteUnavailabilityEvent(e);
			if(site.getErrorManager().getTimeBetweenErrors() > 0)
				site.getErrorManager().createNextSiteFailureEvent();
		}
	}
	
	// @author Aritz
	@AcceptedEventType(value=SiteAvailabilityEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverSiteAvailbilityEvent(SiteAvailabilityEvent e)
	{
		//First check tag-existence
		if (e.getTags().containsKey(this.site.getUUID().toString())){
//			System.out.println("Check");
			site.getErrorManager().deliverSiteAvailabilityEvent(e);			
		}
	}

	
	 private void messageInConsole(Job completedJob){
		 //System.out.println("Job " + completedJob.getName() + " completed at time " + Clock.instance().now().timestamp());
		 System.out.println("Job " + completedJob.getName() + " completed, # "+  Clock.instance().now().timestamp() + " " + completedJob.getProvenance().getLastCondition().getName());
	 }
	 



//SLA constraints review

@AcceptedEventType(value=SLAReviewEvent.class)
@MomentOfNotification(value=NotificationTime.HANDLE)
public void deliverSLAreviewEvent(SLAReviewEvent event)
{
	if (event.getTags().containsKey(this.site.getUUID().toString())) {
		Job suspendedJob = event.getSuspendedJob();
		//if (suspendedJob.getProvenance().getLastCondition()==this.site){
			// if the last condition of the Job was STARTED the job had been executed before a new job
			// arrived so its COMPLETED event should be eliminated
			
			if(laxityInterruptionEvent != null)
			{
				Kernel.getInstance().RemoveLaxityInterruptionEvent(laxityInterruptionEvent);
				laxityInterruptionEvent = null;
			}
		
			
			if (suspendedJob.getLifecycle().getLastCondition() == State.STARTED ){
				try {
						long now = Clock.instance().now().timestamp();
						long elapsedTime = now - testTime;
						
						
					
						this.site.getScheduler().getSchedule().removeJob(suspendedJob);
						Kernel.getInstance().RemoveCompletedEvent(jobComplEventMap.get(suspendedJob));
						suspendedJob.getRuntimeInformation().addRunningTime();
						
						
						suspendedJob.getLifecycle().addEpisode(State.INTERRUPTED);
					
					} catch (IllegalOccupationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			} //if
			else
			{
				// In this case the Job wasn't being executed but a STARTED event was created instead so it also should be 
				// eliminated.
					/*try {
						this.site.getScheduler().getSchedule().removeJob(suspendedJob);
						
					} catch (IllegalOccupationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					//Kernel.getInstance().RemoveStartedEvent(jobStartedEventMap.get(suspendedJob));
					if(suspendedJob.getLifecycle().getLastCondition() != State.QUEUED)
					suspendedJob.getLifecycle().addEpisode(State.INTERRUPTED);
					

			}

		
			
		}
		//site.getSiteSLAManager().deliverJobSLAReviewEvent(e);
	}

	@AcceptedEventType(value=JobStartedEventRegister.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverJobStartedEventRegister(JobStartedEventRegister event)
	{
		if (event.getTags().containsKey(this.site.getUUID().toString())) {
			jobStartedEventMap.put(event.getJob(), event.getJobStartedEvent());
		}
	}


	@AcceptedEventType(value=StartSchedulingEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverStartSchedulingEvent(StartSchedulingEvent event)
	{
		if(event.getTags().containsKey(this.site.getUUID().toString())){
			this.site.getScheduler().activate();
		}
		
	}

	
	@AcceptedEventType(value=LaxityInterruptionEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverLaxityInterruptionEvent(LaxityInterruptionEvent event)
	{
			if(event.getTags().containsKey(this.site.getUUID().toString()))
			{
				
				((AbstractScheduler) this.site.getScheduler()).reactivate();
				this.laxityInterruptionEvent = null;
			}
	}


	@AcceptedEventType(value=LaxityInterruptionEventRegister.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void setLaxityInterruptionEvent(LaxityInterruptionEventRegister event) {
		
		if(event.getTags().containsKey(this.site.getUUID().toString())){
		//System.out.println("Evento de Registro de Interrupcion Hecho " +event.getLaxityInterruptionEvent().toString() );
		this.laxityInterruptionEvent = event.getLaxityInterruptionEvent();
		}
	}

// Review the interruptions and sets the complete event Job 




} //End ComputeSiteEventHandler 