package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import java.util.List;
import java.util.UUID;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class RRobin extends Admissible implements RStrategy {
	
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	
	/**
	 * Class constructor
	 */
	public RRobin() {
		this.site = null;
	} // End MinProcessorLoad

	
	/**
	 * 	Schedules a rigid job to a site based on the MinLowerBound criteria
	 *  (Heuristic #5)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		UUID siteId = null;
		int i = 0;
		List<UUID> knownSites = null;
		
		/* Admissibility */
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		super.initialize(site);
		knownSites = super.getAdmissableSet(size);
				
		if (i >= knownSites.size())
			i = 0;
		
		siteId = knownSites.get(i);
		i++;
				
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
}
