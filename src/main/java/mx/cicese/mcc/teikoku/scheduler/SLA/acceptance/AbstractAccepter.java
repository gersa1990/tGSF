/**
 * 
 */
package mx.cicese.mcc.teikoku.scheduler.SLA.acceptance;

import java.util.Queue;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.util.ConfigurationHelper;

/**
 * @author Anuar
 *
 */
public abstract class AbstractAccepter implements Accepter {
	/**
	 * This method takes the queue of the already accepted jobs queue
	 * and determines if the incoming job (newJob) is accepted or not
	 * according with the state of the accepted jobs and the deadline 
	 * requires of the incoming job.
	 * @param queue
	 * @param newJob
	 * @return Return if the job is accepted true if it is not false
	 */
	 abstract public boolean examine(Queue<Job> queue, Job newJob);

}
