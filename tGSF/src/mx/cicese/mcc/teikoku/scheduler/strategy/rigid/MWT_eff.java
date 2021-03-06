package mx.cicese.mcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.List;


public class MWT_eff implements mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy {
	
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	/**
	 * Min waiting time values
	 */
	private Map<UUID,Double> minWt;
	
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
	public MWT_eff() {
		this.site = null;
		this.minWt = new HashMap<UUID,Double>();
		this.initialized = false;
	}

	
	/**
	 * 	Schedules a rigid job to a site based on the MinWaitingTime criteria
	 *  taking into account site energy efficiency 
	 *  (Heuristic #8)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		Map<UUID,SiteInformationData> estmInfo = null;	//Job estimation information
		Map<UUID,SiteInformationData> statInfo = null;	//Site status information
		double minRatio = Double.MAX_VALUE;
		UUID minSite = null; 

		// Initialize all known sites minWt
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			initialize(((GridInformationBrokerImpl)gInfoBroker).getKnownSites());
		}
		
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double p = ((SWFJob)job).getRequestedTime();

		
		LinkedList<Job> jobs = new LinkedList<Job>();
		jobs.add(job);
		// Poll for status and estimates
		estmInfo = gInfoBroker.pollAllSites(InformationType.ESTIMATE, jobs, null);
		statInfo = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
		
		for(UUID s : estmInfo.keySet()) {
			double ratio = 0.0;
			
			long mi = ((SiteStatusInformation)statInfo.get(s)).numProcessors;
			double ni = ((SiteStatusInformation)statInfo.get(s)).totalJobs;
			double effi = ((SiteStatusInformation)statInfo.get(s)).siteEnergyEfficiency;
			double c = ((Estimate)estmInfo.get(s)).earliestFinishingTime.get(job).timestamp();
			
			// The heuristic has an error if n_i cero, to avoid getting NAN, we do the following
			ratio = ( ni != 0 )? (((c - r - p) / (effi*ni)) + minWt.get(s).doubleValue())
					: minWt.get(s).doubleValue();
			
			if( ratio < minRatio && size <= mi){
				minRatio = ratio;
				minSite = s;
			}
		}
		minWt.put(minSite, minRatio);		
		//Create the allocation entry to schedule
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
			this.minWt.put(s, 0.0);
		this.initialized = true;
	}	
}