/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import de.irf.it.rmg.core.util.time.Instant;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */

public class SiteStatusInformation extends Entity 
	implements SiteInformationData {
	
	public long maxTotalJobs;
	
	public long maxRunningJobs;
	
	public long maxWaitingJobs;
	
	public long maxPreLRMSWaitingJobs;
	
	public boolean servingState;
	
	public long totalJobs;
	
	public long runningJobs;
	
	public long localRunningJobs;
	
	public long waitingJobs;
	
	public long localWaitingJobs;
	
	public long suspendedJobs;
	
	public long localSuspendenJobs;
	
	public long preLRMSWaitingJobs;
	
	public long localPreLRMSWaitingJobs;
	
	public double estimatedAveragewaitingTime;
	
	public double estimatedWorstWaitingTime;
	
	public long numProcessors; 
	
	public long numAvailableProcessors;
	
	//Macadamia
	public double coreEnergyConsumption;
	public double siteEnergyEfficiency;
	public double speed;
	
	public Instant validity;
	
	public InformationType infoType;
		
	public SiteStatusInformation() {
		super();
		initialize();
	}
	
	public SiteStatusInformation(String name) {
		super(name);
		initialize();
	}
	
	private void initialize() {
		this.maxTotalJobs = 0;
		this.maxRunningJobs = 0;
		this.maxWaitingJobs = 0;
		this.maxPreLRMSWaitingJobs = 0;
		this.servingState = false;
		this.totalJobs = 0;
		this.runningJobs = 0;
		this.localRunningJobs = 0;
		this.waitingJobs = 0;
		this.localWaitingJobs = 0;
		this.suspendedJobs = 0;
		this.localSuspendenJobs = 0;
		this.preLRMSWaitingJobs = 0;
		this.localPreLRMSWaitingJobs = 0;
		this.estimatedAveragewaitingTime = 0;
		this.estimatedWorstWaitingTime = 0;
		this.numProcessors = 0;
		this.numAvailableProcessors = 0;
		
		//Macadamia
		this.coreEnergyConsumption = 0.0;
		this.siteEnergyEfficiency = 0.0;
		this.speed = 0.0;
		
		this.validity = null;
		this.infoType = InformationType.STATUS;	
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 * 
	 */
	public InformationType getType() {
		return this.infoType;
	}
	
	public Instant getValidity() {
		return this.validity;
	}
}
