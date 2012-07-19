package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.collections.SortedQueue;



public class LBal_T implements RStrategy {
	/**
	 * The site this metric is registered to 
	 */
	private Site site;
	
	/**
	 * Min waiting time values
	 */
	private LinkedList<UUID> knowSites;
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	/**
	 * Used to initialize the Grid size
	 */
	private boolean initializedGSize;
	
	
	/**
	 * The grid information broker
	 */
	private GridInformationBroker gInfoBroker;
		
	/**
	 * The Grid size
	 */
	private int gridSize;
	
	/**
	 * Class constructor
	 */
	public LBal_T() {
		this.site = null;
		this.knowSites = new LinkedList<UUID>();
		this.initialized = false;
		this.initializedGSize = false;
		this.gridSize = 0;
	} // End MinLoadBalances
	
	/**
	 * 	Schedules a rigid job to a site based on the min load balancing criteria
	 *  (Heuristic #13)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job) {
		UUID minSite = null; 
		double minSTD = Double.MAX_VALUE;		
		
		// Create the Grid information broker instance
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			this.gInfoBroker.bind();
			initialize(((GridInformationBrokerImpl)gInfoBroker).getKnownSites());
		}
		
		// Get the size of the task to allocate	
		int size = ((SWFJob)job).getRequestedNumberOfProcessors();
		double jobTime = ((SWFJob)job).getRequestedTime();
		

		// Get the total size of the Grid machine
		if(!this.initializedGSize){
			for(UUID sId : this.knowSites) {
				Site s = this.gInfoBroker.getKnownSite(sId);
				int m = s.getSiteInformation().getNumberOfAvailableResources();
				if(size <= m)
					this.gridSize += m;
			}
			this.initializedGSize = true;
		}

										
		// Estimate PL for machines were the job can be allocated
		Hashtable<UUID, Number> ParLoad = new Hashtable<UUID,Number>();
		Hashtable<UUID, Number> mSize = new Hashtable<UUID,Number>();
		for(UUID sId : this.knowSites) {
			Site s = this.gInfoBroker.getKnownSite(sId);
			// Get the size of the machine
			int m = s.getSiteInformation().getNumberOfAvailableResources();
			
			if( size <= m ) {
				Set<Job> sj = s.getScheduler().getSchedule().getScheduledJobs();
				SortedQueue<Job> qj = s.getScheduler().getQueue();
				double sum = 0 ;

				// Estimate PL by adding scheduled jobs
				for(Job j : sj) {
					double s_j = ((SWFJob)j).getRequestedTime();
					sum += s_j;
				}
			
				// Estimate PL by adding queued jobs
				for(Job j : qj) {
					double s_j = ((SWFJob)j).getRequestedTime();
					sum += s_j;
				}
					
				// Add the job to the calculation
				UUID id = s.getUUID();
				ParLoad.put(id, sum);
				mSize.put(id, m);
			} // End if
		} // End known sites
		

		Hashtable<UUID, Number> PL = new Hashtable<UUID,Number>();
		for(UUID targetSite : ParLoad.keySet()) {
			double meanPL = 0;
				
			// Compute the PL values
			for(UUID gSite : ParLoad.keySet()) {
				double pl = 0;
				if (targetSite == gSite) 
					pl = (ParLoad.get(gSite).doubleValue() + jobTime) / mSize.get(gSite).doubleValue();
				else
					pl = ParLoad.get(gSite).doubleValue() / mSize.get(gSite).doubleValue();
				PL.put(gSite, pl);
				meanPL += pl;
			}
			
			// Compute the mean PL
			meanPL = meanPL / PL.size();
			
			// Compute the std deviation
			double sumDiff = 0;
			for(UUID s : PL.keySet())
				sumDiff += Math.pow((PL.get(s).doubleValue() - meanPL),2);
			double std = Math.sqrt((1.0/(double)gridSize)* sumDiff);
			
			if(std < minSTD) {
				minSTD = std;
				minSite = targetSite;
			}
		} //End for
		
	
		//Create the allocation entry to schedule
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
			this.knowSites.add(s);
		this.initialized = true;
	} //End initialize


}
