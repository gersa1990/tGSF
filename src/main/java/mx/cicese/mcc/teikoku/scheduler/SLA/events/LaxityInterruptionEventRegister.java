package mx.cicese.mcc.teikoku.scheduler.SLA.events;

import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;

public class LaxityInterruptionEventRegister extends Event {
	
	private LaxityInterruptionEvent laxityInterruptionEvent;

	public LaxityInterruptionEventRegister (LaxityInterruptionEvent event, Site site) {
		super(Clock.instance().now(), site.getUUID().toString(), site);
		this.laxityInterruptionEvent = event;
	}
	
	@Override
	protected int getOrdinal() {
		// TODO Auto-generated method stub
		return EventType.LAXITY_EVENT_REGISTER.ordinal();
	}
	
	public LaxityInterruptionEvent getLaxityInterruptionEvent ()
	{
		return this.laxityInterruptionEvent;
	}
}
