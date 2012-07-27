package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Iterator;
import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;

public class LLFAccepter extends AbstractAccepter {

	@Override
	public boolean examine(Queue<Job> queue, Job newJob) {
		Queue<Job> original = new SortedQueueBinarySearchImpl<Job>();

    	original.addAll(queue);
    	
		Job itJob = null;
		Job incommingJob = newJob;
		boolean result;
		
		
		original.remove(incommingJob);// the incoming job is deleted
		if (incommingJob == null || original.isEmpty())
		{
			// there isn't a new job release therefore acceptance doesn't need to be verified
			result = true; // the job is accepted
			//System.out.println("Inside SLA Review Case 1, there is only one job");
		} //if
		else // there is a new job so the acceptance test should be performed
		{
			//The list is sorted by non increasing order with the job's laxities
			original.clear();
			original.addAll(queue);
			Iterator<Job> it = original.iterator();
			long[] relativeDeadlines = new long[original.size()];
			long acumulativeRunTime = 0;
			long greaterInterval = 0;
			int i=0;
			while(it.hasNext())
			{
				itJob = it.next();
				long remainingRunningTime = itJob.getRuntimeInformation().getRemainingRunTime().length();
				long jobsLaxity = itJob.getRuntimeInformation().getLaxity();
				relativeDeadlines[i]=remainingRunningTime +
						jobsLaxity;
				i++;
				
			}
			it = original.iterator();
			i=0;
			result = true; // the job is accepted
			while(it.hasNext())
			{
				itJob = it.next();
				
				if(relativeDeadlines[i]>greaterInterval)
					greaterInterval=relativeDeadlines[i];
				acumulativeRunTime += itJob.getRuntimeInformation().getRemainingRunTime().length();
				if(acumulativeRunTime > greaterInterval)
					return false;
				i++;
			}
			
		}
			
		
	return result;
	} //examine
	
} //class
