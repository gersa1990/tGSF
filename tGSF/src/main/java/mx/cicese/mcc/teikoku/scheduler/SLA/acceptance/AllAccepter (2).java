package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;

public class AllAccepter extends AbstractAccepter {

	@Override
	public boolean examine(Queue<Job> queue, Job newJob) {
		// Accept All the incoming jobs
		return true;
	}

}
