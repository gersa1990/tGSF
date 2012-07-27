package mx.cicese.mcc.teikoku.scheduler.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import mx.cicese.mcc.teikoku.kernel.events.JobCanceledEvent;
import mx.cicese.mcc.teikoku.kernel.events.JobReplicaReleasedEvent;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class ReplicationControl {
	
	private List <List<SWFJob>> replicatedJobs;
	private int replicationThreshold;
	private Site site;
	
	public ReplicationControl(Site s)
	{
		site = s;
		replicatedJobs=new ArrayList();
		this.loadReplicationThreshold();
	}
	
	private void loadReplicationThreshold() {
		// TODO Auto-generated method stub
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Site.CONFIGURATION_SECTION + "." + this.site.getName());
		try
		{
			this.replicationThreshold = c.getInt(Constants.CONFIGURATION_SITES_SCHEDULER_REPLICATION_THRESHOLD);
		}
		catch (Exception e)
		{
			this.replicationThreshold = 1;
		}
	}

	public void replicate (Job j)
	{
		SWFJob newJob = makeReplica((SWFJob)j);
		TimeFactory tf=new TimeFactory();
		long time = Clock.instance().now().timestamp();
		Instant eventTime=tf.newMoment(time);
		Kernel.getInstance().dispatch(new JobReplicaReleasedEvent (j.getReleasedSite().getActivityBroker(),
										eventTime, newJob, j.getReleasedSite(), this.replicaSearch((SWFJob) j).size()));
	/*
		Kernel.getInstance().dispatch(new JobReleasedEvent (j.getReleasedSite().getActivityBroker(),
				eventTime, newJob, j.getReleasedSite()));*/
	}
	
	public boolean isReplicable(Job j)
	{
		/*
		 * Decide if job j can be replicated
		 */
		if(replicaSearch((SWFJob)j).size()<this.replicationThreshold)
			return true;
		else
			return false;
	}

	public void cancelReplicas(Job j)
	{
		/*
		 * cancels all replicas from job j
		 */
		List <SWFJob> replicasToCancel=replicaSearch((SWFJob)j);
		for(SWFJob job:replicasToCancel)
		{
			if(j!=job)
			{
				if(!j.getReleasedSite().getReleasedSiteQueue().getQueue().contains(job))
				{
					if (job.getProvenance().getLastCondition()==null)
					{
						Kernel.getInstance().unregisterJobEvent(job);						
					}
					else
					{
						TimeFactory tf=new TimeFactory();
						long time = Clock.instance().now().timestamp();
						Instant eventTime=tf.newMoment(time);
						Kernel.getInstance().dispatch(new JobCanceledEvent (eventTime, job, job.getProvenance().getLastCondition()));
					}
				}
				else
				{
					j.getReleasedSite().getReleasedSiteQueue().getQueue().remove(job);
				}
			}
		}
	}
	
	
	public int getNumberOfExecReplicas(Job j)
	{
		List <SWFJob> replicasToCancel=replicaSearch((SWFJob)j);
		int n=0;
		for(SWFJob job:replicasToCancel)
		{
			if(!j.getReleasedSite().getReleasedSiteQueue().getQueue().contains(job))
			{
				if (job.getProvenance().getLastCondition()!=null
						&& job.getProvenance().getLastCondition()!=job.getReleasedSite())
				{
					n++;
				}
			}
		}
		return n;
	}
	
	public List <SWFJob> replicaSearch(SWFJob j)
	{
		List<SWFJob> retList=null;
		for (List<SWFJob> list: replicatedJobs)
		{
			if (list.contains(j))
			{
				retList=list;
				break;
			}
		}
		if (retList==null)
		{
			retList=new ArrayList();
			retList.add(j);
			replicatedJobs.add(retList);
		}
		return retList;
	}
	
	public void remove(SWFJob j)
	{
		List <SWFJob> jobReplicas=replicaSearch(j);
		jobReplicas.remove(j);
	}
	
	public SWFJob makeReplica(SWFJob j)
	{
		SWFJob job = new SWFJob(null);
		List <SWFJob> jobReplicas=replicaSearch(j);
		job.setJobNumber(j.getJobNumber());									// 1
		job.setSubmitTime(j.getSubmitTime());									// 2
		job.setWaitTime(j.getWaitTime());										// 3
		job.setRunTime(j.getRunTime());										// 4
		job.setNumberOfAllocatedProcessors(j.getNumberOfAllocatedProcessors());				// 5
		job.setAverageCPUTimeUsed(j.getAverageCPUTimeUsed());						// 6
		job.setUsedMemory(j.getUsedMemory());					// 7
		job.setRequestedNumberOfProcessors(j.getRequestedNumberOfProcessors());	// 8
		job.setRequestedTime(j.getRequestedTime());				// 9
		job.setRequestedMemory(j.getRequestedMemory());				// 10
		job.setStatus(j.getStatus());						// 11
		job.setUserID(j.getUserID());						// 12
		job.setGroupID(j.getGroupID());						// 13
		job.setExecutableApplicationNumber(j.getExecutableApplicationNumber());	// 14
		job.setQueueNumber(j.getQueueNumber());					// 15
		job.setPartitionNumber(j.getPartitionNumber());				// 16
		job.setPrecedingJobNumber(j.getPrecedingJobNumber());			// 17
		job.setThinkTimeFromPrecedingJob(j.getThinkTimeFromPrecedingJob());	// 18 */
		job.setPriority(j.getPriority()-1);										// 19	
		job.setJobType(j.getJobType());
		job.setReleasedTime(j.getReleaseTime());
		jobReplicas.add(job);
		return job; 
	}
}
