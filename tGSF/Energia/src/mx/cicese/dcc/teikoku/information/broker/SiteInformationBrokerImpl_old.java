/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.scheduler.ParallelMachineScheduler;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.scheduler.SchedulerHelper;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.workload.job.Precedence;

import java.util.Queue;


/**
 * @author ahirales
 *
 */
public class SiteInformationBrokerImpl_old implements SiteInformationBroker {
	/**
	 *	Information broker Identifier
	 */
	private UUID id;
		
	/**
	 *	Holds a reference to the Site that the information broker is associated to 
	 */
	private Site site;
	
	/**
	 * Holds the temporal schedule used for estimates.
	 */
	private Site cloneSite;
	
	
	/**
	 *	 The refresh rate for status information
	 */
	private long refresRate;
		
	/**
	 * TODO:
	 */
	private Map<InformationType,SiteInformationData> publicInformation;
	
	/**
	 * TODO:
	 */
	private List<Job> queue;
	
	
	/**
	 * Class constructor 
	 */
	public SiteInformationBrokerImpl_old()  throws InstantiationException {
		this.id = UUID.randomUUID();
		this.refresRate = -1;
		publicInformation = new HashMap<InformationType,SiteInformationData>();
		queue = new LinkedList<Job>();
	} // SiteInformationBrokerImpl
	
	/**
	 * Notifies the Grid information broker to update 
	 * status data.
	 */
	public void notifyGridInfBroker() {
		/* 
		 * TODO: 
		 */
	} // notifyGridInfBroker
	
	
	/**
	 * Inverts a given topological sorted list
	 * 
	 * @param InformationType	a request type of information
	 * @param Job				a job to schedule
	 * @return					the requested data 
	 */
	@SuppressWarnings(value="all")
	public SiteInformationData getPublicInformation(InformationType infoType,
			Queue<Job> jobs, JobControlBlock jcb) {
		SiteInformationData msg = null;
		
		/* site information was requested */
		if(infoType.equals(InformationType.STATUS) ){
			msg = this.getSampleStatusInformation();			
		} // End request status information
		
		
		/* Estimates are requested */
		if(infoType.equals(InformationType.ESTIMATE)){
			msg = this.getSampleEstimateInformation(jobs);
		} // End estimates
	
		
		// this.logStateInformation(); // MESAGE IN CONSOLE
		
		return msg;
	}
	
	
	/**
	 * Links this component to the given site
	 */
	public void setSite(Site site){
		this.site = site;
	}
	
