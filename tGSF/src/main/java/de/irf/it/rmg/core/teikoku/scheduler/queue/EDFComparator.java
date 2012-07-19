package de.irf.it.rmg.core.teikoku.scheduler.queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.collections.AbstractInvertibleComparator;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * 
 * @author QAnuar
 * This class extends AbstractInvertibleComparator to consider Earliest Deadline First
 * as the sorting method for the scheduling jobs.
 */

public class EDFComparator extends AbstractInvertibleComparator {
	
	public EDFComparator() {
		super();
	}

	@Override
	public int compare(Job left, Job right) {
		
		int result = 0;
		
		if(left.getGuaranteedTime() > right.getGuaranteedTime())
			result = 1;
		else if(left.getGuaranteedTime() < right.getGuaranteedTime())
			result = -1;
		else
			result =0;

		
		return result;
	}

}
