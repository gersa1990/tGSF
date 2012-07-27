package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;

public interface Accepter {

	
	 abstract public boolean examine(Queue<Job> queue, Job newJob);
}
