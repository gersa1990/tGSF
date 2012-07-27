package de.irf.it.rmg.core.teikoku.scheduler.queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.collections.AbstractInvertibleComparator;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * 
 * @author Anuar
 *
 *This class extends AbstractInvertibleComparator to consider Least Laxity First as 
 *the sorting method for scheduling jobs
 */
public class LLFComparator extends AbstractInvertibleComparator{

	public LLFComparator ()
	{
		super();
	}
	
	
	@Override
	public int compare(Job left, Job right) {
		// TODO Auto-generated method stub
		
		Distance leftJobLaxity = TimeFactory.newFinite(0);
		Distance rightJobLaxity = TimeFactory.newFinite(0);
		
		Instant currentTime = Clock.instance().now();

		Distance leftRemainingTime = left.getRuntimeInformation().getRemainingRunTime();
		Distance rightRemainingTime = right.getRuntimeInformation().getRemainingRunTime();

	
		Instant leftJobGuarantee = TimeFactory.newMoment(left.getGuaranteedTime()*1000);
		Instant rightJobGuarantee = TimeFactory.newMoment(right.getGuaranteedTime()*1000);
		
		
		leftJobLaxity = TimeHelper.distance(TimeHelper.subtract(leftJobGuarantee, leftRemainingTime),currentTime);
		rightJobLaxity = TimeHelper.distance(TimeHelper.subtract(rightJobGuarantee, rightRemainingTime), currentTime);
		

		
		return (leftJobLaxity.compareTo(rightJobLaxity));
		
	}

}
