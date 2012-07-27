package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;

public interface RStrategy {	
	/**
	 * 	Schedules a rigid job to a site based on some strategy.
	 * 	Strategies are overloaded by subclases. Implemented rigid job 
	 * 	scheduling strategies are the following:
	 * 	<ol>
	 * 	 	<li> Random 			@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Random
	 * 		<li> Min load per site	@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MinProcessorLoad
	 * 		<li> Min parallel load	@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MinParallelLoad
	 * 		<li> Min load balancing	@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MinLBal
	 * 		<li>
	 * 		<li>
	 * 		<li>
	 * 		<li> Min Waiting time 	@see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.MinWaitinTime
	 * 		<li>
	 * 		<li>
	 * 		<li>
	 * 	</ol>
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job);
	
	
	/**
	 * Setter method, sets the component site
	 * 
	 * @param site	the site this component is associated to
	 */
	public void setSite(Site site);
	
} // End Strategy