	/**
	 * Gets the site this component is link to
	 */
	public Site getSite() {
		return this.site;
	}
	
	
	/**
	 * Sets the sample frequency 
	 * 
	 * @param	the refresh rate frequency
	 */
	public void setRefreshRate(long refreshRate) {
		this.refresRate = refreshRate;
	}
	
	
	/**
	 * Gets the inormation broker user id
	 */
	public UUID getUUID() {
		return this.id;
	}
	
	
	/**
	 *	Samples state information from this compute site
	 *
	 * 	@return	state information
	 */
	private SiteStatusInformation instrumentStatusInformation(){
		site.updateSiteInformation();
		
		SiteInformation siteInfo = this.site.getSiteInformation();
		SiteStatusInformation msg = new SiteStatusInformation(this.site.getName());
		
		((SiteStatusInformation)msg).maxTotalJobs = -1; 					// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).maxRunningJobs = -1;					// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).maxWaitingJobs = -1;					// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).maxPreLRMSWaitingJobs = -1;			// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).servingState = siteInfo.getServingState();
		((SiteStatusInformation)msg).totalJobs = 
			((ComputeSiteInformation)siteInfo).preLRMSWaitingJobs +
			((ComputeSiteInformation)siteInfo).runningJobs +
			((ComputeSiteInformation)siteInfo).waitingJobs;
		((SiteStatusInformation)msg).runningJobs = 
			((ComputeSiteInformation)siteInfo).runningJobs;
		((SiteStatusInformation)msg).localRunningJobs = 
			((ComputeSiteInformation)siteInfo).localRunningJobs;
		((SiteStatusInformation)msg).waitingJobs = 
			((ComputeSiteInformation)siteInfo).waitingJobs;
		((SiteStatusInformation)msg).localWaitingJobs = 
			((ComputeSiteInformation)siteInfo).localWaitingJobs;
		((SiteStatusInformation)msg).suspendedJobs = 
			((ComputeSiteInformation)siteInfo).suspendedJobs;
		((SiteStatusInformation)msg).preLRMSWaitingJobs =
			((ComputeSiteInformation)siteInfo).preLRMSWaitingJobs;
		((SiteStatusInformation)msg).localPreLRMSWaitingJobs =
			((ComputeSiteInformation)siteInfo).localPreLRMSWaitingJobs;
		((SiteStatusInformation)msg).localSuspendenJobs = -1;				// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).estimatedAveragewaitingTime = -1;		// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).estimatedWorstWaitingTime = -1;		// Not deined yet as a property of a site.
		((SiteStatusInformation)msg).validity = 
			TimeHelper.add(Clock.instance().now(), new Distance(this.refresRate));
		((SiteStatusInformation)msg).infoType = 
			InformationType.STATUS;
		((SiteStatusInformation)msg).numProcessors =
			((ComputeSiteInformation)siteInfo).getNumberOfAvailableResources();
		
		//Macadamia
		((SiteStatusInformation)msg).coreEnergyConsumption = ((ComputeSiteInformation)siteInfo).getCoreEnergyConsumption();
		
		/* Consult the number of available resources */
		Schedule schedule = this.getSite().getScheduler().getSchedule();
		((SiteStatusInformation)msg).numAvailableProcessors = 
			schedule.getActualFreeness();
		
		return msg;
	} // instrumentStatusInformation
	
	
	/**
	 * Gets state information from this compute site 
	 * 
	 * @return	state information
	 */
	private SiteInformationData getSampleStatusInformation(){
		SiteInformationData msg = null;
		
		/* Refresh rate not define. Then always sample data and set its validity to null. */
		if( this.refresRate == -1 ) {
			msg = this.instrumentStatusInformation();
			((SiteStatusInformation)msg).validity = null;
		} else {
			/* Refresh rate was set */
			/* Perhaps, there is previously sample status data */
			SiteStatusInformation bufferedData = (SiteStatusInformation) publicInformation.get(InformationType.STATUS);
			if( bufferedData != null ) {
					/* Must check if this data is still usable, validity might have expired */
					if(Clock.instance().now().after(bufferedData.getValidity())) {
						/* information is no longer valid, must resample and store copy*/
						msg = this.instrumentStatusInformation();
						publicInformation.put(InformationType.STATUS, msg);
					} else {
						/* information still valid */
						msg = bufferedData;
					} // End if validity check
			} else {
			/* No information was previously sampled, just sample it, validity is automatically set */
				msg = this.instrumentStatusInformation();	
				publicInformation.put(InformationType.STATUS, msg);
			}// End if check is previous data was stored
		}// End refresh rate check	
		
		return msg;
	} // SiteInformationData
	
	
	/**
	 * Retrieves estimates for the given job  
	 * 
	 * @return	estimate information
	 */
	private SiteInformationData getSampleEstimateInformation(Queue<Job> jobs) {
		SiteInformationData msg = null;
		
		Job job = jobs.peek();
		// I have my doubts. Is the job to be schedule in this moment or must delays be introduced.
		Slot nextFreeSlot = null;
		Period nextFreePeriod = null;
		
		long r = -1;
					
		/*
		 * Sets up the exact interval of time required for execution of a job
		 */
		r = ((SWFJob)job).getSubmitTime();
		Instant jSTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(r));
		Instant jETime = TimeFactory.newEternity();
		Period jSearchArea = new Period(jSTime, jETime);
		Distance ocupation = job.getDescription().getEstimatedRuntime();
		//int numResources = ((SWFJob) job).getNumberOfAllocatedProcessors();
		//Distance ocupation = new Distance(DateHelper.convertToMilliseconds(((SWFJob)job).getRequestedTime()));
		int numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();

