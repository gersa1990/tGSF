package mx.uabc.lcc.teikoku.error;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSink;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSource;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

/*
 * @author Aritz, Adan Hirales Carbajal
 */
public class ErrorManager {

	/**
	 * The site containing the error manager
	 */
	private Site site;
	
	
	/**
	 * The resources that have failed. Resources that have failed are
	 * buffered. When an availability event occurs, they are restored.
	 */
	private ResourceBundle failedResources;
	
	
	/**
	 * ???
	 */
	private ResourceBundle resourcesAtFailedSite;
	
	
	/**
	 * The time between error events in milliseconds
	 */
	private long timeBetweenErrors;
	
	
	/**
	 * ???
	 */
	private long  nextErrorTime;
	
	
	/**
	 * The time between recovery events
	 */
	private long timeToRecover;
	
	
	/**
	 * Class constructor
	 */
	public ErrorManager() {
		failedResources = new ResourceBundleSortedSetImpl();
		resourcesAtFailedSite = new ResourceBundleSortedSetImpl();
		nextErrorTime = 0;
	}
	
	
	/**
	 * Sets the site that contains the error manager
	 * 
	 * @param site, a Site
	 */
	public void setSite(Site site){
		this.site=site;
	}
	
	
	/**
	 * Gets the site the event error manager is associated to
	 * 
	 * @return a Site 
	 */
	public Site getSite()
	{
		return site;
	}
	
	
	/**
	 * Sets the time for recovery events
	 * 
	 * @param newTime
	 */
	public void setTimeToRecover(long newTime){
		timeToRecover = newTime;
	}
	
	
	/**
	 * Sets the time between errors
	 * 
	 * @param newTime
	 * @param wl
	 */
	public void setTimeBetweenErrors(long newTime){
		timeBetweenErrors = newTime;
		nextErrorTime = nextErrorTime + timeBetweenErrors;
	}
	
			
	/**
	 * Returns the time between errors
	 * 
	 * @return
	 */
	public long getTimeBetweenErrors() {
		return this.timeBetweenErrors;
		
	}
	
	
	/** 
	 * Creates a resource unavailability event and registers it in the Kernel
	 * 
	 */
	public void createNextResourceFailureEvent(){
		Error e = null;
		Error r = null;
		
		e = new Error(0,nextErrorTime,this.site.getName());
		
		// Create recovery event
		if(timeToRecover > 0) 
			r = new Error(1,nextErrorTime+timeToRecover,this.site.getName());
		
		nextErrorTime = nextErrorTime + timeBetweenErrors;
		
		Kernel.getInstance().dispatch(e.getEvent());
		
		if( r != null ) 
			Kernel.getInstance().dispatch(r.getEvent());
	}
	
	
	/**
	 * Creates a site failure event
	 */
	public void createNextSiteFailureEvent(){
		Error e=null, r=null;
		e = new Error(2,nextErrorTime,this.site.getName());
		if(timeToRecover>0)
		{
			r = new Error(3,nextErrorTime+timeToRecover,this.site.getName());
		}
		nextErrorTime=nextErrorTime+timeBetweenErrors;
		Kernel.getInstance().dispatch(e.getEvent());
		if(r!=null)
		{
			Kernel.getInstance().dispatch(r.getEvent());
		}
	}

	
	public void generateEventsFromErrorLoad()
	{
		//Creates an ErrorBundle from ErrorParser
		ErrorParser ep = new ErrorParser();
		for(Error e:ep.getErrorSet())
		{
			Kernel.getInstance().dispatch(e.getEvent());
		}
	}

	
	
