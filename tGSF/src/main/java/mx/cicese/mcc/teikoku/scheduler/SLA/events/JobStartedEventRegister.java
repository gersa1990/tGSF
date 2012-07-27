 package mx.cicese.mcc.teikoku.scheduler.SLA.events;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class JobStartedEventRegister extends Event {
	
	Site site;
	Job job;
	JobStartedEvent jobStartedEvent;

	public JobStartedEventRegister(Instant timeRegister, Job job, Site site, JobStartedEvent event) {
		// TODO Auto-generated constructor stub
		super(timeRegister,site.getUUID().toString(),site);
		this.site = site;
		this.job = job;
		this.jobStartedEvent = event;
	}

	@Override
	protected int getOrdinal() {
		// TODO Auto-generated method stub
		return EventType.STARTED_JOB_EVENT_REGISTER.ordinal();
	}
	
	public Site getSite()
	{
		return site;
	}
	
	public Job getJob()
	{
		return job;
	}
	
	public JobStartedEvent getJobStartedEvent ()
	{
		return jobStartedEvent;
	}

}
