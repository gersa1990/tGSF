package mx.cicese.mcc.teikoku.metrics;

import java.util.Map;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.InformationType;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationData;
import mx.cicese.dcc.teikoku.information.broker.SiteStatusInformation;
import mx.cicese.dcc.teikoku.metrics.MetricHelper;
import mx.cicese.mcc.teikoku.kernel.events.SitePowerOffEvent;
import de.irf.it.rmg.core.teikoku.exceptions.SubmissionException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;

public class EBoT_Site extends AbstractMetric {
	/**
	 * Initialization flag
	 */
	private boolean initialized;
	
	/**
	 * Total number of jobs 
	 */
	private double n;
	
		
	/**
	 * Sum of waiting times
	 */
	private double swt;
	
	
	/**
	 * Sum of weighted waiting time (a = area)
	 */
	private double swct;
	
	
	/**
	 * Sum of slowdown
	 */
	private double sbssd;
	
	
	/**
	 * Used for estimation of the competitive factor
	 *  - sja, sum of job areas (p * size)
	 *  - max_rp, largest size job
	 */
	private double sja;
	private double max_rp;
	
	/**
	 * Machine size
	 */
	private long m;

	
	/**
	 * C_opt
	 */
	private double C_opt;
	
	
	/*
	 * 
	 */
	
	private Object[] temporalValues;
		
	public EBoT_Site() {
		super();
		this.initialized = false;
		this.n = 0;
		this.swt = 0;
		this.swct = 0;
		this.sbssd = 0;
		this.sja = 0;	
		this.max_rp = -1;
		this.m = 0;		
		this.temporalValues = new Object[9];
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[9];
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();
		/*
		cmax (imp)
		waiting time (imp)
		Numero Replicas Ejec
		Waisted Time 
		sj
		cj
		*/
		values[0] = "job_id ";
		values[1] = "cmax ";							// c, job completion time, sec
		values[2] = "Mean_waiting_time ";				// MWT, Mean waiting time, sec 
		values[3] = "Mean_bounded_slowdow ";			// MBS, Mean bounded slowdown, sec
		values[4] = "Sum_weighted_completion_time ";	// SWCT, Sum of weighted completion time, sec
		values[5] = "Competitive_factor ";				// rho, Competitive factor
		values[6] = "Utilization ";						// Utilization
		values[7] = "Number_of_executed_replicas ";		// Number of executed replicas
		values[8] = "Energy ";							// Energy consumed

		return values;
	} // End getHeader
	
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			//if (job.getReleasedSite()==this.getSite()){
				this.handleCompletedEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			//}
		}
	}// End deliverCompletedEvent
	

	@AcceptedEventType(value=SitePowerOffEvent.class)
	public void deliverSitePowerOffEvent(SitePowerOffEvent e)
	{
		if (e.getTags().containsKey(this.getSite().getUUID().toString())){
			this.handleSitePowerOffEvent(this.getSite());
		}
	}

	private void handleCompletedEvent(Job job, long timestamp) {
		
		/* 
		 * If the machine size is not known, query it
		 */
		if(!initialized) 
			m = super.getSite().getSiteInformation().getNumberOfAvailableResources();
		
		MetricHelper mh = new MetricHelper();

		this.n++;
		double p = TimeHelper.toSeconds(job.getDuration().distance());
		double c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double size = ((SWFJob) job).getRequestedNumberOfProcessors();
		double wt = mh.p_wait_time((SWFJob)job);
		
		
		swt += wt;									// Sum of waiting times
		sbssd += ((c-r) / Math.max(10, p));  		// Sum bounded slowdown
		swct += c * (p * size);						// SWCT, Sum of weighted completion time, sec
		// Estimation of the competitive factor
		if( (r+p) > max_rp )						// Search and select the largest job
			max_rp = (r + p);
		sja += (p * size);							// Sum of job areas
		C_opt = Math.max(max_rp, (sja/m));
		
		
		this.temporalValues[0] = ((SWFJob)job).getJobNumber();			// job id
		this.temporalValues[1] = (long) c;			// c, job completion time, sec
		this.temporalValues[2] = (1/n) * swt;		// AWT, Mean waiting time,sec
		this.temporalValues[3] = (1/n) * sbssd;		// MBS, Mean bounded slowdown, sec
		this.temporalValues[4] = swct; 				// SWCT, Sum of weighted completion time, sec
		this.temporalValues[5] = (c / C_opt);		// rho, Competitive factor
		this.temporalValues[6] = sja / (c * m);		// Utilization
		this.temporalValues[7] = job.getReleasedSite().getReplicationControl().getNumberOfExecReplicas(job);
		
		} // End handleEvent
	
	private void handleSitePowerOffEvent(Site site) {
		// TODO Auto-generated method stub
		Object[] values = this.getLatestValuesPrototype();
		double e = site.getSiteEnergyManager().getEnergyConsumption();
		values[0]=this.temporalValues[0];
		values[1]=this.temporalValues[1];
		values[2]=this.temporalValues[2];
		values[3]=this.temporalValues[3];
		values[4]=this.temporalValues[4];
		values[5]=this.temporalValues[5];
		values[6]=this.temporalValues[6];
		values[7]=this.temporalValues[7];
		values[8] = e;		// Utilization
		super.setLatestValues(values);
		super.manualMakePermanent();	
	}
}