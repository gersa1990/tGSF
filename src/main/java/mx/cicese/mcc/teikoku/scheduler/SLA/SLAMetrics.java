package mx.cicese.mcc.teikoku.scheduler.SLA;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mx.cicese.mcc.teikoku.scheduler.SLA.events.JobRejectedEvent;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

public class SLAMetrics extends AbstractMetric {
	
	private double startScheduler = Double.MAX_VALUE;
	
	final private static Log log = LogFactory.getLog("debugger");

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
	 * Sum of turnaround times
	 */
	private double stat;
	
	/**
	 * Sum of interruptions 
	 */
	private int tni;
	
	/**
	 * Number of rejected jobs
	 */
	
	private int rej;

	
	/**
	 * Register how much the system has obtained  for executing jobs
	 */
	private double c_gain;
	
	/**
	 * Used for estimation of the competitive factor
	 *  - sja, sum of job areas (p * size)
	 *  - max_rp, largest size job
	 */
	private double sja;
		
	private double max_rp;
	
	private int s; // number of Sites
	
	//number of processors in our study just considering one
	private int m = 1;
	
	//processing time of jobs with SLAs
	private long[] SLAp = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	
	public SLAMetrics() {
		super();
		this.n = 0;
		this.swt = 0;
		this.tni = 0;
		this.rej = 0;
		this.c_gain = 0;
		this.initialized = false;
		this.s = 1;
		this.stat = 0;

	}
	
	@Override
	protected Object[] getHeader() {
		Object [] values = this.getLatestValuesPrototype();
		
		values[0] = "job_id ";
		values[1] = "Cmax ";
		values[2] = "Mean_waiting_time ";
		values[3] = "Mean_bounded_slowndown ";
		values[4] = "Mean_number_of_interruptions_per_job ";
		values[5] = "Percentage_of_rejections ";
		values[6] = "Competitive_factor ";
		values[7] = "Utilization ";
		values[8] = "Total_processing_time ";
		values[9] = "Processing_Time_SLA_0";
		values[10] = "Processing_Time_SLA_1";
		values[11] = "Processing_Time_SLA_2";
		values[12] = "Processing_Time_SLA_3";
		values[13] = "Processing_Time_SLA_4";
		values[14] = "Processing_Time_SLA_5";
		values[15] = "Processing_Time_SLA_6";
		values[16] = "Processing_Time_SLA_7";
		values[17] = "Processing_Time_SLA_8";
		values[18] = "Processing_Time_SLA_9";
		values[19] = "Processing_Time_SLA_10";
		values[20] = "Processing_Time_SLA_11";
		values[21] = "Processing_Time_SLA_12";
		values[22] = "Processing_Time_SLA_13";
		values[23] = "Processing_Time_SLA_14";
		values[24] = "Processing_Time_SLA_15";
		values[25] = "Processing_Time_SLA_16";
		values[26] = "Processing_Time_SLA_17";
		values[27] = "Processing_Time_SLA_18";
		values[28] = "Processing_Time_SLA_19";
		values[29] = "System_benefit";
		return values;
	}

