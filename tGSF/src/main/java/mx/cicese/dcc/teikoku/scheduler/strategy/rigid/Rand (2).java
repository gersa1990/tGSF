package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;


import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.UUID;
import java.util.List;


public class Rand extends Admissible implements RStrategy{
	
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	
	/**
	 * Class constructor
	 */
	public Rand() {
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
		long numKnownSites = 0;
		List<UUID> knownSites = null;
		
		
		/* Admissibility */
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		super.initialize(site);
		knownSites = super.getAdmissableSet(size);
		numKnownSites = knownSites.size(); 
			
		/* Select a machine randomly */
		i = (int) Math.round((Math.random()*(numKnownSites-1)));
		siteId = knownSites.get(i);
		
		//Create the allocation entry to schedule
		AllocationEntry entry = new AllocationEntry((SWFJob)job, siteId, -1);
		
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
	
} // End Parallel
