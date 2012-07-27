package mx.cicese.dcc.teikoku.scheduler.strategy.composite;

import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public interface CStrategy{
	
	public void schedule(Job job, JobControlBlock jcb);
	
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb);
	
	public void setSite(Site site);
	
	public void initialize();
	
} //End Strategy
