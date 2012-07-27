package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MST implements RStrategy {

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
		public MST() {
			this.site = null;
			this.initialized = false;
		} // End MinCompletionTime

		
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
				this.initialized = true;
			}
			
			LinkedList<Job> jobs = new LinkedList<Job>();
			jobs.add(job);
			//System.out.println("Solicitando estimacion para " + job.getName() );
			
			avail = gInfoBroker.pollAllSites(InformationType.EARLIEST_START_TIME, jobs, null);
			for(UUID s : avail.keySet()) {
				
				// This function returns the minimum of each site
				double c = ((Estimate)avail.get(s)).earliestStartTime.get(job).timestamp();
				if( c < minCompletion ) {
					minCompletion = c;
					minSite = s; 
				} //End if
			} //End for
			
			if(minSite == null)
				System.out.print("Error para el trabajo " + ((SWFJob)job).getJobNumber());
			
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
