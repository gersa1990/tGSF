package mx.cicese.dcc.teikoku.metrics.Grid;

import java.util.Map;
import java.util.UUID;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.WorkflowCompletedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.metrics.MetricHelper;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;

public class WorkflowJob_Grid extends AbstractMetric {
	
	/**
	 * The number of scheduled composite jobs
	 */
	private double n;
	
	
	/**
	 * Sum of workflow waiting times
	 */
	private double swt;
	
	
	/**
	 * Sum of critical path slow down
	 */
	private double cps;
	
	
	/**
	 * Sum of workflow lenght ratio
	 */
	private double swlr;
	
	/**
	 * Approximation factor (only ofr workflows)
	 */
	private double sumAreas;
	
	
	/**
	 * Initialization flag
	 */
	private boolean initialized;
	
	
	/**
	 * Grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	
	/**
	 * Machine size, it accounts for all machines in the Grid
	 */
	private long m;

	
	/**
	 * Class constructor
	 */
	public WorkflowJob_Grid() {
		super();
		this.n = 0;
		this.swt = 0;
		this.cps = 0;
		this.swlr = 0;
		this.sumAreas = 0;
		this.initialized = false;
		this.m = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[4];
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "cmax ";
		values[1] = "mean_waiting_time ";		
		values[2] = "mean_crital_path_slowdown ";
		values[3] = "Approximation_factor ";

		
		return values;
	} // End getHeader
	
	
	@AcceptedEventType(value=WorkflowCompletedEvent.class)
	public void deliverCompletedEvent(WorkflowCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			//if (job.getReleasedSite()==this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			//}
		}
	}// End deliverCompletedEvent
	
	
	
	private void handleEvent(Job job, long timestamp) {
		Object[] values = this.getLatestValuesPrototype();
		
		/* 
		 * If machines sizes are not known query them
		 */
		if(!initialized) {
			Map<UUID,SiteInformationData> statInfo = null;
			
			this.gInfoBroker = super.getSite().getGridInformationBroker();
			statInfo = gInfoBroker.pollAllSites(InformationType.STATUS, null, null);
			
			for(UUID s : statInfo.keySet()) {
				double mi = (double) ((SiteStatusInformation)statInfo.get(s)).numProcessors;
				this.m += mi;
			} //End for
		}
		
		
		
		MetricHelper mh = new MetricHelper();
		
		this.n++;
		swt += mh.w_getWaitingTime(job);
		double sp = mh.w_getCritalPathSlowDown(job); // No esta sumando el tiempo de liberacion.
		cps += sp;
		
		
		/* Get the approximation ratio */
		double cp = mh.w_getCriticalPathCost(job);
		double cpc = cp  + DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double tja = mh.w_getTotalWork(job);
		this.sumAreas += tja;
		double apf = Math.max((this.sumAreas/this.m), cpc);
		
		
		double cmax = mh.w_getCmax(job); 
		
		values[0] = cmax;
		values[1] = (1/n) * swt; 
		values[2] = (1/n) * cps;
		values[3] = (cmax/apf);
				
		mh.clear();
		
		// System.out.println("Workflow id : " + job.getName() + " completed");
		super.setLatestValues(values);
		super.manualMakePermanent();
	} // End handleEvent

}
