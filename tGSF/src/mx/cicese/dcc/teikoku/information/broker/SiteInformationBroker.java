package mx.cicese.dcc.teikoku.information.broker;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.job.Job;
import java.util.UUID;
import java.util.Queue;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;



public interface SiteInformationBroker {
	
	public void notifyGridInfBroker();

	public SiteInformationData getPublicInformation(InformationType infoType, Queue<Job> jobs, JobControlBlock jcb);
	
	public void setSite(Site site);
	
	public Site getSite();
	
	public void setRefreshRate(long refreshRate);
	
	public UUID getUUID();
	
}
