package mx.cicese.mcc.teikoku.scheduler.SLA.strategy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.Accepter;

public class SiteSLAAccepter {

	private boolean initialized;
	
	final private static Log log = LogFactory.getLog(SiteSLAAccepter.class);
	
	
	/**
	 * The grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	
	
	public SiteSLAAccepter()
	{
		this.initialized = false;
	}
	
	public void initialize(Site site) {
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			this.gInfoBroker.bind();
			
			this.initialized = true;
		}
	}
	
	/**
	 * Retrieves if the machine can or not accept the given job 
	 * @param jobSize, the job size
	 * @return a boolean value that indicates if the job can be executed in the site
	 */
	public boolean getAcceptanceTest (Job job, UUID siteUUID) {
	
		boolean admissible = false;
		Site tentativeSite = gInfoBroker.getKnownSite(siteUUID);
		Set<Job> scheduledJobs = tentativeSite.getScheduler().getSchedule().getScheduledJobs();		
		Accepter siteAccepter = tentativeSite.getScheduler().getLocalAccepter();
		SortedQueue<Job> schedulerQueue = tentativeSite.getScheduler().getQueue();
		SortedQueue<Job> copySchedulerQueue = new SortedQueueBinarySearchImpl<Job>();
		copySchedulerQueue.setOrdering(schedulerQueue.getOrdering());
		
		//In this part we can not modify the jobs because they may or not may be modified as result of the
		// acceptance policy, therefore if some site rejects a set of jobs these jobs should leave in a 
		// state before the accepting proof was made. Therefore we clone this objects and then dispose the copies.
		
		copySchedulerQueue.addAll(schedulerQueue);
		
		Job clonedIncommingJob = (Job)job.clone();
		copySchedulerQueue.add(clonedIncommingJob);
		
		
		Iterator<Job> tIt = scheduledJobs.iterator();
		while(tIt.hasNext())
		{
			Job scheduledJob = tIt.next();
			copySchedulerQueue.add((Job)scheduledJob.clone());
		}
		
		Iterator<Job> it = copySchedulerQueue.iterator();
		while(it.hasNext())
		{
			//the running time information is updated
			Job itJob = it.next();
			if(itJob.getLifecycle().getLastCondition() == State.STARTED)
			{
				
				itJob.getRuntimeInformation().addRunningTime();
			}
		}
		copySchedulerQueue.reorder();
		admissible = 
		siteAccepter.examine(copySchedulerQueue, clonedIncommingJob);
		
		
		if(!admissible) 
		{
			String msg = "The released job " + job.getName() + " was rejected by the site " + tentativeSite.getName();
			log.info(msg);
		}
		else
		{
			String msg = "The released job " + job.getName() + " was accepted by the site " + tentativeSite.getName();
			log.info(msg);
		}
		
		return admissible;
	}
	
	
}
