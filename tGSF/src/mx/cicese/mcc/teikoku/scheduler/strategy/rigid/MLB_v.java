package mx.cicese.mcc.teikoku.scheduler.strategy.rigid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class MLB_v implements mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy {
	
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	/**
	 * Min waiting time values
	 */
	private Map<UUID,Double> minLb;
	
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
	public MLB_v() {
		this.site = null;
		this.minLb = new HashMap<UUID,Double>();
		this.initialized = false;
	}

	
	/**
	 * 	Schedules a rigid job to a site based on the MinLowerBound criteria
	 *  taking into account site speed
	 *  (Heuristic #5)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		double minRatio = Double.MAX_VALUE;
		UUID minSite = null; 
		Map<UUID,SiteInformationData> statInfo = null;
		
		double size = (double) ((SWFJob)job).getRequestedNumberOfProcessors();
		double p_k = (double) ((SWFJob)job).getRequestedTime();
		
		// Initialize all known sites minWt
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			initialize(((GridInformationBrokerImpl)gInfoBroker).getKnownSites());
		}

		statInfo = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
				
		for(UUID s : statInfo.keySet()) {
			double mi = (double) ((SiteStatusInformation)statInfo.get(s)).numProcessors;
			double vi = ((SiteStatusInformation)statInfo.get(s)).speed;
			double ratio = ((size * p_k)/ (vi*mi)) + this.minLb.get(s).doubleValue();
			if(ratio < minRatio && size <= mi){
				minRatio = ratio;
				minSite = s;
			}
		}
		this.minLb.put(minSite, minRatio);
		AllocationEntry entry = new AllocationEntry((SWFJob)job, minSite, -1);
		
		return entry;
	}
	
	
	/**
	 * Setter method, sets the component site
	 * 
	 * @param site	the site this component is associated to
	 */
	public void setSite(Site site) {
		this.site = site;
	}
	
	
	/**
	 * Initializes the min waiting times to 0 for all known sites.
	 * 
	 * @param knownSites	the list of all known sites of type
	 * 						List<UUID>
	 */
	private void initialize(List<UUID> knownSites) {
		for(UUID s: knownSites)
			this.minLb.put(s, 0.0);
		this.initialized = true;
	}

}
