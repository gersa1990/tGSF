package mx.cicese.mcc.teikoku.scheduler.util;

import java.util.ArrayList;
import java.util.List;

import mx.cicese.mcc.teikoku.kernel.events.JobReplicaReleasedEvent;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class ReleasedSiteQueue {

	List <Job> queuedJobs;
	Site site;
	
	public ReleasedSiteQueue(Site s)
	{
		site = s;
		queuedJobs = new ArrayList();
	}
	
	public void queue (Job j)
	{
		//j.getLifecycle().addEpisode(State.ABORTED);
		queuedJobs.add(j);
	}
	
	public void notify(Event e)
	{
		while(!queuedJobs.isEmpty())
		{
			Job job = queuedJobs.get(0);
			this.queuedJobs.remove(0);
			State st = job.getLifecycle().getLastCondition();
			
			if(st==null)
			{
				if(site.getReplicationControl().replicaSearch((SWFJob) job).size()>1)
				{
					Kernel.getInstance().dispatch(new JobReplicaReleasedEvent(site.getActivityBroker(),e.getTimestamp(), 
							site.getReplicationControl().makeReplica((SWFJob) job),
							site, site.getReplicationControl().replicaSearch((SWFJob) job).size()));
				}
				else
				{
					Kernel.getInstance().dispatch(new JobReleasedEvent(site.getActivityBroker(),e.getTimestamp(), 
							site.getReplicationControl().makeReplica((SWFJob) job), site));
				}
				site.getReplicationControl().remove((SWFJob) job);
			}
			else
			{
				switch (st)
				{
					case RELEASED:
						if(job.getReleasedSite().getReplicationControl().replicaSearch((SWFJob) job).size()>1)
						{
							Kernel.getInstance().dispatch(new JobReplicaReleasedEvent(site.getActivityBroker(),e.getTimestamp(), 
							site.getReplicationControl().makeReplica((SWFJob) job), site, 
							job.getReleasedSite().getReplicationControl().replicaSearch((SWFJob) job).size()));
						}
						else
						{
							Kernel.getInstance().dispatch(new JobReleasedEvent(site.getActivityBroker(),e.getTimestamp(), 
							site.getReplicationControl().makeReplica((SWFJob) job), site));
						}
						site.getReplicationControl().remove((SWFJob) job);
						break;
					case QUEUED:
						Kernel.getInstance().dispatch(new JobQueuedEvent(e.getTimestamp(), job, job.getProvenance().getLastCondition()));
						break;
				}
			}
		}
	}
	
	public List <Job> getQueue()
	{
		return queuedJobs;
	}
}