	@Override
	protected Object[] getLatestValuesPrototype() {
		
		return new Object[30];
	}
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event)
	{
		
		Job  job = event.getCompletedJob();
		//First check tag-existence
		
		if (event.getTags().containsKey(this.getSite().getUUID().toString()))
		{
			//Only calculate the new Value if the metric is the metric at the release site
			
			this.handleEvent(job, DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			
		}//if
	}// deliverCompletedEvent
	
	@AcceptedEventType(value=JobRejectedEvent.class)
	public void deliverJobRejectedEvent(JobRejectedEvent event)
	{
		Job job = event.getRejectedJob();
		
			//Only calculate the new Value if the metric is the metric at the release site
			
			this.handleRejectedJobEvent(job, DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			

	}//deliverJobRejectedEvent

	private void handleEvent(Job job, long timestamp) {
		//get number of sites
		
		/*
		 * If the size of the machine is unknown query it
		 */
		if(!initialized) {
			m = super.getSite().getSiteInformation().getNumberOfAvailableResources();
			s = super.getSite().getGridInformationBroker().getKnownSites().size();
		
		}
		
		if(startScheduler>DateHelper.convertToSeconds(((SWFJob)job).getReleaseTime().timestamp()))
		{
			startScheduler = DateHelper.convertToSeconds(((SWFJob)job).getReleaseTime().timestamp());
		}
		
		
		System.out.println("++++++++++++"+ super.getSite().getName());
		
		Object[] values = this.getLatestValuesPrototype();
		MetricHelper mh = new MetricHelper((SWFJob)job);
		
		this.n++;
		double p = TimeHelper.toSeconds(job.getRuntimeInformation().getRunTime());
		double c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double startc = c - startScheduler;
		
		
		double wt = mh.wait_time((SWFJob)job);
		//double tat = mh.turnaround_time((SWFJob) job);
		int ni = ((SWFJob)job).getRuntimeInformation().getNumberOfInterruptions();
		double tat = mh.slow_down((SWFJob)job);
		
		
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double size = ((SWFJob) job).getRequestedNumberOfProcessors();
		 
		c_gain = c_gain + p*job.getSla().getPrice();
		
		SLAp[job.getSla().getType()] += p; 
		
		if( (r+p) > max_rp )						// Search and select the largest job
			max_rp = (r + p);
		sja += (p * size);			
		
		swt += wt;
		stat += tat;
		tni += ni;
		
		
		values[0] = ((SWFJob)job).getJobNumber();			// job id
		values[1] = (long) c;			// c, job completion time, sec
		values[2] = (1/n) * swt;		// AWT, Mean waiting time, sec
		values[3] = (1/n) * stat;		// MSD, Mean bounded slow down 
		values[4] =  (1/n)*tni;// Mean  number of Interruptions per job 
		values[5] = (1/(n+rej))*rej*100.0; // percentaje of rejected jobs
		values[6] = (c_gain / (startc * s) );		// rho, Competitive factor
		values[7] = sja / (startc * m * s);		// Utilization
		values[8] = sja; // Total processing time
		values[9] = SLAp[0]/sja; // processing time of job with SLA 0
		values[10] = SLAp[1]/sja; //processing time of job with SLA 1
		values[11] = SLAp[2]/sja; //processing time of job with SLA 2
		values[12] = SLAp[3]/sja; // processing time of job with SLA 3
		values[13] = SLAp[4]/sja; //processing time of job with SLA 4
		values[14] = SLAp[5]/sja; //processing time of job with SLA 5
		values[15] = SLAp[6]/sja; // processing time of job with SLA 6
		values[16] = SLAp[7]/sja; // processing time of job with SLA 7
		values[17] = SLAp[8]/sja; //processing time of job with SLA 8
		values[18] = SLAp[9]/sja; //processing time of job with SLA 9
		values[19] = SLAp[10]/sja; // processing time of job with SLA 10
		values[20] = SLAp[11]/sja; //processing time of job with SLA 11
		values[21] = SLAp[12]/sja; //processing time of job with SLA 12
		values[22] = SLAp[13]/sja; // processing time of job with SLA 13
		values[23] = SLAp[14]/sja; // processing time of job with SLA 14
		values[24] = SLAp[15]/sja; //processing time of job with SLA 15
		values[25] = SLAp[16]/sja; //processing time of job with SLA 16
		values[26] = SLAp[17]/sja; // processing time of job with SLA 17
		values[27] = SLAp[18]/sja; //processing time of job with SLA 18
		values[28] = SLAp[19]/sja; //processing time of job with SLA 19
		values[29] = c_gain;
		super.setLatestValues(values);
		//super.manualMakePermanent();
	}// End HandleEvent
	
	private void handleRejectedJobEvent (Job job, long timestamp )
	{
		// This method only register than the job has been rejected 
		
		String startedMsg = Clock.instance().now().timestamp() +"\tREJECTED\t"+ "site0\t"+
				 job.getName();
		
		log.trace(startedMsg);
		
		Object[] values = this.getLatestValuesPrototype();
		rej ++; // the number of rejected jobs is increased
		
		
		values[0] = ((SWFJob)job).getJobNumber();			// job id
		values[1] = 0;			// c, job completion time, 0 sec the job was never executed
		values[2] = 0;		// AWT, Mean waiting time, sec
		values[3] = 0;		// MTA, mean turaround time, sec 
		values[4] =  tni;  // Number of Interruptions
		values[5] = rej; //Number of jobs so far rejected
		values[6] = 0;
		values[7] = 0;
		values[8] = 0;
		values [9] = 0;
		values[10] = 0;
		values[11] = 0;
		values[12] = 0;
		values[13] = 0;
		values[14] = 0;
		values[15] = 0;
		values[16] = 0; 
		values[17] = 0; //processing time of job with SLA 8
		values[18] = 0; //processing time of job with SLA 9
		values[19] = 0; // processing time of job with SLA 10
		values[20] = 0; //processing time of job with SLA 11
		values[21] = 0; //processing time of job with SLA 12
		values[22] = 0; // processing time of job with SLA 13
		values[23] = 0; // processing time of job with SLA 14
		values[24] = 0; //processing time of job with SLA 15
		values[25] = 0; //processing time of job with SLA 16
		values[26] = 0; // processing time of job with SLA 17
		values[27] = 0; //processing time of job with SLA 18
		values[28] = 0; //processing time of job with SLA 19
		values[29] = c_gain;
		
		super.setLatestValues(values);
		//super.manualMakePermanent();
	}// End handleRejectedJobEvent

}
