package mx.cicese.dcc.teikoku.scheduler.strategy.composite;

import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.Estimate;
import mx.cicese.dcc.teikoku.scheduler.priority.DownwardRank;
import mx.cicese.dcc.teikoku.scheduler.priority.UpperRank;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


public class CPOP implements CStrategy {

	
	/**
	 * Site 
	 */
	Site site;

	
	/**
	 * The ranking strategy
	 */ 
	private DownwardRank<SWFJob,Precedence> downRankLabeler;

	
	/**
	 * The ranking strategy
	 */ 
	private UpperRank<SWFJob,Precedence> upRankLabeler;

	
	/**
	 * Class constructor 
	 */
	public CPOP() {
		 downRankLabeler = new DownwardRank<SWFJob,Precedence>();
		 upRankLabeler = new UpperRank<SWFJob,Precedence>();
	}// End CPOP
	
	
	/**
	 * 	Estimates the ranking of the composite job
	 * 
	 *  @param g	the composite job structure 
	 */
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb){
		Map<SWFJob,Number> priority = new HashMap<SWFJob,Number>();
		
		downRankLabeler.clear();
		downRankLabeler.compute(g);
		Map<SWFJob,Number> downRank = downRankLabeler.getRanking();
		upRankLabeler.clear();
		upRankLabeler.compute(g);
		Map<SWFJob,Number> upRank = upRankLabeler.getRanking();
		for(SWFJob j : downRank.keySet()) {
			double val = ((Number)downRank.get(j)).doubleValue() + ((Number)upRank.get(j)).doubleValue();
			priority.put(j, val);
		}
		jcb.setRanking("rank",priority);
		jcb.setOrdRanking(StrategyHelper.sortDecreasing(priority));
	}// getRanking
	
	
	/**
	 *	Schedules a composite job and updates the plan 
	 * 
	 * 	@param job		the composite job to schedule
	 * 	@param plan		a plan
	 */
	public void schedule(Job job, JobControlBlock jcb) {
		
		AllocationEntry entry = null;
		List<SWFJob> setCP = jcb.getCP();
		
		// Line 5. Select the highest priority job.
		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
		List<SWFJob> ordIndJobs = StrategyHelper.getSortedIndpJobs(jcb.getOrdRanking(),
								CompositeJobUtils.getIndependentJobs(g));
		long now = Clock.instance().now().timestamp();
		while(ordIndJobs.size() != 0) {
			SWFJob nk = this.highestPriorityEntry(ordIndJobs, jcb.getRanking("rank"));
			ordIndJobs.remove(nk);
			
			//Update the job state
			if( nk.getLifecycle().getLastCondition()== null )
				nk.getLifecycle().addEpisode(State.RELEASED);
			
			if( setCP == null ) {
				// 	Line 6.  
				setCP = new LinkedList<SWFJob>(); 
				setCP.add(nk);
				// Line 8 to 12.
				this.getCP(nk, g, jcb.getRanking("rank"), setCP);
				jcb.setCP(setCP);
				// Line 13. Elect the critical path processor.
				UUID cpProc = this.selectCPProcessor(job, setCP, g, jcb);
				jcb.setCPProc(cpProc);
			
				// The moment the job is freed, then the member job state must be set to RELEASED
				//nk.getLifecycle().addEpisode(State.RELEASED);
				entry = new AllocationEntry(nk, cpProc, jcb.getRanking("rank",nk));
			} else {
				if( setCP.contains(nk) ) {
					long submitTime = Math.max(now, CompositeJobUtils.getMaximumStartTime(g, nk, jcb));
					nk.setSubmitTime(submitTime);
					// The moment the job is freed, then the member job state must be set to RELEASED
					//nk.getLifecycle().addEpisode(State.RELEASED);
					entry = new AllocationEntry(nk, jcb.getCPProc(), jcb.getRanking("rank",nk));
					// Make a reservation the target site.
				} else {
					//Assign the job to a processor that minimizes the EST
					double minEstimate = Double.MAX_VALUE;
					UUID minSite = null;
					// Must assign the maximum predecessor release time. 
					long submitTime = Math.max(now, CompositeJobUtils.getMaximumStartTime(g, nk, jcb));
					nk.setSubmitTime(submitTime);
					// The moment the job is freed, then the member job state must be set to RELEASED
					//nk.getLifecycle().addEpisode(State.RELEASED);
					GridInformationBroker gInfoBroker = site.getGridInformationBroker();
					
					LinkedList<Job> jobs = new LinkedList<Job>();
					jobs.add(nk);
					
					//Set the job type as independent, then reset to its original type.
					JobType originalType = nk.getJobType();
					nk.setJobType(JobType.INDEPENDENT);
					
					//Map<UUID,SiteInformationData> avail  = gInfoBroker.pollAllSites(InformationType.ESTIMATE, jobs, jcb);
					Map<UUID,SiteInformationData> avail  = gInfoBroker.pollAllSites(InformationType.EARLIEST_START_TIME, jobs, jcb);
					nk.setJobType(originalType);
			
					for(UUID s : avail.keySet()) {
						double c = ((Estimate)avail.get(s)).earliestStartTime.get(nk).timestamp();
						if( c < minEstimate ){
							minEstimate = c;
							minSite = s;
						} //End if
					}//End for
					entry = new AllocationEntry(nk,minSite,jcb.getRanking("rank",nk));
					//Make a reservation at target site.
				}// End else
			}//End else
			jcb.addEntry(entry, entry.getDestination());
		} //End do
	}//End schedule
	
	
	/**
	 * Basic setter, sets this strategy site
	 * 
	 * @param site		the strategies site
	 */
	public void setSite(Site site) {
		this.site = site;
	}// End setSite
	
	
	public void initialize() {}
	
	
	/**
	 * Gets the highest priority entry job
	 * 
	 * @param indJobs	the set of independent jobs
	 * @param priority	the set of priorities 
	 * @return			the highest priority job
	 */
	private SWFJob highestPriorityEntry(List <SWFJob> indJobs, Map<SWFJob,Number> priority){
		SWFJob maxJob = null;
		double max = Double.MIN_VALUE, p;

		for(Iterator<SWFJob> it = indJobs.iterator(); it.hasNext();){
			SWFJob j = it.next();
			p = priority.get(j).doubleValue();//valor maximal;
			if(p>max){
				max = p;
				maxJob = j;
			} //End if
		} //End for
        return maxJob;
	} //End getHighestPriorityEntry
	
	
	/**
	 * Creates the critical path
	 * 
	 * @param j
	 * @param g
	 * @param priority
	 * @param setCP
	 * @return
	 */
	private  void getCP(	SWFJob j, Hypergraph<SWFJob,Precedence> g, 
							Map<SWFJob,Number> priority, List<SWFJob> setCP){
		double p = priority.get(j).doubleValue();
        
        while(true){
        	if( g.outDegree(j) == 0)  
        		break;
        	else {
        			for(SWFJob nj: g.getSuccessors(j)){
        				if(priority.get(nj).doubleValue() == p){
        					setCP.add(nj);
        					j = nj;
        				} //End if
        		}//End for
        	}//End else
        } //End while
    } // getSetCP

		
	/**
	 * Selects the processor that minimizes the CP
	 * 
	 * @param site		
	 */
	private UUID selectCPProcessor(Job job, List<SWFJob> setCP, Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb){
		UUID minCPP = null;
		double minCmax = Double.MAX_VALUE;

		//Determine the CP processor
		GridInformationBroker gInfoBroker = site.getGridInformationBroker();
		List<UUID> knownInfBrokers = gInfoBroker.getKnownInformationBrokers();
		
		for(Iterator it = knownInfBrokers.iterator(); it.hasNext();){
			UUID s = (UUID) it.next();
			// Determine the minimum release time, this would be the release
			// time of the composite job
			long releaseTime = job.getReleaseTime().timestamp();
			for(Iterator itCP = setCP.iterator(); itCP.hasNext();){
				SWFJob j = (SWFJob) itCP.next();
				// Backup information...falta conversion
				long oldSubTime = j.getSubmitTime();
				j.setSubmitTime(DateHelper.convertToSeconds(releaseTime));
				

				if( !(j.getLifecycle().getLastCondition()== null) ){
					if(!j.getLifecycle().getLastCondition().equals(State.RELEASED))	
						j.getLifecycle().addEpisode(State.RELEASED);
				} else
					j.getLifecycle().addEpisode(State.RELEASED);
				
				LinkedList<Job> jobs = new LinkedList<Job>();
				jobs.add(j);
				
				//Set the job type as independent, then reset to its original type.
				JobType originalType = j.getJobType();
				j.setJobType(JobType.INDEPENDENT);		
				
				//SiteInformationData est = gInfoBroker.pollSite(s, InformationType.ESTIMATE, jobs, jcb);
				SiteInformationData est = gInfoBroker.pollSite(s, InformationType.EARLIEST_START_TIME, jobs, jcb);
				j.setJobType(originalType);
				
				releaseTime = ((Estimate) est).earliestStartTime.get(j).timestamp() +
					DateHelper.convertToMilliseconds(j.getRequestedTime());
				
				// Restore old submittime
				j.setSubmitTime(oldSubTime);
			}//End for
			if(releaseTime < minCmax){
				minCmax = releaseTime;
				// 	This variable represents the information broker ID. Not the site ID. Quering site ID is required.
				minCPP = gInfoBroker.getKnownSiteUUID(s); 
			} // End if
		}//End for
		return minCPP;
	}// End setSite
} //End CPOP


