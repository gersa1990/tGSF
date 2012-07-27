package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MWCTW implements RStrategy {
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	/**
	 * Min completion time values
	 */
	private Map<UUID,Double> minCT;
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	
	/**
	 * The grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	
	/**
	 * Class constructor
	 */
	public MWCTW() {
		this.site = null;
		this.initialized = false;
		this.minCT = new HashMap<UUID,Double>();
	} // End MinWeightedCompleteTime

	
	/**
	 * 	Schedules a rigid job to a site based on the MinProcessorLoad criteria
	 *  (Heuristic #2)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		double minCompletion = Double.MAX_VALUE;
		UUID minSite = null; 
		Map<UUID,SiteInformationData> avail = null;
			
		// Initialize all known sites minWt
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			initialize(((GridInformationBrokerImpl)gInfoBroker).getKnownSites());
		}
		
		LinkedList<Job> jobs = new LinkedList<Job>();
		jobs.add(job);
		
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		avail = gInfoBroker.pollAllSites(InformationType.ESTIMATE, jobs, null);
		for(UUID s : avail.keySet()) {
			double c = ((Estimate)avail.get(s)).earliestFinishingTime.get(job).timestamp();

			// Weight is added here!
			c = (c * size) + minCT.get(s).doubleValue();
			
			if( c < minCompletion ) {
				minCompletion = c;
				minSite = s; 
			} //End if
		} //End for
		
		minCT.put(minSite, minCompletion);
		AllocationEntry entry = new AllocationEntry((SWFJob)job, minSite, -1);
		
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
	
	
	/**
	 * Initializes the min waiting times to 0 for all known sites.
	 * 
	 * @param knownSites	the list of all known sites of type
	 * 						List<UUID>
	 */
	private void initialize(List<UUID> knownSites) {
		for(UUID s: knownSites)
			this.minCT.put(s, 0.0);
		this.initialized = true;
	} //End initialize

}
