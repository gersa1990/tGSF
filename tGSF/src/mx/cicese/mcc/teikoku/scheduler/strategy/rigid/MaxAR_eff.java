package mx.cicese.mcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MaxAR_eff implements mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy {
	
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
	public MaxAR_eff() {
		this.site = null;
		this.initialized = false;
	}

	
	/**
	 * 	Schedules a rigid job to a site based on the MaxAvailableProcessors criteria
	 *  taking into account site energy efficiency
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		Map<UUID,SiteInformationData> estmInfo = null;	//Job estimation information
		Map<UUID,SiteInformationData> statInfo = null;	//Site status information
		double maxRatio = 0.0;
		UUID maxSite = null; 

		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
		}

		//Get the job size
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		
		LinkedList<Job> jobs = new LinkedList<Job>();
		jobs.add(job);
		
		// Poll for status and estimates
		estmInfo = gInfoBroker.pollAllSites(InformationType.ESTIMATE, jobs, null);
		statInfo = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
		
		for(UUID s : estmInfo.keySet()) {
			double ratio = 0.0;
			
			//Total number of processors in site s
			long mi = ((SiteStatusInformation)statInfo.get(s)).numProcessors;

			//Available processors in site s
			long availi = ((SiteStatusInformation)statInfo.get(s)).numAvailableProcessors;

			//Energy efficiency of site s
			double effi = (((SiteStatusInformation)statInfo.get(s)).siteEnergyEfficiency);
			
			ratio = availi*effi/mi;
			
			if (ratio > maxRatio && size <= mi) {
				maxRatio = ratio;
				maxSite = s;
			}
		}
		//Create the allocation entry to schedule
		AllocationEntry entry = new AllocationEntry((SWFJob)job, maxSite, -1);
		
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
}
