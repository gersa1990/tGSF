/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Queue;

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */

public class GridInformationBrokerImpl implements GridInformationBroker{

	/**
	 * The site the grid information broker belongs to
	 * 
	 */
	Site site;
	
	
	/**
	 * The list of known sites
	 * 
	 */	
	List<SiteInformationBroker> knownSites = null;
	

	/**
	 * Registers the known sites
	 * 
	 */
	public void bind( ) {
		knownSites = new LinkedList<SiteInformationBroker>();
		
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for (Site s:sites.values()){
			if (!(s == this.getSite())){
				knownSites.add(s.getSiteInformationBroker());
			}
		} 
			
		// Ordenar los sitios despues de agregar
		Collections.sort(knownSites,new SortSitesBySize());
		
	}
	
	/**
	 * Polls all site information brokers for a requested type of information
	 * 
	 * @param infoType
	 *		Two type of information data structures can be polled from
	 *		each site information broker: information that gives the 
	 *		status of the site (SiteStatusInformation) and information that
	 *		allows to get estimates. (see {@link InformationType})
	 *
	 *@return A map containing polled data from all site information brokers.
	 *        
	 */
	public Map<UUID,SiteInformationData> pollAllSites(InformationType infoType, Queue<Job> jobs, JobControlBlock jcb) {
		Map<UUID,SiteInformationData> polledData = new LinkedHashMap<UUID,SiteInformationData>();
		
		if(knownSites == null)
			bind();
		
		for (SiteInformationBroker s:knownSites){
			SiteInformationData data = s.getPublicInformation(infoType, jobs, jcb);
			if(data != null)
				polledData.put(s.getSite().getUUID(), data);
		}
		
		return polledData;
	}
	
	
	
	/**
	 * Polls a single site information brokers for a requested type of information
	 * 
	 * @param infoType
	 *		Two type of information data structures can be polled from
	 *		each site information broker: information that gives the 
	 *		status of the site (SiteStatusInformation) and information that
	 *		allows to get estimates. (see {@link InformationType})
	 *        
	 * @param id
	 * 		The unique identifyer of the site to be polled.
	 * 
	 * @return The requested site information       
	 */	
	public SiteInformationData pollSite(UUID id, InformationType infoType, Queue<Job> jobToSchedule,  JobControlBlock jcb){
		
		if(knownSites == null)
			bind();
		
		SiteInformationBroker sInfBroker = this.getInformationBroker(id);
		SiteInformationData data = sInfBroker.getPublicInformation(infoType, jobToSchedule, jcb);
		return data;
	}
	
	
	/**
	 * Retrieves the site this grid information broker belongs to
	 *  
	 * @return The requested grid information broker sites.        
	 */
	public Site getSite() {
		return this.site;
	}
	
	
	/**
	 * Sets the site for this grid information broker
	 *  
	 */
	public void setSite(Site site) {
		this.site = site;
	}
	
	/**
	 * Given an information broker UUID, gets the information broker 
	 * instance. 
	 * 
	 * @return 	the instance of the requested information broker 
	 * 			of type SiteInformationBroker
	 */	
	public SiteInformationBroker getInformationBroker(UUID id) {
		SiteInformationBroker sInfBroker = null;
		
		for (SiteInformationBroker s:knownSites)
			if( s.getUUID().equals(id)) { 
				sInfBroker = s;
				break;
			}
		
		return sInfBroker;
	}
	
	
	/**
	 * Gets all known sites UUID's 
	 * 
	 * @return 	a list containing the UUID of all known sites. The list
	 * 			is of type List<UUID>        
	 */	
	public List<UUID> getKnownSites() {
		List<UUID> knownSites = new LinkedList<UUID>();
		
		List<ActivityBroker> knownBrokers = 
			this.site.getGridActivityBroker().getKnownActivityBrokers();
		for(ActivityBroker b : knownBrokers)
			knownSites.add(b.getSite().getUUID());
		
		return knownSites;
	}//End getKnownSites
	
	
	
	/**
	 * Gets all known sites UUID's 
	 * 
	 * @return 	a list containing the UUID of all known sites. The list
	 * 			is of type List<UUID>        
	 */	
	public Site getKnownSite(UUID sId) {
		Site x = null;
		
		for(SiteInformationBroker b : knownSites)
			if(b.getSite().getUUID().equals(sId)) {
				x = b.getSite();
				break;
			}
		return x;
	}//End getKnownSites
	
	
	/**
	 * Gets the host information broker id
	 * 
	 * @param 	the target site information broker id 
	 * @return	the host activity broker id	
	 */
	public UUID getKnownSiteUUID(UUID sInfBrokerId){
		UUID siteID = null;
	
		List<ActivityBroker> knownBrokers = 
			this.site.getGridActivityBroker().getKnownActivityBrokers();
		
		for(ActivityBroker b : knownBrokers) {
			if(b.getSite().getSiteInformationBroker().getUUID().equals(sInfBrokerId)) {
				siteID = b.getSite().getUUID();
				break;
			}
		} //End for
		
		return siteID;
	}
	
	
	
	
	
	/**
	 * Retrieves a set of known information brokers
	 * 
	 * @return	a list known information brokers
	 */
	public List<UUID> getKnownInformationBrokers() {
		List<UUID> knownInfBrokers = new LinkedList<UUID>();
		
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for (Site s:sites.values()){
			if (!(s == this.getSite())){
				knownInfBrokers.add(s.getSiteInformationBroker().getUUID());
			} // End if
		} // End for
		
		return knownInfBrokers;
	} // getKnownInformationBrokers
	
}
