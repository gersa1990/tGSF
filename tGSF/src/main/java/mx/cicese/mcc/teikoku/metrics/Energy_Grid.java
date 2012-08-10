package mx.cicese.mcc.teikoku.metrics;

import de.irf.it.rmg.core.teikoku.exceptions.SubmissionException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.CorePowerOffEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;
import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import mx.cicese.mcc.teikoku.energy.GridEnergyManager;
import mx.cicese.mcc.teikoku.kernel.events.SitePowerOffEvent;
import de.irf.it.rmg.core.util.math.AverageHelper;

public class Energy_Grid extends AbstractMetric {
	
	/**
	 * The number of scheduled composite jobs
	 */
	private double n;

	
	/**
	 * Bounded slowdown
	 */
	private double sbsd;

	/**
	 * Utilization
	 */
	private double util;
	
	/**
	 * Energy efficiency
	 */
	private double efficiency;
	
	/**
	 * Sum of weighted completion times
	 */
	private double swct;
	
	/**
	 * Machine size
	 */
	private long m;

	/**
	 * Average response time
	 */
	private AverageHelper ah;
	private double art;
	
	/**
	 * Initialization flag
	 */
	private boolean initialized;
	
	/**
	 * Sum of waiting times
	 */
	private double swt;
		
	/**
	 *  Grid Energy Consumption
	 */
	private GridEnergyManager gridEnergyManager;
	private double e;
	
	/**
	 * Number of cores/boards/chassis/sites turnoff/turnon times.
	 */
	private int[] turns = {0, 0, 0, 0};
	
	/**
	 *  C max
	 */
	private double cMax;
	
	/**
	 * competitive factor
	 */
	private double rho;
	private double sja;
	private double maxRP;
		
	/**
	 * C optimal
	 */
	private double cOpt;

	/**
	 * Total core times
	 * 0: On time
	 * 1: Off time
	 * 2: Idle time
	 * 3: Work time
	 */
	private double[] times = {0,0,0,0};
	
	/**
	 * Total time (sum of on+off)
	 */
	private double timeSum;
	
	//alpha
	long worktime = 0;
	
	long initialtime;
	
	long idletime;
	//alpha>>
	
