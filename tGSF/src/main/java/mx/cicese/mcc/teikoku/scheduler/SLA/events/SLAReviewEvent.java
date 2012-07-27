package mx.cicese.mcc.teikoku.scheduler.SLA.events;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class SLAReviewEvent extends Event {
	
	Site site;
	Job job;
	
	public SLAReviewEvent(final Instant time, Job j, Site s )
	{
		super(time,s.getUUID().toString(),s);
		this.site=s;
		this.job = j;
	}
	
	@Override
	protected int getOrdinal() {
		// TODO Auto-generated method stub
		return EventType.SLA_REVIEW.ordinal();
	}
	
	public Site getSite()
	{
		return this.site;
	}
	
	public Job getSuspendedJob()
	{
		return this.job;
	}

}
