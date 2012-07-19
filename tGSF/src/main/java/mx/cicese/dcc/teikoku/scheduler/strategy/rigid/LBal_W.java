package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.collections.SortedQueue;

public class LBal_W implements RStrategy {
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
	 * The grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	/**
	 * Mean parallel load
	 */
	private double mean_PL;
	
	/**
	 * Class constructor
	 */
	public LBal_W() {
		this.site = null;
		this.knowSites = new LinkedList<UUID>();
		this.initialized = false;
		this.mean_PL = 0.0;
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
		Map<UUID,SiteInformationData> statInfo = null;	//Site status information
		double minRatio = Double.MAX_VALUE;
		UUID minSite = null; 
		
		
		
		// Creacion de la instancia del sistema de informacion
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			this.gInfoBroker.bind();
			initialize(((GridInformationBrokerImpl)gInfoBroker).getKnownSites());
		}
		
		
		double p = ((SWFJob)job).getRequestedTime();
		int size = ((SWFJob)job).getRequestedNumberOfProcessors();
		double w = p * size;
		int mglobal = 0;
		
		// Calcular PL para cada maquina
		Hashtable<UUID, Number> ssj = new Hashtable<UUID,Number>();
		//Vector <Number> ssj = new Vector <Number>();
		int i =0;
		for(UUID sId : this.knowSites) {
			Site s = this.gInfoBroker.getKnownSite(sId);
			Set<Job> sj = s.getScheduler().getSchedule().getScheduledJobs();
			SortedQueue<Job> qj = s.getScheduler().getQueue();
			double sum = 0 ;
			// Sacar informacion correspondiente a las caracteristica de cada trabajo
			for(Job j : sj) {
				int s_j = ((SWFJob)j).getRequestedNumberOfProcessors();
				double p_j =  ((SWFJob)j).getRequestedTime();
				double w_j = s_j * p_j;
				sum += w_j;
			}
			
			// calcular tamanio de tareas que estan en cola
			for(Job j : qj) {
				int s_j = ((SWFJob)j).getRequestedNumberOfProcessors();
				double p_j =  ((SWFJob)j).getRequestedTime();
				double w_j = s_j * p_j;
				sum += w_j;
			}
			int m = s.getSiteInformation().getNumberOfAvailableResources();
			mglobal += m;
			sum = sum + w;
			sum = sum/m;
			ssj.put(s.getUUID(), sum); // .add(sum);
			
		}
		double media = 0;
		
		for(Number n: ssj.values()) 
		media += n.doubleValue();

		media = media / ssj.size();
		
		//objetivo elegir el sitio 
		//for (Number n: ssj)
		  double minds = Double.MAX_VALUE;
		  UUID minsite = null;
		  for(UUID k: ssj.keySet())
		  {
			  Number n = ssj.get(k);
			  double ds = Math.sqrt((1/mglobal)*Math.pow((n.doubleValue() - media),2));
			  if (ds < minds){
				  minds = ds;
				  minsite = k;
			  }
		  }
		
		//Create the allocation entry to schedule
		AllocationEntry entry = new AllocationEntry((SWFJob)job, minsite, -1);
		
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

