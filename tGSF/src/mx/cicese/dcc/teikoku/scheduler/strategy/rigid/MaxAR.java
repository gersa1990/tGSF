package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.Map;
import java.util.UUID;

/**
 * Allocates a job to the site with maximum available resources
 * 
 * @author ahirales
 *
 */
public class MaxAR implements RStrategy {
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	
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
	public MaxAR() {
		this.site = null;
		this.initialized = false;
	} // End MinProcessorLoad

	
	/**
	 * 	Schedules a rigid job to a site based on the MinProcessorLoad criteria
	 *  (Heuristic #2)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		double minRatio = Double.MIN_VALUE;
		UUID minSite = null; 
		Map<UUID,SiteInformationData> avail = null;
		
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		
		// Initialize all known sites minWt
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			this.initialized = true;
		}
		
		avail = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
		
		/* (ni/mi) 
		 * ni	- the number of assiged jobs to machine M_i 
		 * mi	- the size of machine M_i
		 */
		for(UUID s : avail.keySet()) {
			double ni = (double) ((SiteStatusInformation)avail.get(s)).numAvailableProcessors;
			double mi = (double) ((SiteStatusInformation)avail.get(s)).numProcessors;
			double ratio = (ni/ mi);
			if( ratio > minRatio && size <= mi){
				minRatio = ratio;
				minSite = s;
			} //End if
		} //End for
		
		// Chose the site with maximum resources
		long max = Long.MIN_VALUE;
		if( minSite == null ){
			for(UUID s : avail.keySet()) {
				double mi = (double) ((SiteStatusInformation)avail.get(s)).numProcessors;
				if(mi > max) {
					max = (long) mi;
					minSite = s;
				}
			}
		}
		
		
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
}
