package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Iterator;
import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;

public class EDFAccepter extends AbstractAccepter {

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
		
		}
		else
		{
			original.clear();
			original.addAll(queue);
			// there is new incoming job
			Iterator<Job> it = original.iterator();
			
			// All Jobs which their deadline is greater or equal than the incoming job
			// this jobs will be delayed in case the incoming job is accepted
			Queue<Job> myList2 = new SortedQueueBinarySearchImpl<Job>();
			
			double processingBeforeIncomingExec = 0;
			while (it.hasNext())
			{	
				itJob = it.next();
				
				if(itJob.getGuaranteedTime() >= incommingJob.getGuaranteedTime())
				{ 
					myList2.add(itJob);		
				}
				else
				processingBeforeIncomingExec += itJob.getRuntimeInformation().getRemainingRunTime().length();
			}
			
			
			
			double sumProc;
			result = true;
			double currentTime = Clock.instance().now().timestamp();
			sumProc = processingBeforeIncomingExec;
			while (!myList2.isEmpty())
			{
				itJob = myList2.peek();			

				
				sumProc += itJob.getRuntimeInformation().getRemainingRunTime().length();
					
				double deadline = DateHelper.convertToMilliseconds(itJob.getGuaranteedTime()) - currentTime;
				if (sumProc > deadline)
					return false;
				else
					myList2.remove();
			}//while
		
	}//else
		return result;
	}

}
