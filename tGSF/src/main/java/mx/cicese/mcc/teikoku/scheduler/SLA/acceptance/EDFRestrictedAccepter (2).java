package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Iterator;
import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.sim.kuiga.Clock;

public class EDFRestrictedAccepter extends AbstractAccepter {

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
			
			
			int incomingSLAType = incommingJob.getSla().getType();
			// All Jobs which their deadline is greater or equal than the incoming job
			// this jobs will be delayed in case the incoming job is accepted
			Queue<Job> myList2 = new SortedQueueBinarySearchImpl<Job>();
			
			double processingBeforeIncomingExec = 0;
			while (it.hasNext())
			{	
				itJob = it.next();
				
				
				if(itJob.getSla().getType() == incomingSLAType)
				{
					
					if(itJob.getGuaranteedTime() >= incommingJob.getGuaranteedTime())
					{ 
						myList2.add(itJob);		
					}
					else
						processingBeforeIncomingExec += itJob.getRuntimeInformation().getRemainingRunTime().length();
				}
				else
				{
					if(itJob.getRestrictedTime() >= incommingJob.getRestrictedTime())
					{
						myList2.add(itJob);
					}
					else
						processingBeforeIncomingExec += itJob.getRuntimeInformation().getRemainingRunTime().length();
				}
				
			}
			
			
			
			double sumProc;
			result = true;
			double currentTime = Clock.instance().now().timestamp();
			sumProc = processingBeforeIncomingExec;
			
			// From the whole jobs that which deadline is delayed we need to perform the acceptability test
			while (!myList2.isEmpty())
			{
				itJob = myList2.peek();			
				
				sumProc += itJob.getRuntimeInformation().getRemainingRunTime().length();
				double deadline; 
				if(itJob.getSla().getType() == incomingSLAType)
				{
				
					deadline = DateHelper.convertToMilliseconds(itJob.getGuaranteedTime()) - currentTime;
					if (sumProc > deadline)
						return false;
					else
					myList2.remove();
				}
				else
				{
					deadline = DateHelper.convertToMilliseconds(itJob.getRestrictedTime()) - currentTime;
					if (sumProc > deadline)
						return false;
					else
					myList2.remove();
				}
				
			}//while
		
	}//else
		return result;
	}

}
