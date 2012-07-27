/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Queue;

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */

public interface GridInformationBroker {

	
	/**
	 * The grid information broker id
	 * 
	 */
	public void bind( );
	
	
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
	public Map<UUID,SiteInformationData> pollAllSites(InformationType infoType, Queue<Job> jobs,  JobControlBlock jcb); 
	
	
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
	 * 		The unique identifier of the site to be polled.
	 * 
	 * @return The requested site information       
	 */	
	public SiteInformationData pollSite(UUID id, InformationType infoType, Queue<Job> jobs,  JobControlBlock jcb);
	
	
	/**
	 * Retrieves the site this grid information broker belongs to
	 *  
	 * @return The requested grid information broker sites.        
	 */
	public Site getSite();
	
	
	/**
	 * Sets the site for this grid information broker
	 *  
	 */
	public void setSite(Site site);
	
	
	/**
	 * Returns the set of known sites
	 * 
	 * @return
	 */
	public List<UUID> getKnownSites();
	
	
	/**
	 * Gets the host information broker id
	 * 
	 * @param 	the target site information broker id 
	 * @return	the host activity broker id	
	 */
	public UUID getKnownSiteUUID(UUID sInfBrokerId);
	
	
	/**
	 * Retrieves a set of known information brokers
	 * 
	 * @return	a list known information brokers
	 */
	public List<UUID> getKnownInformationBrokers();
	
	
	public Site getKnownSite(UUID sId);

} //GridInformationBroker
