package mx.cicese.mcc.teikoku.scheduler.SLA.strategy;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Admissible;
import mx.cicese.dcc.teikoku.scheduler.strategy.rigid.RStrategy;
import mx.cicese.mcc.teikoku.scheduler.SLA.events.JobRejectedEvent;

public class RandomStrategy extends Admissible  implements RStrategy {

	final private static Log log = LogFactory.getLog("debugger");

	
	private Site site;
	
	public RandomStrategy ()
	{
		this.site = null;
	}
	@Override
	public AllocationEntry schedule(Job job) {
		UUID siteId = null;
		int i = 0;
		long numKnownSites = 0;
		List<UUID> knownSites = null;
		
		String startedMsg = Clock.instance().now().timestamp() +"\tRELEASED\t" +site.getName() 
				+ "\t" + job.getName();
		
		log.trace(startedMsg);
		
		/* Admissibility */
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		super.initialize(site);
		knownSites = super.getAdmissableSet(size);
		numKnownSites = knownSites.size(); 
		
		SiteSLAAccepter slaAccepter = new SiteSLAAccepter();
		slaAccepter.initialize(site);
		
				
		/* Select a machine randomly */
		boolean accepted = false;
		do{
		i = (int) Math.round((Math.random()*(numKnownSites-1)));
		siteId = knownSites.get(i);
		//Once the machine is selected we verify if this machine is able to execute the given job
		accepted = slaAccepter.getAcceptanceTest(job, siteId);
		if(accepted)
			break;
		else
		{
			knownSites.remove(i);
			numKnownSites = knownSites.size(); 
		}
		}while(!knownSites.isEmpty());
		AllocationEntry entry =null;
		if(accepted){
				 entry = new AllocationEntry((SWFJob)job, siteId, -1);
				 String acceptedMsg = Clock.instance().now().timestamp() +"\tACCEPTED\t"+ site.getGridInformationBroker().getKnownSite(siteId).getName()+"\t"+
						 job.getName();
				log.trace(acceptedMsg);
		}
		else
		{
		
			// we notify the rejection of the released job
			Instant timeEvent = Clock.instance().now();
			JobRejectedEvent event = new JobRejectedEvent(timeEvent,job,site);
			Kernel.getInstance().dispatch(event);
		}
		return entry;
	}

	@Override
	public void setSite(Site site) {
		// TODO Auto-generated method stub
		this.site = site;

	}

}
