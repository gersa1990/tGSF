package mx.cicese.dcc.teikoku.scheduler.strategy.composite;

import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.scheduler.priority.DownwardRank;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.sim.kuiga.Clock;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.Collections;


public class HEFT implements CStrategy{
	/**
	 * Site 
	 */
	Site site;

	/**
	 * The ranking strategy
	 */ 
	private DownwardRank<SWFJob,Precedence> rankLabeler;
	
	/**
	 * Class constructor 
	 */
	public HEFT() {
		 rankLabeler = new DownwardRank<SWFJob,Precedence>();
	}// End HEFT
	
	/**
	 * 	Estimates the ranking of the composite job
	 * 
	 *  @param g	the composite job structure 
	 */
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb){
		rankLabeler.clear();
		rankLabeler.compute(g);
		jcb.setRanking("downward",rankLabeler.getRanking());
		jcb.setOrdRanking(StrategyHelper.sortDecreasing(rankLabeler.getRanking()));
	}// getRanking

	/**
	 *	Schedules a composite job and updates the jcb 
	 * 
	 * 	@param job		the composite job to schedule
	 * 	@param jcb		a jcb
	 */
	@SuppressWarnings(value = "unchecked")
	public void schedule(Job job, JobControlBlock jcb) {
		Map<UUID,SiteInformationData> avail = null;
		Set <Value> EST = new HashSet<Value>();
		GridInformationBroker gInfoBroker = site.getGridInformationBroker();
		
		/* Create the list of schedulable jobs */
		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
		List<SWFJob> indJobs = CompositeJobUtils.getIndependentJobs(g);
		List<SWFJob> ordIndJobs = StrategyHelper.getSortedIndpJobs(jcb.getOrdRanking(),indJobs);	
		
		/* Select the first job from the list for scheduling */
		for(Iterator it = ordIndJobs.iterator(); it.hasNext();) {
			SWFJob m = (SWFJob)it.next();
			// Define this job state
			m.getLifecycle().addEpisode(State.RELEASED);
			
			/* Update m's release time to max AFT of predeccesor nodes (if any) */
			if(g.inDegree(m) != 0) {
				long r = DateHelper.convertToSeconds(CompositeJobUtils.getMaximumStartTime(g, m, jcb));
				m.setSubmitTime(r);
			} //End if
			
			LinkedList<Job> jobs = new LinkedList<Job>();
			jobs.add(m);
			
			//avail = gInfoBroker.pollAllSites(InformationType.ESTIMATE, jobs, jcb);
			avail = gInfoBroker.pollAllSites(InformationType.EARLIEST_START_TIME, jobs, jcb);
			/* For each known site */
			double minEFT = Double.MAX_VALUE;
			UUID minSite = null;
			for(UUID s : avail.keySet()) {
				double eft = computeEFT(avail, g, s, m, jcb);
				if(eft < minEFT ) {
					minEFT = eft;
					minSite = s;
				}
			}
			//	EST.add(new Value(s,computeEFT(avail, g, s, m, jcb)));
			//Value min = (Value) Collections.min(EST, new HEFT.ValueComparator());
			
			//System.out.println( site.getGridInformationBroker().getKnownSite((UUID)min.getKey()).getName() + " for task " + m.getName() );
			//System.out.println( site.getGridInformationBroker().getKnownSite(minSite).getName() + " for task " + m.getName() );
			
			/* Update the scheduling jcb */
			//AllocationEntry entry = new AllocationEntry(m,(UUID)min.getKey(),jcb.getRanking("downward",m));
			AllocationEntry entry = new AllocationEntry(m,minSite,jcb.getRanking("downward",m));
			jcb.addEntry(entry, entry.getDestination());
		}// End for	
	}//End schedule
	
	
	/**
	 * Computes the Earliest Start Time of a job m at site s
	 * 
	 * @param avail		a map containg earliest availability times at all known sites
	 * @param g			a graph containing precedence constraints
	 * @param s			a site UUID
	 * @param m			the job to schedule
	 * @param jcb		the jcb holding the AFT times
	 * @return			the EFT of job m in site s
	 */
	private long computeEFT(Map<UUID,SiteInformationData> avail, 
			Hypergraph<SWFJob,Precedence> g, UUID s, SWFJob m, 
			JobControlBlock jcb){

		//double eat = ((Estimate)avail.get(s)).earliestAvailTime.get(m).timestamp();
		double est = ((Estimate)avail.get(s)).earliestStartTime.get(m).timestamp();
		double aft = predecessorsMaxAFT(g,m,jcb);
		double max = (long) Math.max(est, aft);
		return (long) max;
	}
		
	/**
	 * Gets the maximum Absolute Finishing Time (AFT) of 
	 * all predecessors of a given job
	 * 
	 * @param g			a graph containing precedence constraints
	 * @param m			the successor job
	 * @param jcb		the jcb holding the AFT times 
	 * @return			the maximum AFT 
	 */
	@SuppressWarnings(value = "unchecked")
	private long predecessorsMaxAFT(Hypergraph<SWFJob,Precedence> g, 
				SWFJob m, JobControlBlock jcb) {
		long max = 0;
		Set <Value> AFT = new HashSet<Value>();
		
		if(g.getPredecessors(m).size()!= 0) {
			for(SWFJob p : g.getPredecessors(m))
				AFT.add(new Value(p,jcb.getAFT(p)));
			max = ((Value)Collections.max(AFT, new HEFT.ValueComparator())).getValue().longValue();
		}// End if
		
		return max;
	}// End predecessorsMaxAFT

		
	/**
	 * Basic setter, sets this strategy site
	 * 
	 * @param site		the strategis site
	 */
	public void setSite(Site site) {
		this.site = site;
	}// End setSite
	

	public void initialize() {}
	
	
	/* 
	 * Auxilary classes used for sorting or getting the minimum or maximum value
	 * in a given set.
	 */
	static class ValueComparator implements Comparator {
		public int compare(Object obj1, Object obj2) {
			if(!(obj1 instanceof Value) || !(obj2 instanceof Value))
				throw new ClassCastException();
			long value = ((Value)obj1).getValue().longValue() - ((Value)obj2).getValue().longValue();
			return (new Double(Math.floor(value))).intValue();
		}
	}
	
	private static class Value{
		private Object v;
		private Number n;
		
		public Value(Object v, Number n){
			this.v = v;
			this.n = n;
		}
		
		public Number getValue() {
			return this.n;
		}
		
		public Object getKey() {
			return this.v;
		}
	} //End Value
	
}// End HEFT
