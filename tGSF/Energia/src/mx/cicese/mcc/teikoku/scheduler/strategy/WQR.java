package mx.cicese.mcc.teikoku.scheduler.strategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Admissible;
import mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy;
import mx.cicese.mcc.teikoku.scheduler.util.ReplicationControl;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class WQR  extends Admissible implements RStrategy{

	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	/**
	 * Class constructor
	 */
	public WQR() {
		this.site = null;
	} // End MinWaitingTime
	
	
	/**
	 * 	Schedules a rigid job to a site based on the Random criteria
	 *  (Heuristic #1)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		UUID siteId = null;
		int i = 0;
		List<UUID> knownSites = null;
		List<UUID> availableSites = new ArrayList();
		AllocationEntry entry = null;
		GridInformationBroker gInfoBroker = job.getReleasedSite().getGridInformationBroker();
		
		/* Admissibility */
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		super.initialize(site);
		//
		knownSites = super.getAdmissableSet(size); 
			
		for(UUID s : knownSites)
			if(gInfoBroker.getKnownSite(s).getReleasedSiteQueue().getQueue().isEmpty()&&
					gInfoBroker.getKnownSite(s).getScheduler().getSchedule().getScheduledJobs().isEmpty())
				availableSites.add(s);
		
		if(availableSites.size()>0)
		{
			/* Select a machine randomly */
			i = (int) Math.round((Math.random()*(availableSites.size()-1)));
			siteId = availableSites.get(i);
			
			if(job.getReleasedSite().getReplicationControl().isReplicable(job))
			{
				job.getReleasedSite().getReplicationControl().replicate(job);
			}
			//Create the allocation entry to schedule
			entry = new AllocationEntry((SWFJob)job, siteId, -1);
		}
		else
		{
			//queue
			job.getReleasedSite().getReleasedSiteQueue().queue(job);
		}
		return entry;
		
	} // End schedule
	
	
	/**
	 * Setter method, sets the component site
	 * 
	 * @param site	the site this component is associated to
	 */
	public void setSite(Site site) {
		this.site = site;
	}// End setSite
	
	public Site getSite(){
		return this.site;
	}
	
} // End Parallel
