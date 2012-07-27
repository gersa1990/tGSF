package mx.cicese.dcc.teikoku.metrics;

import mx.cicese.dcc.teikoku.utilities.CriticalPath;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;

public final class MetricHelper {

	/**
	 * A critical path estimation strategy
	 */
	private CriticalPath<SWFJob,Precedence> cpAlg;
	
	
	/**
	 * Avoids recomputing the critical path
	 */
	private boolean cpComputed = false;
	

	/**
	 * Composite job cmax
	 */
	private double w_cmax;

	
	
	/**
	 * Class constructor
	 */
	public MetricHelper() { 
		cpAlg = new CriticalPath<SWFJob,Precedence>();
		w_cmax = -1;
		cpComputed = false;
	} // End MetricHelper
	
	
		
	
	/**
	 * Gets the parallel job waiting time
	 * 
	 * @param job, a parallel job
	 * @return the parallel job waiting time
	 */
	public double p_wait_time(SWFJob job ) {
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double processingTime = DateHelper.convertToSeconds(job.getDuration().distance().length());

		return (endTime - processingTime - releaseTime);
	} // End waitingTime
	
	
	
	/**
	 * Gets the parallel job completion time
	 * 
	 * @param job, a parallel job
	 * @return the parallel job completion time
	 */
	public double p_getCmax(Job job) {
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		return endTime;
	} // getCmax
	
		
	
	/**
	 * Gets the composite job maximum completion time
	 * 
	 * @param job, a composite job
	 * @return the composite job completion time
	 */
	public double w_getCmax(Job job) {
		double maxCompletionTime = -1;
		
		if (w_cmax == -1) {
			for(SWFJob j:((CompositeJob) job).getStructure().getVertices()){
				double completionTime = DateHelper.convertToSeconds(j.getDuration().getCessation().timestamp());
				if (completionTime > maxCompletionTime)
					maxCompletionTime = completionTime;				
			} // End for
			w_cmax = maxCompletionTime;
		} else 
			maxCompletionTime = w_cmax;
		
		return maxCompletionTime;
	}

	
	/**
	 * Gets the composite job aggregate mean waiting time
	 * 
	 * @param job, a composite job
	 * @return the composite job mean waiting time
	 */
	public double w_getWaitingTime(Job job) {
		double cpProcTime = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double completionTime = this.w_getCmax(job);
		double waitingTime = completionTime - cpProcTime - releaseTime; 
		return waitingTime;
	}
	
	
	/**
	 * Gets the composite job speedup.  
	 * - It assumes their is no waiting times between jobs with precedence
	 *   constraints
	 * 
	 * @param job, a composite job
	 * @return
	 */
	public double w_getCritalPathSlowDown(Job job) {
		double cmax = this.w_getCmax(job);
		double cpCost = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double delay = cmax - releaseTime - cpCost;
		
		double cps = 1 + (delay / cmax);
		return cps; 
	} //End getSpeedUp
	
		
	
	/**
	 * Gets the composite job length ratio
	 * - It assumes their is no waiting times between jobs with precedence
	 *   constraints
	 *   
	 * @param job, a composite job
	 * @return the length ratio of the composite job
	 */
	public double w_getWLR(Job job) {
		double cmax = this.p_getCmax(job);
		double cpCost = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		double wlr = cmax /(cpCost + releaseTime);
		
		return wlr;
	} // End getSLR
	
	
	
	// Composite job HELPER methods
	
	/**
	 * Computes a composite job critical path cost, it includes the release time
	 * 
	 * @param job,
	 * @return
	 */
	public double w_getCriticalPathCost(Job job) {
		double cp = 0;
		
		if(!this.cpComputed) { 
			cpAlg.compute(((CompositeJob) job).getStructure());
			this.cpComputed = true;
		} // End if
		//CP cost in seconds
		cp = cpAlg.getCriticalPathLength();
		
		return cp;
	} // End getCriticalPathCost


	
	/**
	 * Computes the total area of the workflow
	 * 
	 * @param job,
	 * @return
	 */
	public double w_getTotalWork(Job job) {
		double totalWork = 0;
		
		for(SWFJob j :((CompositeJob) job).getStructure().getVertices()) {
			double work = j.getRunTime();
			totalWork += work;
		}
		
		return totalWork;
	} // End getCriticalPathCost
	
	
	/**
	 * Clears critical path estimation data structures
	 */
	public void clear() {
		this.cpAlg.clear();
		this.cpComputed = false;
	}// End clear
	
} // End MetricHelper
