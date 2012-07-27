package mx.cicese.mcc.teikoku.scheduler.SLA;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;

public class StrategyHelper {

final private static Log log = LogFactory.getLog(StrategyHelper.class);


public static Slot determineSoonestSlot(Schedule schedule, Job job,
		Instant startTime, boolean allowTardiness) {
	Slot result = null;

	/*
	 * determine the allowed search area for the given job
	 */
	Period searchArea = null;
	if (allowTardiness) {
		/*
		 * tardiness : search within now and forever
		 */
		Instant endTime = TimeFactory.newEternity();
		searchArea = new Period(startTime, endTime);
	} // if
	else {
		/*
		 * no tardiness: search within now and runtime estimation
		 */
		
		//Instant guarTimeI = new Instant(job.getGuaranteedTime()*1000);
		Instant guarTimeI = TimeFactory.newEternity();
		searchArea = new Period(startTime,guarTimeI);
		//searchArea = new Period(startTime, TimeHelper
			//	.add(startTime, job.getDescription().getEstimatedRuntime()));
	
	} // else

	try {
		/*
		 * try to find a fitting slot for the given job
		 */
		Distance mydist = new Distance(job.getRuntimeInformation().getRemainingRunTime());
		result = schedule.findNextFreeSlot(mydist, job.getDescription()
				.getNumberOfRequestedResources(), searchArea);
	} // try
	catch (InvalidTimestampException e) {
		String msg = "scheduling failed: " + e.getMessage();
		log.error(msg, e);
	} // catch
	catch (IllegalOccupationException e) {
		String msg = "scheduling failed: " + e.getMessage();
		log.error(msg, e);
	} // catch

	return result;
}



/**
 * Finds a slot for starting the given job, if possible.
 * 
 * In detail, this method tries to find a slot in the schedule allowing the
 * job provided in the parameter to be started now or as soon as possible.
 * 
 * The requested start time for the given job is determined by calling
 * {@link #determineRequestedStartTime(OldJob)}, which has to be
 * overwritten by subclasses for different than default behaviour.
 * 
 * In the former case, the behaviour of this method directly corresponds to
 * the <em>First-Come-First-Serve</em> (FCFS) scheduling algorithm: if the
 * search is successful, a possible slot is returned with the determined
 * start time and (estimated) end time and the set of resources that could
 * be occupied by the job. Otherwise, nothing is returned.
 * 
 * In the latter case, the behaviour of this method is characterized by an
 * <em>As-Soon-As-Possible</em> (ASAP) policy: searching will
 * <strong>always</strong> yield a result; however, such a slot is not
 * anymore guaranteed to start now, but may be delayed until the end of the
 * schedule.
 * 
 * Which of the two cases is applied depends on the setting of the
 * "allowTardiness" parameter.
 * 
 * @param schedule
 *            The schedule to which the given job is to be added.
 * @param job
 *            The job to be scheduled immediately.
 * @param allowTardiness
 *            Denotes which behaviour is to be used for the next search.
 *            <code>false</code> entails in the first (FCFS) case,
 *            <code>true</code> in the second (ASAP).
 * @return A slot with start time, end time and occupied resources
 *         parameters matching the job's requirements, iif the job could be
 *         scheduled; <code>null</code>, otherwise.
 * 
 * @see Schedule#findNextFreeSlot(long, int,
 *      de.irf.it.rmg.core.teikoku.common.Period)
 * @see #determineRequestedStartTime(OldJob)
 * @see Job#durationFrom(Instant)
 * @see Job#getNumberOfRequestedResources()
 * @see Slot
 */
public static Slot determineSoonestSlot(TemporalSchedule schedule, Job job,
		Instant startTime, boolean allowTardiness) {
	Slot result = null;

	/*
	 * determine the allowed search area for the given job
	 */
	Period searchArea = null;
	if (allowTardiness) {
		/*
		 * tardiness : search within now and forever
		 */
		Instant endTime = TimeFactory.newEternity();
		searchArea = new Period(startTime, endTime);
	} // if
	else {
		/*
		 * no tardiness: search within now and runtime estimation
		 */
		searchArea = new Period(startTime, TimeHelper
				.add(startTime, job.getDescription().getEstimatedRuntime()));
	} // else

	try {
		/*
		 * try to find a fitting slot for the given job
		 */
		result = schedule.findNextFreeSlot(job.getDescription().getEstimatedRuntime(), job.getDescription()
				.getNumberOfRequestedResources(), searchArea);
	} // try
	catch (InvalidTimestampException e) {
		String msg = "scheduling failed: " + e.getMessage();
		log.error(msg, e);
	} // catch
	catch (IllegalOccupationException e) {
		String msg = "scheduling failed: " + e.getMessage();
		log.error(msg, e);
	} // catch

	return result;
}

	
}