		/* Before trying to schedule, 
		 * 	A)The site must verify if it has sufficient resources to schedule the job.
		 *    That is, if the size of the machine is not large enough to process the job, then
		 *    - ((Estimate) msg).earliestStartTime = -1
		 *    - ((Estimate) msg).earliestFinishingTime = -1
		 *    - And ((Estimate) msg).earliestAvailTime = -1
		 *   
		 *   Otherwise, estimates are estimated by building the temporal schedule.
		 */
		// Validate if request can be satisfied
		int numAvailResources = this.site.getSiteInformation().getNumberOfAvailableResources();
		if( numResources <=  numAvailResources) {
			try {
			
				/* 
				 * Create or update the temporal schedule. Such data structure is
				 * used to calculate job execution time estimates, such as:
				 * - Job start time
				 * - Job end time
				 */
				updateTemporalSchedule();
			
			
				/*
				 * try to find a fitting slot for the given job and the availability
				 * in this site schedule
				 */
				this.queue.add(job);
				Schedule schedule = this.cloneSite.getScheduler().getSchedule();
						
				/* Perhaps it would be optimal to determine if there are resources available 
				 * in this moment. But we dont really care if they are available at the current
				 * instance, if they are available at a future time, it is ok. 
				 * They would be reserved.
				 * 
				 * To query if their are resources, the getActualFreeness method can be used
				 * 
				 * if( schedule.getActualFreeness() == 0 )
				 */
				nextFreeSlot = schedule.findNextFreeSlot(ocupation, numResources, jSearchArea);
			
				/* The earliest time at which a site is ready for task execution 
				 * Hence, at least one processors is require to be free
				 */
				nextFreePeriod = schedule.findNextFreePeriod(numResources, jSearchArea);
		 	
			} // try
			catch (InvalidTimestampException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InitializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //End if
		// Fill in the estimates
		msg = new Estimate();
		if( nextFreeSlot != null) {			
			/*
			((Estimate) msg).earliestStartTime = nextFreeSlot.getAdvent();
			((Estimate) msg).earliestFinishingTime = nextFreeSlot.getCessation();
			((Estimate) msg).validity = null; */			//Does not apply, it must always be sampled.
			
			((Estimate) msg).earliestStartTime.put((SWFJob)job, nextFreeSlot.getAdvent()); 
			((Estimate) msg).earliestFinishingTime.put((SWFJob)job,nextFreeSlot.getCessation());
			((Estimate) msg).validity = null; 			//Does not apply, it must always be sampled.
			
			
		} 
		
		if( nextFreePeriod != null) {
			//((Estimate) msg).earliestAvailTime = nextFreeSlot.getAdvent(); //Incorrect pero temporal
			((Estimate) msg).earliestAvailTime.put((SWFJob)job,nextFreeSlot.getAdvent());
				//nextFreePeriod.getAdvent();
		}
		
		if(nextFreeSlot == null || nextFreePeriod == null)
			msg = null;
		
		return msg;
	} //End getSampleEstimateInformation
	
	
	/* Methods for creating the temporal schedule */
	private void updateTemporalSchedule() throws InstantiationException, InitializationException{
		
		/**
		 * Create a clone schedule if it does not exists, otherwise update the
		 * clone schedule to the real schedule.  
		 */
		if( this.cloneSite == null ) {
			this.cloneSite = ((ComputeSite)this.getSite()).clone();
			this.cloneSite.getScheduler().getSchedule().setScheduler(this.cloneSite.getScheduler());
			this.cloneSite.getScheduler().setSite(this.cloneSite);
			((ComputeSiteInformation)this.cloneSite.getSiteInformation()).setSite(this.cloneSite);
		} else {
			/**
			 * Update the clone schedule (queue, utilization changes, etc)
			 */
			Schedule copy = this.site.getScheduler().getSchedule().clone();
			copy.setScheduler(this.cloneSite.getScheduler());
			((ParallelMachineScheduler) this.cloneSite.getScheduler()).setSchedule(copy);
			
			
			/**
			 * Update strategy information
			 */
			this.cloneSite.getScheduler().getStrategy().setSite(this.cloneSite);
			
			
			/**
			 *  Update the compute site information
			 */
			ComputeSiteInformation siteInformation = ((ComputeSiteInformation)this.site.getSiteInformation()).clone();
			this.cloneSite.setSiteInformation(siteInformation);
			((ComputeSiteInformation)this.cloneSite.getSiteInformation()).setSite(this.cloneSite);
		}

		/**
		 * Schedule any job that is pending execution, that is, that have state Queued.
		 * Such set of jobs are to be inserted in the clone schedule. But their state 
		 * must remain unchanged.
		 */
		if(this.getSite().getScheduler().getQueue().size() > 0){
			// Create a copy by value of queued jobs. 
			SortedQueue<Job> queuedJobs = new SortedQueueBinarySearchImpl<Job>();
			queuedJobs.addAll(this.getSite().getScheduler().getQueue());
			
			/* Schedule queued jobs, such jobs have been sent to this site but
			 * are pending scheduling and execution
			 */
			SchedulerHelper.scheduleQueuedJobs(
					this.cloneSite.getScheduler().getSchedule(),
					queuedJobs,
					this.cloneSite.getScheduler().getStrategy());	
		} // End if
		
		
		/* Check whether there is some job, who has requested an estimate in the past,
		 * but has not been send to a computational site yet. If such condition is true,
		 * then those jobs must be included in the temporal (dummy) schedule. Since, we can 
		 * assume, that they may be sent to this site. When they are finally sent to a site,
		 * then they are no longer considered in the temporal schedule. 
		 */
		List<Job> toRemove = new LinkedList<Job>();
		for(Iterator<Job> it = queue.iterator(); it.hasNext();) {
				Job j = it.next(); 
				if( !j.getLifecycle().getLastCondition().equals(State.RELEASED))
					toRemove.add(j);
		} // End for
		if(toRemove.size()>0)
			queue.removeAll(toRemove);
		
		// If there are some tasks in queue, then insert them in schedule in the 
		// order in which request for an estimate was made.
		if(queue.size() > 0) {
			SortedQueue<Job> queuedJobs = new SortedQueueBinarySearchImpl<Job>();
			queuedJobs.addAll(queue);
			
			SchedulerHelper.scheduleQueuedJobs(
					this.cloneSite.getScheduler().getSchedule(),
					queuedJobs,
					this.cloneSite.getScheduler().getStrategy());
		} 
	} // End cloneSite
	
	

	
	/**
	 * Helper method that prints out or logs site status
	 * information
	 *
	 */
	private void logStateInformation() {
		SiteInformationData msg = this.getSampleStatusInformation();
		System.out.println("Site Information Broker: Information dump");
		System.out.println("Simulation time -> " + Clock.instance().now());
		System.out.println("Site name -> " + ((SiteStatusInformation)msg).getName());
		System.out.println("Total jobs : " + ((SiteStatusInformation)msg).totalJobs);
		System.out.println("Running jobs : " + ((SiteStatusInformation)msg).runningJobs);
		System.out.println("Waiting jobs : " + ((SiteStatusInformation)msg).waitingJobs);
		System.out.println("Suspended jobs : " + ((SiteStatusInformation)msg).suspendedJobs);
		System.out.println("Released jobs : " + ((SiteStatusInformation)msg).preLRMSWaitingJobs);
		System.out.println();
	}// End logStateInformation


} //End SiteInformationBrokerImpl
