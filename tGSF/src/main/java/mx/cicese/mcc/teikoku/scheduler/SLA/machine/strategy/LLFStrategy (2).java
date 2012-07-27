package mx.cicese.mcc.teikoku.scheduler.SLA.machine.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.AbstractAccepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.Accepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.AllAccepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.JobRejectedEvent;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.LaxityInterruptionEvent;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.LaxityInterruptionEventRegister;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.AbstractStrategy;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.Pair;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class LLFStrategy extends AbstractStrategy {
	
	final private static Log log = LogFactory.getLog(LLFStrategy.class);
	
	private List<Pair<Job,Slot>> processedJobs;
		
	private AbstractAccepter accepter;
	
	private Job executingJob = null;
	
	/*
	 * Constructors Area
	 */
	
	public LLFStrategy () {
		super();
		this.processedJobs = new ArrayList<Pair<Job, Slot>>();
	}
	
	public LLFStrategy (LLFStrategy another) {
		super(another);
		this.processedJobs = new ArrayList<Pair<Job,Slot>>();
		for(Iterator<Pair<Job, Slot>> it = another.processedJobs.iterator(); it.hasNext();)
			this.processedJobs.add(it.next());
	}
	
	/*
	 * End of Constructor Area
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Strategy#decide(java.util.Queue, de.irf.it.rmg.core.teikoku.site.schedule.Schedule)
	 */
	
	@Override
	public Strategy clone() {
		return new LLFStrategy(this);
	}
	
	protected Instant determineRequestedStartTime (Job job) {
		return Clock.instance().now();
	}

	/*
	 * This part decide if the queue with jobs will be accepted or not
	 */
	@Override
	public void decide(Queue<Job> queue, Schedule schedule) {
		
		this.processedJobs.clear();
		
		if(!queue.isEmpty()) {
			
			Job itJob = null; // the job used with the iterator
			Job incomingJob = null; // in case that this method is call by the release of a new job, 
									//incoming job will hold this new job
			
			Iterator<Job> it = queue.iterator(); // the iterator of the queue of jobs
			while(it.hasNext()) //move through the queue to find the just released job if it exists
			{
				itJob = it.next();
				if(itJob.getLifecycle().getLastCondition() == State.QUEUED)
				{
					incomingJob = itJob;
				}
				
			}//while
			
			if(incomingJob != null)
			{
				// if there is a new release we need to call the accepter over this job
				/*
				if(!accepter.examine(queue, incomingJob))
				{
					queue.remove(incomingJob);
					Instant timeEvent = Clock.instance().now();
					JobRejectedEvent event = new JobRejectedEvent(timeEvent,incomingJob,this.getSite());
					Kernel.getInstance().dispatch(event);
				}*/
				
			}//if job rejection
		}//if verify queue emptiness 
		
		
		/*
		 * Scheduling block one after another according with the queue order
		 */
		
		
		Instant now = null;
		
//		while (queueIterator.hasNext()) {
			Slot possibleSlot = null;
			
			Job jobToSchedule = queue.remove();
			//this part is only executed for the first job
			if (now == null) 
			{
				now = this.determineRequestedStartTime(jobToSchedule);
				//This is used to obtain the number of interruptions if the first job to be executed is the one which was previously executed 
				//we do not count it as an interruption
				if(jobToSchedule.equals(executingJob))
				{
					jobToSchedule.getRuntimeInformation().setIterruptionFlag(false);
				}
				executingJob = jobToSchedule;
				
			}//if
			
			
			//we allocated the jobs one after the other using their remaining run time.
			possibleSlot = new Slot (now, jobToSchedule.getRuntimeInformation().getRemainingRunTime());
			possibleSlot.setResources(new ResourceBundleBitVectorImpl(this.getSite().getSiteInformation(),
					this.getSite().getSiteInformation().getProvidedResources().toArray()));
			
			now = possibleSlot.getCessation();
			this.processedJobs.add(new Pair<Job, Slot>(jobToSchedule, possibleSlot));
//		}//while
		
		//This calculates the difference between the job with the less laxity and the second job with less
		//laxity to create the LaxityInterruptionEvent
		if(queue.size() >= 2)
		{
			long firstJobLaxity = jobToSchedule.getRuntimeInformation().getLaxity();
			long secondJobLaxity = queue.peek().getRuntimeInformation().getLaxity();
			
			
			Instant currentTime = Clock.instance().now();
			/*
			System.out.println(Clock.instance().now().toString() + " First Job " + processedJobs.get(0).getLeft().getName() +
					" " + firstJobLaxity + " " + processedJobs.get(0).getLeft().getRuntimeInformation().getRemainingRunTime().toString() 
					+" Second Job " + processedJobs.get(1).getLeft().getName() +
					" " +secondJobLaxity + " " + processedJobs.get(1).getLeft().getRuntimeInformation().getRemainingRunTime().toString());
			
			*/
			Distance laxityDiferenceDistance = TimeFactory.newFinite(secondJobLaxity - firstJobLaxity);
			Instant laxityInterruptionTime = null;
			if(laxityDiferenceDistance.length() == 0)
			{
				laxityInterruptionTime = TimeHelper.add(currentTime, TimeFactory.newFinite(1000));
			}
			else
			{
				laxityInterruptionTime = TimeHelper.add(currentTime, laxityDiferenceDistance);
			}
			Event laxityInterruption = new LaxityInterruptionEvent(laxityInterruptionTime,this.getSite(),jobToSchedule);
			Event laxityInterruptionRegister = 
					new LaxityInterruptionEventRegister((LaxityInterruptionEvent)laxityInterruption,
							this.getSite());
			Kernel.getInstance().dispatch(laxityInterruptionRegister);
			Kernel.getInstance().dispatch(laxityInterruption);
			
		}
		
		//send collected decisions for this iteration to strategy listener
		for (Pair<Job,Slot> p : this.processedJobs) {
			try {
				super.getStrategyListener().advise(p.getLeft(),
						p.getRight());
			}
			catch (DecisionVetoException e) {
				String msg = "decision vetoed: properties in slot \"" 
						+ p.getRight() + "\" not accepted for job \""
						+ p.getLeft();
				log.info(msg, e);
			}
		}
		
		
		
	}

	@Override
	public void setSLAAccepter(Accepter accepter) {
		if (accepter != null)
			this.accepter = (AbstractAccepter) accepter;
		else
			this.accepter = new AllAccepter();
		
	}

	@Override
	public JobControlBlock getNextDecision(Queue<Job> queue, Schedule schedule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobControlBlock getNextDecision(Queue<Job> queue,
			TemporalSchedule schedule) {
		// TODO Auto-generated method stub
		return null;
	}

}
