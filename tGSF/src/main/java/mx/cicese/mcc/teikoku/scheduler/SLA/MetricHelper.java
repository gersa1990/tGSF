package mx.cicese.mcc.teikoku.scheduler.SLA;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;


public final class MetricHelper {

	
	public MetricHelper(SWFJob job) {
		job.getRuntimeInformation().addRunningTime();
	}
	
	/**
	 *Gets the jobs' waiting time as (c_j - p_j - r_j)
	 *
	 *@param a job
	 *@return the job's waiting time
	 */
	
	public double wait_time (SWFJob job) {
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		//ads the time since the last resume until now (we assuming we are under a complete event)
		
		double processingTime = job.getRuntimeInformation().getRunningTime();
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		
		return (endTime - processingTime - releaseTime);
	}
	
	public double turnaround_time (SWFJob job) {
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		
		return (endTime - releaseTime);
	}
	
	public double slow_down (SWFJob job) {
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double procesingTime = job.getRuntimeInformation().getRunningTime();
		
		return (endTime- releaseTime)/Math.max(procesingTime, 10);
	}
}
