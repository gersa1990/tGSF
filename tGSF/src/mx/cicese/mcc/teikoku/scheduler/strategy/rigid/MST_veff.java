package mx.cicese.mcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MST_veff implements mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy {

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
		public MST_veff() {
			this.site = null;
			this.initialized = false;
		}

		
		/**
		 * 	Schedules a rigid job to a site based on the MinProcessorLoad criteria
		 *  taking into account site energy efficiency and speed
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
			Map<UUID,SiteInformationData> statInfo = null;
				
			// Initialize all known sites minWt
			if(!this.initialized) {
				this.gInfoBroker = site.getGridInformationBroker();
				this.initialized = true;
			}
			
			LinkedList<Job> jobs = new LinkedList<Job>();
			jobs.add(job);
			
			avail = gInfoBroker.pollAllSites(InformationType.EARLIEST_START_TIME, jobs, null);
			statInfo = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
			for(UUID s : avail.keySet()) {

				Estimate a = (Estimate)avail.get(s);
				Hashtable<SWFJob, Instant> f = a.earliestStartTime;
				double c = Double.POSITIVE_INFINITY;
				double effi = ((SiteStatusInformation)statInfo.get(s)).siteEnergyEfficiency;
				double vi = ((SiteStatusInformation)statInfo.get(s)).speed;
				if (f.isEmpty() == false) {
					Instant h = f.get(job);
					c = h.timestamp()/(effi*vi);
				}
				
				if( c < minCompletion ) {
					minCompletion = c;
					minSite = s; 
				}
			}
			
			if(minSite == null)
				System.out.print("Error para el trabajo " + ((SWFJob)job).getJobNumber());	
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
}