	/**
	 * Class constructor
	 */
	public Energy_Grid() {
		super();
		this.initialized = false;
		this.n = 0;
		this.swt = 0;
		
		//2522574337
		this.efficiency = 0;
		
		gridEnergyManager = new GridEnergyManager();
		this.e = 0;
		this.rho = 0;
		this.sja = 0;
		this.maxRP = -1;
		this.cOpt = 0;
		this.sbsd = 0;
		this.art = 0;
		this.swct = 0;
		this.ah = new AverageHelper();
		timeSum = 0;
		//SitePowerOffEvent event = new SitePowerOffEvent(this.getSite().getSiteEnergyManager().);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[12];
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
		values[2] = "competitive_factor ";
		values[3] = "energy ";
		values[4] = "offtime ";
		values[5] = "idletime ";
		values[6] = "worktime ";
		values[7] = "mean_bounded_slowdown ";
		values[8] = "sum_weighted_completion_time ";
		values[9] = "utilization ";
		values[10] = "response_time ";
		values[11] = "efficiency ";
		
		return values;
	} // End getHeader

	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		this.handleCompletedEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
		//alpha
		System.out.println("RUNTIME:::"+ job.getRuntimeInformation().getRunningTime());
		
		
		
	}// End deliverCompletedEvent
	
	@AcceptedEventType(value=SitePowerOffEvent.class)
	public void deliverSitePowerOffEvent(SitePowerOffEvent e){
		this.handleSitePowerOffEvent(DateHelper.convertToSeconds(TimeHelper.toLongValue(e.getTimestamp())));
	}// End deliverCompletedEvent
	
	//alpha
	/*
	@AcceptedEventType(value=JobStartedEvent.class)
	public void deliverJobStartedEvent(JobStartedEvent e){
		Job job=e.getStartedJob();
		this.handleJobStartedEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(e.getTimestamp())));
	}
	
	private void handleJobStartedEvent(Job job,long timestamp) {
		// TODO Auto-generated method stub
		
		
		//System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		
	}

	@AcceptedEventType(value=JobInterruptedEvent.class)
	public void deliverJobInterruptedEvent(JobInterruptedEvent e){
		Job job=e.getInterruptedJob();
		this.handleJobInterruptedEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(e.getTimestamp())));
	}
	
	private void handleJobInterruptedEvent(Job job,long timestamp) {
		// TODO Auto-generated method stub
		
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
	}
	*/
	//alpha>>
	
	private void assignNewValues()
	{
		Object[] values = this.getLatestValuesPrototype();
		values[0] = cMax;
		values[1] = (1/n) * swt; 
		values[2] = rho;
		values[3] = e;
		values[4] = times[1];
		values[5] = idletime;
		values[6] = worktime;
		values[7] = (1/n)*sbsd;
		values[8] = (1/n)*swct;
		values[9] = util;
		values[10] = art;
		values[11] = efficiency;
		
		super.setLatestValues(values);
		
		//Con esto nomás escribe para el site0, si los demás los estoy borrando...
		if (super.getSite().getName().equals("site0"))
				super.manualMakePermanent();
	}
	
	private void handleCompletedEvent(Job job, long timestamp) {
		
		this.n++;
		swt += ((SWFJob) job).getWaitTime();
		e = this.gridEnergyManager.getEnergyConsumption();
		
		//2522574337
		long work = this.gridEnergyManager.getWork();
		long longest = this.gridEnergyManager.getLongest();
		efficiency = work / e;
		e /= timestamp;
		
		//Galleta
		turns = this.gridEnergyManager.getTurnOffTimes();
		times = this.gridEnergyManager.getTimes();
		//double ext = this.gridEnergyManager.getSite().getSiteEnergyManager().getCoresIdleTime();
		timeSum = times[1] + times[2] + times[3];
		for (int i = 0; i < times.length; i++)
			if (times[i] != 0)
				times[i] *= (100/timeSum);

		cMax = timestamp;

		if(!initialized) 
			m = super.getSite().getSiteInformation().getNumberOfAvailableResources();
		
		double p = TimeHelper.toSeconds(job.getDuration().distance());
		double c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double size = ((SWFJob) job).getRequestedNumberOfProcessors();
		
		sbsd += ((c-r) / Math.max(10, p));  		// Sum bounded slowdown
		
		art = this.calculateResponseTime(job);
		swct += c * (p * size);						// SWCT, Sum of weighted completion time, sec
		
		
		//Esto debe ser para TODOS los trabajos.
		// Estimation of the competitive factor
		
		cOpt = Math.max(longest, (work/m));
		this.rho = c/cOpt;
		//alpha , sumamos el runtime de este trabajo completado
		operaciones(job,this.cMax);

		//alpha>>
		this.assignNewValues();
	} // End handleEvent

	private void handleSitePowerOffEvent(long timestamp) {
		// TODO Auto-generated method stub
		e = this.gridEnergyManager.getEnergyConsumption();
		e /= timestamp;
		//Galleta
		turns = this.gridEnergyManager.getTurnOffTimes();
		times = this.gridEnergyManager.getTimes();
		
		timeSum = times[1] + times[2] + times[3];
		for (int i = 0; i < times.length; i++)
			if (times[i] != 0)
				times[i] *= (100/timeSum);		
	}
	
	

	@Override
	protected double calculateResponseTime(Job j) {
		return ah.calculateAverage(super.calculateResponseTime(j));
	}
	
	protected void operaciones(Job job,double cmax)
	{
	
		worktime += job.getRuntimeInformation().getRunningTime();
		
		if(job.getName().compareTo("1")==0)
		{
			initialtime = job.getReleaseTime().timestamp()/1000;	
		}
		
		idletime = ((long)cmax - initialtime) - worktime;
		//System.out.println("idle::"+idletime);
		
	}
	
}
