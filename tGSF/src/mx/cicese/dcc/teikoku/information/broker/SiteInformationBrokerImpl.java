package mx.cicese.dcc.teikoku.information.broker;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import de.irf.it.rmg.sim.kuiga.Clock;
import mx.cicese.dcc.teikoku.utilities.TopologicalSort;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
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
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;
import mx.cicese.dcc.teikoku.scheduler.SchedulerHelper;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;


/**
 * @author ahirales
 *
 */
public class SiteInformationBrokerImpl implements SiteInformationBroker {
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
	private TemporalSchedule cloneSchedule;
	
	
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
	public SiteInformationBrokerImpl()  throws InstantiationException {
		this.id = UUID.randomUUID();
		this.refresRate = -1;
		publicInformation = new HashMap<InformationType,SiteInformationData>();
		queue = new LinkedList<Job>();
		this.cloneSchedule = null;
	} // SiteInformationBrokerImpl
	
	/**
	 * Notifies the Grid information broker to update 
	 * status data.
	 */
	public void notifyGridInfBroker() {
	
	} // notifyGridInfBroker
	
	
	/**
	 * Inverts a given topological sorted list
	 * 
	 * @param InformationType	a request type of information
	 * @param Job				a job to schedule
	 * @return					the requested data 
	 */
	@SuppressWarnings(value="all")
	public SiteInformationData getPublicInformation(InformationType requestedInfo, Queue<Job> jobs, JobControlBlock jcb) {
		SiteInformationData msg = null;
		
		/* site information was requested */
		if(requestedInfo.equals(InformationType.STATUS) ){
			msg = this.getSampleStatusInformation();			
		} // End request status information
		
		
		/* Estimates are requested */
		if(requestedInfo.equals(InformationType.ESTIMATE)){
			msg = this.getSampleEstimateInformation(jobs, jcb);
		} // End estimates
	
		
		/* Earliest start time is requested */
		if(requestedInfo.equals(InformationType.EARLIEST_START_TIME)) {
			msg = this.getEarliestStartTime(jobs, jcb);
		}
			
		
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
	 * Gets the information broker user id
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
		
		((SiteStatusInformation)msg).maxTotalJobs = -1; 					// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).maxRunningJobs = -1;					// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).maxWaitingJobs = -1;					// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).maxPreLRMSWaitingJobs = -1;			// Not defined yet as a property of a site.
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
		((SiteStatusInformation)msg).localSuspendenJobs = -1;				// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).estimatedAveragewaitingTime = -1;		// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).estimatedWorstWaitingTime = -1;		// Not defined yet as a property of a site.
		((SiteStatusInformation)msg).validity = 
			TimeHelper.add(Clock.instance().now(), new Distance(this.refresRate));
		
		//Macadamia
		((SiteStatusInformation)msg).coreEnergyConsumption = ((ComputeSiteInformation)siteInfo).getCoreEnergyConsumption();
		((SiteStatusInformation)msg).siteEnergyEfficiency = ((ComputeSiteInformation)siteInfo).getSiteEnergyEfficiency();
		((SiteStatusInformation)msg).speed = ((ComputeSiteInformation)siteInfo).getSpeed();
		
		((SiteStatusInformation)msg).infoType = 
			InformationType.STATUS;
		((SiteStatusInformation)msg).numProcessors =
			((ComputeSiteInformation)siteInfo).getNumberOfAvailableResources();
		

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
						/* information is no longer valid, must re-sample and store copy*/
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
	private SiteInformationData getSampleEstimateInformation(Queue<Job> jobs, JobControlBlock jcb) {
		SiteInformationData msg =  null;
		Hypergraph<SWFJob,Precedence> g = null;
		
		if( jcb != null )
			g = ((CompositeJob)jcb.getJob()).getStructure();
		
		
		Job job = jobs.peek();
		if(((SWFJob)job).getJobType().equals(JobType.INDEPENDENT) && jobs.size() == 1) {
			int numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();
			int numAvailResources = this.site.getSiteInformation().getNumberOfAvailableResources(); /// checar
			
			if(numResources > numAvailResources)
				return null;
		}
		
		try {
			updateTemporalSchedule(jobs,g);
		} // try
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Job> E = new LinkedList<Job>();
		msg = new Estimate();
		for(Job v : this.queue) {
			Slot nextFreeSlot = null;
			if( v.getLifecycle().getLastCondition().equals(State.RELEASED)) {
				nextFreeSlot = insertInTemporalSchedule(v,jcb);
				// Fill in the estimates
				if( nextFreeSlot != null) {	
					if(jobs.contains(v)){
						((Estimate) msg).earliestStartTime.put((SWFJob)v, nextFreeSlot.getAdvent()); 
						((Estimate) msg).earliestFinishingTime.put((SWFJob)v,nextFreeSlot.getCessation());
						((Estimate) msg).validity = null; 			//Does not apply, it must always be sampled.
						((Estimate) msg).earliestAvailTime.put((SWFJob)v,nextFreeSlot.getAdvent());
					}
				} 
			} else {
				E.add(v);
			}//End if 
 		}
		
		// Clean data structures
		this.queue.removeAll(E);
				
		return msg;
	} //End getSampleEstimateInformation
	
	
	/* Methods for creating the temporal schedule */
	private void updateTemporalSchedule(Queue<Job> J, Hypergraph<SWFJob,Precedence> g) throws InstantiationException, InitializationException{
		/**
		 * Create a clone schedule if it does not exists, otherwise update the
		 * clone schedule to the real schedule.  
		 */
		if( this.cloneSchedule == null ) {
			/**
			 *  Information is cloned in the following order
			 *  - Site information
			 *  - Update utilization changes 
			 */
			ComputeSiteInformation siteInformation = ((ComputeSiteInformation)this.site.getSiteInformation()).clone();
			this.cloneSchedule = new TemporalSchedule(siteInformation);
			this.cloneSchedule.update(this.getSite().getScheduler().getSchedule());
		} else {
			/**
			 * Update the clone schedule (queue, utilization changes, etc)
			 */
			this.cloneSchedule.update(this.getSite().getScheduler().getSchedule());
						
			/**
			 *  Update the compute site information
			 */
			ComputeSiteInformation siteInformation = ((ComputeSiteInformation)this.site.getSiteInformation()).clone();
			this.cloneSchedule.updateSiteInformation(siteInformation);
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
					this.cloneSchedule, queuedJobs,
					this.site.getScheduler().getStrategy());	
		} // End if
		
		Job job = J.peek();
		if(((SWFJob)job).getJobType().equals(JobType.INDEPENDENT) && J.size() == 1)
			this.queue.add(job);
		else 
			actualizaQ(J, g);
	} // End cloneSite
	
	
	
	
	
