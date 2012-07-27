package mx.cicese.mcc.teikoku.scheduler.SLA.machine.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.AbstractAccepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.Accepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.AllAccepter;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.JobRejectedEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.AbstractStrategy;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.LISTStrategy;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.Pair;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;



public class EDFStrategy extends AbstractStrategy {
	
	
	final private static Log log = LogFactory.getLog(LISTStrategy.class);
	
	private List<Pair<Job, Slot>> processedJobs;
	/**
	 * executingJob is the job that are being executed that means the first to be schedule, when an interruption
	 * happens it is compared with the new job to be firstly executed, if they are the same that means that the
	 * job was not interrupted otherwise the job was interrupted and is counted.
	 */
	private Job executingJob = null;
	

	private AbstractAccepter accepter;
	
	public EDFStrategy () {
		super();
		this.processedJobs = new ArrayList<Pair<Job, Slot>>();
		
	}
	
	public EDFStrategy(EDFStrategy another) { 
		super(another);
		this.processedJobs = new ArrayList<Pair<Job, Slot>>();
		for(Iterator<Pair<Job, Slot>> it = another.processedJobs.iterator(); it.hasNext();) 
			this.processedJobs.add(it.next());
	} 
	
	@Override
	public Strategy clone() {
		return new EDFStrategy(this);
	} 
	
    protected Instant determineRequestedStartTime(Job job) {
        return Clock.instance().now();
    }
    
    
    public void decide(Queue<Job> queue, Schedule schedule) {
    	
    	this.processedJobs.clear();
    	
    	
    	
        
        if (!queue.isEmpty()) {
        
        	Iterator<Job> tstIt = queue.iterator();
        	
            /*
             * repeat decision process for all jobs in the queue
            */
            boolean finished;
            Instant now = null;  
            
                	finished = false;
                	Job jobToSchedule = null;
                	Slot possibleSlot = null;
                		while (!finished) {
                			jobToSchedule = queue.remove(); // the job has been schedule we remove it from the incoming queue
                			
                			
                			if (now == null)
                			{
                   				now = this.determineRequestedStartTime(jobToSchedule);
                   				
                   				//This part is used to obtain the number of interruptions 
                   				if(jobToSchedule.equals(executingJob))
                   				{
                   					jobToSchedule.getRuntimeInformation().setIterruptionFlag(false);
                   					
                   				}
                   				executingJob = jobToSchedule;
                   				//end of counting interruptions
                				
                			}
     			
                			/*
                			 * try to schedule the given job
                			 */
                			possibleSlot = new Slot(now, jobToSchedule.getRuntimeInformation().getRemainingRunTime());
                		
                			possibleSlot.setResources(new ResourceBundleBitVectorImpl(this.getSite().getSiteInformation(),
                					this.getSite().getSiteInformation().getProvidedResources().toArray()));
                		

                			if (possibleSlot != null) {
                				/*
                				 * break the loop: a fitting job has been found
                				*/
                				finished = true;
                				now = possibleSlot.getCessation();
                			} // if
                			
    
                		} //while
                    
                    
                		this.processedJobs.add(new Pair<Job, Slot>(jobToSchedule,
                            possibleSlot));
                
       

            /*
                * send collected decisions for this iteration to strategy listener
                */
            for (Pair<Job, Slot> p : this.processedJobs) {
                try {
                    super.getStrategyListener().advise(p.getLeft(),
                            p.getRight());
                } // try
                catch (DecisionVetoException e) {
                    String msg = "decision vetoed: properties in slot \""
                            + p.getRight() + "\" not accepted for job \""
                            + p.getLeft()
                            + "\"; skipping current and trying backfill";
                    log.info(msg, e);
                } // catch
            } // for
            
       
            
        } // if
    }
    
   

	@Override
    public JobControlBlock getNextDecision(final Queue<Job> queue, final Schedule schedule){
		/* TODO: implement */

		return null;
	}
    @Override
    public JobControlBlock getNextDecision(final Queue<Job> queue, final TemporalSchedule schedule){
		/* TODO: implement */
		return null;
	}

	@Override
	public void setSLAAccepter(Accepter accepter) {
		if (accepter != null)
		this.accepter = (AbstractAccepter) accepter;
		else
			this.accepter = new AllAccepter();
	}

	
	
		
}

