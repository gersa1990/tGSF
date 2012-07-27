package mx.cicese.mcc.teikoku.scheduler.strategy;

import mx.cicese.mcc.teikoku.scheduler.SLA.events.LaxityInterruptionEvent;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;


public interface StrategyListener {

	void advise(Job jobToSchedule, Slot possibleSlot)
			throws DecisionVetoException;
	
	void registerLaxityEvent(LaxityInterruptionEvent laxityInterruptionEvent);

}