	private void actualizaQ(Queue<Job> J, Hypergraph<SWFJob,Precedence> g) {
		boolean C;
		LinkedList<Job> pi = new LinkedList<Job>();
		List<SWFJob> T = new LinkedList<SWFJob>();
		TopologicalSort<SWFJob,Precedence> algTS = new TopologicalSort<SWFJob,Precedence>();
		
		int G = ((SWFJob)J.peek()).getCompositeJobId();
		
		for(Job v : this.queue)
			if(((SWFJob)v).getCompositeJobId() == G)
				T.add((SWFJob)v);
		
		if(T.size() != 0){
			for(Job x : J)
				T.add((SWFJob)x);
						
			// Creacion del grafo temporal inicio
			DirectedGraph<SWFJob,Precedence> tmp = new DirectedSparseMultigraph<SWFJob,Precedence>();
			for(Job p : T) 
				tmp.addVertex((SWFJob)p);
		
			for(Job p : T)		
				for(SWFJob s : g.getSuccessors((SWFJob)p))
					if(T.contains(s))
						tmp.addEdge(new Precedence(), (SWFJob)p, s, EdgeType.DIRECTED);
			// Creacion del grafo temporal fin
			
			algTS.sort(tmp);
			T = algTS.getTopologicalSort();
		}//End if
			
		C = true;
		for(Job v : this.queue){
			if(((SWFJob)v).getCompositeJobId() != G)
				pi.add(v);
			else{ if(C != false){
					pi.addAll(T);
					C=false;
				}//End if
			}//End if
		} //End for
		
		if (C != false){
			this.queue.clear();
			this.queue.addAll(pi);
			this.queue.addAll(J);
		} else {
			this.queue.clear();
			this.queue.addAll(pi);
		} //End if
	}//End method
	
	
	
	
	private Slot insertInTemporalSchedule(Job job, JobControlBlock jcb) {
		
		Hypergraph<SWFJob,Precedence> g = null;
			
		if( jcb != null )
			g = ((CompositeJob)jcb.getJob()).getStructure();
		/*
		 * Sets up the exact interval of time required for execution of a job
		 */
		Slot nextFreeSlot = null;
		int numResources = -1;
		int numAvailResources = -1;
		Distance ocupation = null;
		Period jSearchArea = null;
		Instant jETime, jSTime;
		long r = -1;
		
		/*
		 * Determine the release time of the job. 
		 */
		if(job.getJobType().equals(JobType.INDEPENDENT)) {
			r = ((SWFJob)job).getSubmitTime();
		} else {
			/* The jobs belonging to the current workflow is analyzed */
			if(g != null && g.containsVertex((SWFJob)job))
				if(g.inDegree((SWFJob)job) != 0) {
					for(SWFJob p :g.getPredecessors((SWFJob)job)) {
						long cur_aest = jcb.getAFT(p); 
						if(cur_aest > r)
								r = cur_aest;
					}//End predecessor
					r = DateHelper.convertToSeconds(r);
				}// End degree 
			else 
				r = ((SWFJob)job).getSubmitTime();
		} //End else
		
		/* Determine if time to submit job occurs in the past. If so, 
		 * release job in this moment
		 */
		long time = DateHelper.convertToSeconds(Clock.instance().now().timestamp());
		if( time > r)
			r = time;
		
		/*
		 * Set occupation variables
		 */
		jSTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(r));
		jETime = TimeFactory.newEternity();
		jSearchArea = new Period(jSTime, jETime);
		//ocupation = job.getDescription().getEstimatedRuntime();
		ocupation = new Distance(DateHelper.convertToMilliseconds(((SWFJob)job).getRequestedTime()));
		numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();
		numAvailResources = this.site.getSiteInformation().getNumberOfAvailableResources();
		
