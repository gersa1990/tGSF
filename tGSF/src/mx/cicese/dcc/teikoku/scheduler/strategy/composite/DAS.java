package mx.cicese.dcc.teikoku.scheduler.strategy.composite;

import java.util.Iterator;
import java.util.List;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.priority.Priority;
import mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import org.apache.commons.configuration.Configuration;


/**
 *	Dynamic Scheduling Mechanism (Strategy)
 *	- Since policy and Scheduling Mechanism is separated.
 *	- Policies don't change, mechanism do changes.  
 * 
 * @author Adan Hirales Carbajal
 * @author Andrei Tchernykh
 */

public class DAS implements CStrategy {
	/**
	 * The DSS labeling strategy
	 */
	private Priority<SWFJob,Precedence> priority;
	
	/**
	 * The labeling strategy name
	 */
	private String labelingStrategyName;
	
	/**
	 * DSS Allocation strategy
	 */
	private RStrategy strategy;
	
	/**
	 * The site name
	 */
	private Site site;
	
	
	public void initialize(){
		labelingStrategyName = new String();
		try {
			loadLocalRigidStrategy();
		} catch (InitializationException ex) {
			//Logger.getLogger(DSS.class.getName()).log(Level.SEVERE, null, ex);
		} //try
	} // initialize

	
	public void schedule(Job job, JobControlBlock jcb) {
		long r = -1;
		
		// Get independent jobs and schedule them.
		/* Create the list of schedulable jobs */
		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
		List<SWFJob> indJobs = CompositeJobUtils.getIndependentJobs(g);
				
		/* Should use priority to iterate over jobs. Have not figured out its use */
		for(Iterator it = indJobs.iterator(); it.hasNext();) {
			SWFJob m = (SWFJob)it.next();
			/* Update m's release time to max AFT of predeccesor nodes (if any) */
			if(g.inDegree(m) != 0) 
				r = DateHelper.convertToSeconds(CompositeJobUtils.getMaximumStartTime(g, m, jcb));
			else 
				r = DateHelper.convertToSeconds(Clock.instance().now().timestamp());
			m.setSubmitTime(r);
			// The moment the job is freed, then the member job state must be set to RELEASED
			m.getLifecycle().addEpisode(State.RELEASED);
			
			//Set the job type as independent, then reset to its original type.
			JobType originalType = m.getJobType();
			m.setJobType(JobType.INDEPENDENT);
			// The job is then scheduled.
			AllocationEntry entry = strategy.schedule(m);
			m.setJobType(originalType);
			
			//Add the job priority
			long rank = jcb.getRanking(this.labelingStrategyName, m);
			m.setPriority(rank);
						
			jcb.addEntry(entry, entry.getDestination());
		}//End for
	} // End schedule

	
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb) {
		priority.compute(g);
		jcb.setRanking(this.labelingStrategyName,priority.getRanking());
	} // getRanking

	
	public void setSite(Site site) {
		this.site = site;
	} // setSite
	
    @SuppressWarnings("unchecked")
	protected void loadLocalRigidStrategy() throws InitializationException {
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, this.site.getName(),
		Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_ALLOCATION_CLASS);

		if (key == null) {
			String msg = "local strategy entry (" + ComputeSite.CONFIGURATION_SECTION
				+ "[" + this.site.getName()	+ "]"
				+ Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_ALLOCATION_CLASS
				+ ") not found in configuration";
				throw new InitializationException(msg);
		} // if

		String className = c.getString(key);
		try {
			this.strategy = ClassLoaderHelper.loadInterface(className,RStrategy.class);
			this.strategy.setSite(site);
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch


		key = ConfigurationHelper.retrieveRelevantKey(c, this.site.getName(),
				Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_PRIORITY_CLASS);

		if (key == null) {
				String msg = "local strategy entry (" + ComputeSite.CONFIGURATION_SECTION
				+ "[" + this.site.getName()
				+ "]" + Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_PRIORITY_CLASS
				+ ") not found in configuration";
				throw new InitializationException(msg);
		} // if

		className = c.getString(key);
		try {
			this.priority = ClassLoaderHelper.loadInterface(className,Priority.class);
			this.labelingStrategyName = this.priority.getName();
			
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch
	} // End  loadLocalStrategy
}