	public void generateEventsFromErrorLoad(String path)
	{
		//Creates an ErrorBundle from ErrorParser
		ErrorParser ep = new ErrorParser(path);
		for(Error e:ep.getErrorSet())
		{
			Kernel.getInstance().dispatch(e.getEvent());
		}
	}
	
	
	public void deliverResourceUnavailabilityEvent(Event e)
	{
		Event eventToRemove = null;
		ResourceBundle unAvailableResources = null;
		ResourceBundle resourcesToRemove = new ResourceBundleSortedSetImpl();
		Job j= null;

		
		/**
		 * If there are jobs that are being executed, we chose one job arbitrarily. 
		 * All information associated to that job must be erased, such as:
		 * - Any event registered in the Kernel (StartedEvent)
		 * - The job pointer in the scheduled job queue in the schedule class
		 * - Utilization events in the schedule
		 * - Only one resource (processor) that was associated to the job is removed,  but information
		 * of it is stored in a buffer. The objective of buffering it, is to emulate recovery of the
		 * resource once a recovery event occurs in a future event.
		 * 
		 */ 
		Set<Job> scheduledJobs = site.getScheduler().getSchedule().getScheduledJobs();
		
		double pbbDroppingJob = Math.random();
		if(scheduledJobs.size() != 0) {
			
			/* Abort a scheduled job */
			if(pbbDroppingJob >= 0) {
				j = scheduledJobs.toArray(new SWFJob[0])[0];
				System.out.println("Job " + j.getName() + " just kick the bucket!");
				
				/**
				 * Determine what resources that will be made unavailable 
				 */
				unAvailableResources = j.getResources();
				
				try {	
					/** 
					 * Removes the job from the schedule, but resources are not made unavailable 
					 * to other jobs 
					 */
					site.getScheduler().getSchedule().removeJob(j);
					
					/**
					 * Removes any started or completed event associated to the job
					 */
					SortedQueue<Event> eq = Kernel.getInstance().getEventQueue();
					for(Event ev : eq){
						if(ev instanceof JobStartedEvent)
							if(((JobStartedEvent) ev).getStartedJob().getUUID().equals(j.getUUID()))
								eventToRemove = ev;
									
						if ( ev instanceof JobCompletedEvent)
							if(((JobCompletedEvent) ev).getCompletedJob().getUUID().equals(j.getUUID()))
								eventToRemove = ev;
					}//End for
					
					
					/** 
					 * Remove the event from the event queue in the simulator
					 */
					if(eventToRemove!=null) 
						Kernel.getInstance().getEventQueue().remove(eventToRemove);
					
					/**
					 * Get resources that were used by the job and make them unavailable
					 * to other jobs
					 */
					// Decrease the amount of available resources 
					if( unAvailableResources != null ) {
						System.out.println("Size " + unAvailableResources.size());
						System.out.println("Empty " + unAvailableResources.isEmpty());
						
						for(Iterator<Resource> it = unAvailableResources.iterator(); it.hasNext();) {
							System.out.println("Job " + j.getName() + " has :" + it.next().getName());
						}
						if( unAvailableResources.get(0) != null ) {
							resourcesToRemove.add( unAvailableResources.get(0) ); // Only a single resource is removed
							site.getSiteInformation().getProvidedResources().remove(resourcesToRemove);
							failedResources.add(resourcesToRemove);
						}
						// All resources that the job held are removed.
						/*
						site.getSiteInformation().getProvidedResources().remove(jobFailedResources);
						failedResources.add(jobFailedResources);
						*/
					} //End if unavailable resources
				} catch(IllegalOccupationException ex) {
					System.out.println("IllegalOccupationException occur in ErrorManager");
				}
			} // End probability of choosing a job  
		} //End if there are some jobs that may be aborted
		else {
			// Get the first resource and make it unavailable 
			ResourceBundle pr = site.getSiteInformation().getProvidedResources();
			if( pr.size() != 0 ) {
			/*	failedResources.add(site.getSiteInformation().getProvidedResources().get(0));
				site.getSiteInformation().getProvidedResources().remove(0); */ 
			} 
		} //End else
		
		printStatus();
	}
	
	
	
	
	public void deliverResourceAvailabilityEvent(Event e) throws Exception
	{	
		//Aumentar recursos
		if(failedResources.size()>0)
		{
			List<Resource> ls = site.getSiteInformation().getProvidedResources();
			Resource rs = new Resource(failedResources.get(0).getName());
			ls.add(rs);
		}
		else
		{
			throw new NoAvailableResources();
		}
		printStatus();
	}
	
	
	
	
	public void deliverSiteUnavailabilityEvent(SiteUnavailabilityEvent e) {
		// TODO Auto-generated method stub
		Event toRemove;
		ResourceBundle jobFailedResources=new ResourceBundleSortedSetImpl();
		Job j;

		
		//Si tiene tareas, truena tareas
		Set<Job> js = site.getScheduler().getSchedule().getScheduledJobs();
		while(js.size()>0)
		{
			toRemove = null;
			j = js.toArray(new SWFJob[0])[0];
			jobFailedResources.add(j.getResources());
			//
			try
			{
				site.getScheduler().getSchedule().removeJob(j);
				SortedQueue<Event> eq = Kernel.getInstance().getEventQueue();
				for(Event ev :eq){
					if(ev instanceof JobStartedEvent)
					{
						if(((JobStartedEvent) ev).getStartedJob().getUUID().equals(j.getUUID()))
								{
									toRemove=ev;
								}
					}
					if ( ev instanceof JobCompletedEvent)
					{
						if(((JobCompletedEvent) ev).getCompletedJob().getUUID().equals(j.getUUID()))
							{
								toRemove=ev;
							}
					}
				}
				if(toRemove!=null)
				{
					Kernel.getInstance().getEventQueue().remove(toRemove);
				}
 //				site.getScheduler().getSchedule().;
			}
			catch(IllegalOccupationException ex)
			//catch(NoSuchElementException ex)
			{
				System.out.println("Fallo");
			}
		}
		//Decrementar recursos
		if(jobFailedResources!=null)
		{
			site.getSiteInformation().getProvidedResources().remove(jobFailedResources);
			resourcesAtFailedSite.add(jobFailedResources);
		}
		ResourceBundle pr=site.getSiteInformation().getProvidedResources();
		while(pr.size()>0)
		{
			Resource rs = pr.get(0);
			resourcesAtFailedSite.add(rs);
			site.getSiteInformation().getProvidedResources().remove(rs);		
		}
		printStatus();
	}
	
	
	
	
	public void deliverSiteAvailabilityEvent(SiteAvailabilityEvent e) {
		// TODO Auto-generated method stub
		site.getSiteInformation().getProvidedResources().add(resourcesAtFailedSite);
		printStatus();
	}
	
	
	
	private void printStatus(){
		Instant n = Clock.instance().now();
		System.out.println("At time" + n.toString() +" " + site.getName()+ " has "+ site.getSiteInformation().getNumberOfAvailableResources());
	}
}
