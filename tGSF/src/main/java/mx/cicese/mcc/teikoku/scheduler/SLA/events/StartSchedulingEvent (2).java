package mx.cicese.mcc.teikoku.scheduler.SLA.events;

import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class StartSchedulingEvent extends Event {
	
	
	public StartSchedulingEvent(final Instant time, Site s )
	{
		super(time, s.getUUID().toString(),s);
		
	}
	@Override
	protected int getOrdinal() {
		// TODO Auto-generated method stub
		return EventType.START_SCHEDULING.ordinal();
	}
	

}