		/*
		 * Execution time estimates can be estimated if machine can accommodate the job
		 */
		if( numResources <=  numAvailResources) {
			try {
				nextFreeSlot = this.cloneSchedule.findNextFreeSlot(ocupation, numResources, jSearchArea);
			
			} // try
			catch (InvalidTimestampException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch	
		}
		
		/* After finding when the job can be scheduled in the temporal schedule
		 * such, must be inserted in the schedule. Otherwise, the resources that such
		 * job would have occupy will be free.
		 */
		SortedQueue<Job> queuedJobs = new SortedQueueBinarySearchImpl<Job>();
		queuedJobs.add(job);
			
		SchedulerHelper.scheduleQueuedJobs(
				this.cloneSchedule,
				queuedJobs,
				this.site.getScheduler().getStrategy());
		
		
		return nextFreeSlot;
	} // insertInTemporalSchedule

	
	
	
	/*
	 * Gets the Earliest Start Time for a single job.
	 * Estimate is based on status of the real schedule. 
	 * 
	 */
	public SiteInformationData getEarliestStartTime(Queue<Job> jobs, JobControlBlock jcb) {
		Schedule realSchedule = this.getSite().getScheduler().getSchedule();
		SiteInformationData msg =  null;
		Job job = jobs.peek();
		Slot nextFreeSlot = null;
		long r = -1;
					
		/*
		 * Sets up the exact interval of time required for execution of a job
		 */
		r = ((SWFJob)job).getSubmitTime();
		Instant jSTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(r));
		Instant jETime = TimeFactory.newEternity();
		Period jSearchArea = new Period(jSTime, jETime);
		Distance ocupation = new Distance(DateHelper.convertToMilliseconds(((SWFJob)job).getRequestedTime()));
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
				nextFreeSlot = realSchedule.findNextFreeSlot(ocupation, numResources, jSearchArea);		
			} // try
			catch (InvalidTimestampException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
		} //End if

		// Fill in the estimates
		msg = new Estimate();
		if( nextFreeSlot != null) {			
			((Estimate) msg).earliestStartTime.put((SWFJob)job, nextFreeSlot.getAdvent()); 
			((Estimate) msg).earliestFinishingTime.put((SWFJob)job,nextFreeSlot.getCessation());
			((Estimate) msg).validity = null; 			//Does not apply, it must always be sampled.
		} 
		
		return msg;
	}
		
	
	
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