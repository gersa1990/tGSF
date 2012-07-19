package mx.cicese.mcc.teikoku.scheduler.SLA.events;

import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;

public class LaxityInterruptionEvent extends Event {

	Job job;
	
	public LaxityInterruptionEvent(final Instant timestamp, final Site location, Job j)
	{
		super(timestamp, location.getUUID().toString(), location);
		this.job = j;
		
	}
	
	@Override
	protected int getOrdinal() {
		// TODO Auto-generated method stub
		return EventType.LAXITY_INTERRUPTION_EVENT.ordinal();
	}

	public Job getLaxityJob()
	{
		return this.job;
	}

}
