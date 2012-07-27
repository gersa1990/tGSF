/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.Hashtable;
import java.util.Vector;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
public class Estimate extends Entity 
	implements SiteInformationData {
	
	/**
	 * Models the earliest start time when a given job 
	 * can start execution at the given site.
	 */
	public Hashtable<SWFJob,Instant> earliestStartTime;
	
	public Vector<Instant> e;
	
	/**
	 * Models the earliest finishing time when a given 
	 * job can finish execution at the given site.
	 */
	public Hashtable<SWFJob,Instant> earliestFinishingTime;
	
	/**
	 * Represents the time the machine is available to schedule
	 * some job. It does not consider if it can schedule it. It
	 * only says that the machine can schedule something. 
	 */
	public Hashtable<SWFJob,Instant> earliestAvailTime;
	
	/**
	 * Used to set an expidation time. Hence the information should be
	 * considered stale. 
	 */
	public Instant validity;
	
	/**
	 * Describes the information type this class implementation provides
	 */
	public InformationType infoType;
	
	/**
	 * Class constructor
	 */
	public Estimate() {
		super();
		this.validity = null;
		this.infoType = InformationType.ESTIMATE;
		this.earliestStartTime = new Hashtable<SWFJob,Instant>();
		this.earliestFinishingTime = new Hashtable<SWFJob,Instant>();
		this.earliestAvailTime = new Hashtable<SWFJob,Instant>();
	}
	
	/**
	 * Getter, returns the type of information this class models 
	 * 
	 * @return		this class information type
	 */
	public InformationType getType() {
		return this.infoType;
	}
	
	/**
	 * Getter, returns the time when this information should be considered
	 * stale.
	 * 
	 * @return		an instance of time
	 */
	public Instant getValidity() {
		return this.validity;
	}
	
}
