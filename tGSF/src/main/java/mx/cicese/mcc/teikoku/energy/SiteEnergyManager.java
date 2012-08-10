package mx.cicese.mcc.teikoku.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mx.cicese.mcc.teikoku.kernel.events.CorePowerOffEvent;
import mx.cicese.mcc.teikoku.kernel.events.CorePowerOnEvent;
import mx.cicese.mcc.teikoku.kernel.events.SitePowerOffEvent;
import mx.cicese.mcc.teikoku.kernel.events.SitePowerOnEvent;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class SiteEnergyManager extends AbstractEnergyManager{
	
	
	final private static Log log = LogFactory.getLog("debugger");

	
	private List <CoreEnergyManager> coreEM;
	private List <BoardEnergyManager> boardEM;
	private List <ChassisEnergyManager> chassisEM;

	//Galleta
	private int coresTurnOffTimes;
	private int boardsTurnOffTimes;
	private int chassisTurnOffTimes;
	
	
	private double coresOnTime;
	private double coresOffTime;
	private double coresWorkingTime;
	private double coresIdleTime;

	/**
	 * Numero de cores por board, 
	 */
	private int nCores;
	
	/**
	 * Contador de cores
	 */
	private int contCores;
	/**
	 * Numero de board por chassis
	 */
	private int nBoards;
	
	/**
	 * Contador de boards
	 */
	private int contBoards;
	
	
	/**
	 * Numero de chassis por sitio
	 */
	private int nChassis;
	
	/**
	 * contador de chassis
	 */
	
	private int contChassis;
	
	//Galleta
	private ResourceBundle opCore;
	
	/**
	 * 
	 */
	private long siteTurnOffDelay;
	
	private long siteTurnOnDelay;

	private long coreTurnOffDelay;
	
	private long coreTurnOnDelay;
	
	/**
	 * Recursos que estan prendidos y pueden calendarizarse
	 */
	private ResourceBundle onCore;
	
	/**
	 * Recursos apagados
	 */
	private ResourceBundle offCore;
	
	private boolean admissible;
	
	/*
	 * Constructores
	 */
	
	/**
	 * Constructor vacio, creara un sitio con:
	 *  1 chassis con
	 *  1 board   con
	 *  1 core 
	 */
	public SiteEnergyManager (Site s)
	{
		super (s);
		this.init();
	}
	
	public void init()
	{	
		offCore= new ResourceBundleSortedSetImpl();
		onCore= new ResourceBundleSortedSetImpl();
		offCore.add(this.getSite().getSiteInformation().getProvidedResources());
		opCore = new ResourceBundleSortedSetImpl();
		coresOnTime = 0;
		coresOffTime = 0;
		coresWorkingTime = 0;
		coresIdleTime = 0;
		
		
		coreEM = new ArrayList ();
		boardEM = new ArrayList ();
		chassisEM = new ArrayList ();

		this.turnOff();

		//Variables Setup
		this.setTurnOnConsumption(eC.getSitePowerTurnOn(getSite()));
		this.setPidle(eC.getSitePowerIdle(getSite()));
		this.siteTurnOffDelay=eC.getSiteTurnOffDelay(getSite());
		this.siteTurnOnDelay=eC.getSiteTurnOnDelay(getSite());
		this.coreTurnOffDelay=eC.getCoreTurnOffDelay(getSite());
		this.coreTurnOnDelay=eC.getCoreTurnOnDelay(getSite());
		
		//Galleta
		this.coresTurnOffTimes = 0;
		this.boardsTurnOffTimes = 0;
		this.chassisTurnOffTimes = 0;
		
		//EM Sets Setup
		this.contCores = 0;
		this.nBoards=eC.getNumberBoards(getSite());
		this.contBoards=0;
		this.nChassis =eC.getNumberChassises(getSite());
		this.contChassis=0;		
		if(offCore.size()%(nChassis*nBoards)!=0)
		{
			System.out.println("ERROR: "+(double)offCore.size()/(nChassis*nBoards)+(" cores en Chassis*Boards"));
			System.exit(0);
		}
		nCores = offCore.size()/(nChassis*nBoards);
		
		for (int i = 0; i<nChassis; i++)
		{
			this.chassisEM.add(new ChassisEnergyManager (this.getSite()));
		}
		
		for (int i = 0; i<nBoards*nChassis; i++)
		{
			this.boardEM.add(new BoardEnergyManager (this.getSite()));
		}
		
		for (int i = 0 ; i<offCore.size(); i++)
		{
			this.coreEM.add(new CoreEnergyManager (this.getSite(), offCore.get(i)));
		}

		this.admissible=false;
	}	

	/**
	 * @return the total coreOnTime 
	 */
	public double getCoresOnTime(){
		return coresOnTime;
	}
	
	/**
	 * @return the total coreOffTime 
	 */
	public double getCoresOffTime(){
		return coresOffTime;
	}

	/**
	 * @return the total coreWorkingTime 
	 */
	public double getCoresWorkingTime(){
		return coresWorkingTime;
	}

	/**
	 * @return the total coreIdleTime 
	 */
	public double getCoresIdleTime(){
		return coresIdleTime;
	}

	/**
	 * @param time the ontime to add
	 */
	public void setCoresOnTime(double time){
		this.coresOnTime += time;
	}
	
	/**
	 * @param time the offtime to add
	 */
	public void setCoresOffTime(double time){
		this.coresOffTime += time;
	}
	/**
	 * @param time the working time to add
	 */
	public void setCoresWorkingTime(double time){
		//alpha
		//System.out.println("Antes:::"+this.coresWorkingTime);
		//alpha>>
		this.coresWorkingTime += time;
		//alpha
		//System.out.println("W= "+time+"\n"+"WORK::::" + this.coresWorkingTime);
	}

	/**
	 * @param time the idle time to add
	 */
	public void setCoresIdleTime(double time){
		this.coresIdleTime += time;
		//System.out.println("IDLEFINAL:"+ time);
	}

	/**
	 * @return the onCore
	 */
	public ResourceBundle getOnCore() {
		return onCore;
	}

	/**
	 * @return the offCore
	 */
	public ResourceBundle getOffCore() {
		return offCore;
	}

	/**
	 * @return the times the cores have turned off
	 */	public int getCoresTurnOffTimes(){
		return coresTurnOffTimes;
	}

	/**
	 * @return the times the boards have turned off
	 */
	public int getBoardsTurnOffTimes(){
		return boardsTurnOffTimes;
	}

	/**
	 * @return the times the chassis have turned off
	 */
	public int getChassisTurnOffTimes(){
		return chassisTurnOffTimes;
	}
	
	//Galleta
	public void setCoresTurnOffTimes(int v){
		this.coresTurnOffTimes += v;
	}
	
	//Galleta
	public void setBoardsTurnOffTimes(int v){
		this.boardsTurnOffTimes += v;
	}
	
	//Galleta
	public void setChassisTurnOffTimes(int v){
		this.chassisTurnOffTimes += v;
	}
	
	/*
	 * Otros metodos
	 */
	
	/**
	 * Solicitud de prender o apagar componentes
	 * @param e
	 */
	public void requestSiteTurnOff(Event e){
		TimeFactory tf=new TimeFactory();
		long time=e.getTimestamp().timestamp()+this.siteTurnOffDelay;
		Instant eventTime = tf.newMoment(time);
		/*
		 * Set Idle
		 */
		
		Kernel.getInstance().dispatch(new SitePowerOffEvent(eventTime,this.getSite()));
	}
	
	public void requestSiteTurnOn(Event e){
		TimeFactory tf=new TimeFactory();
		long time=e.getTimestamp().timestamp()+this.siteTurnOnDelay;
		Instant eventTime = tf.newMoment(time);
		Kernel.getInstance().dispatch(new SitePowerOnEvent(eventTime,this.getSite()));
		
	}
	
	public void requestCoreTurnOff(Event e, Job job){
		TimeFactory tf=new TimeFactory();
		long time=e.getTimestamp().timestamp()+this.coreTurnOffDelay;
		Instant eventTime = tf.newMoment(time);
		/*
		 * Set Idle
		 */

		//Lista de recursos desocupados por el trabajo
		ResourceBundle releasedResources = job.getResources();
		int quantity = releasedResources.size();
		
		for (int i = 0 ; i < quantity;i++) {
			this.coreEM.get(releasedResources.get(i).getOrdinal()-1).setIdle(e.getTimestamp().timestamp());
		}
		Kernel.getInstance().dispatch(new CorePowerOffEvent(eventTime,this.getSite(), job));
	}
	
	public void requestCoreTurnOn(Event e){
		TimeFactory tf=new TimeFactory();
		long time=e.getTimestamp().timestamp()+this.coreTurnOnDelay;
		Instant eventTime = tf.newMoment(time);
		Kernel.getInstance().dispatch(new CorePowerOnEvent(eventTime,this.getSite()));
		
	}
		
	
	
	/**
	 * 
	 * @param e
	 */
	public void deliverSitePowerOnEvent(SitePowerOnEvent e) {
		// TODO Auto-generated method stub
		Long now = new Long (e.getTimestamp().timestamp()); 	
		//alpha
		//System.out.println("ENCENDIENDO SITIO");
		//log.trace( "power on event " + Clock.instance().now().toString());
		//alpha>>
		if(this.isOff()) {
			this.setOffTime(this.getOffTime() + (now-getLastEventTime()));
			this.setnTurnOn(this.getnTurnOn() + 1);
			this.turnOn();
			//System.out.println(this.getSite().getName()+" turns on on "+e.getTimestamp().toString());
		}else {
			this.setOnTime(this.getOnTime() + (now-getLastEventTime()));
		}
		this.setLastEventTime(now);
	} 

	/**
	 * @param e
	 */
	public void deliverSitePowerOffEvent(SitePowerOffEvent e) {
		// TODO Auto-generated method stub
		//alpha
		//System.out.println("SitePowerOFF");
		//log.trace("Site " + e.getSite().getName() + " power off event " + Clock.instance().now().toString());
		//alpha>>
		Long now = new Long (e.getTimestamp().timestamp());
		if(this.getSite().getScheduler().getSchedule().getScheduledJobs().isEmpty() && this.getSite().getReleasedSiteQueue().getQueue().isEmpty())	{
			if(this.getSite().getSiteEnergyManager().isOn()) {
				
				this.setOnTime(this.getOnTime()+ (now-getLastEventTime()));
				this.setnTurnOff(this.getnTurnOff() + 1);
				this.turnOff();
			}
			else {
				this.setOffTime (this.getOffTime()+(now-getLastEventTime()));
			}			
		}
		else {
			this.setOnTime (this.getOnTime() + (now-getLastEventTime()));
		}
		
		this.setLastEventTime(now);
	}
	
	public void addtoOperating(ResourceBundle resources) {
		if (resources != null)
			opCore.add(resources);
	}

	public void removefromOperating(ResourceBundle resources) {
		opCore.remove(resources);
	}
	
	/**
	 * Retorna una lista con los recursos que estan prendidos y ocupados
	 * @return
	 */
	private ResourceBundle getOperatingCores() {
		return opCore;
	}
	
	public int numberOfIdleOnCore() {
		ResourceBundle op = getOperatingCores();
		ResourceBundle desop = this.onCore;
		desop.remove(op);
		return desop.size();
	}
	
	/**
	 * Apaga un core del sitio, 
	 * Si es el último core del board que se apaga, apagará el board.
	 * Si es el último bord del chassis que se apaga, apagará el chassis.
	 * Si es el último chassis del sitio que se apga, mandará el evento para apagar el sitio.
	 * 
	 * @param e
	 */
	public void deliverCorePowerOffEvent (CorePowerOffEvent e, Job job)
	{
		Long now = new Long (e.getTimestamp().timestamp());
		ResourceBundle resources = job.getResources();
		int quantity = resources.size();
		
		for (int i = 0 ; i < quantity;i++) {
			if (!onCore.isEmpty()) {
				int coreNumber = resources.get(i).getOrdinal();
				coreNumber--;
				this.coreEM.get(coreNumber).powerOff(now);
				this.setCoresOnTime(this.coreEM.get(coreNumber).getOnTime());
				this.setCoresWorkingTime(this.coreEM.get(coreNumber).getWorkTime());
				this.setCoresIdleTime(this.getCoresOnTime()-this.getCoresWorkingTime());
				
				this.setCoresTurnOffTimes(1);
				//if (resources.contains(onCore.get(0))) {
					offCore.add(onCore.get(0));
					onCore.remove(0);
					this.contCores--;
				//}
		
				if (contCores==0) {
					//Apaga el board
					this.setBoardsTurnOffTimes(1);
					this.boardPowerOff(e, now);
				
					if(contBoards==0) {
						//Apagar un chassis 
						this.setChassisTurnOffTimes(1);
						this.chassisPowerOff(e, now);
				
						if (contChassis==0) {
							//Mandar evento de apagar site 
							this.requestSiteTurnOff(e);
							this.contCores=0;
							this.contBoards=0;
						}
					}
				}
			}
		}
	} 
	
	/**
	 * Apaga un board que este prendido.
	 * 
	 * @param e
	 * @param now
	 */
	private void boardPowerOff (CorePowerOffEvent e, Long now)
	{
		for(int i=0; i<boardEM.size();i++)
		{
			if(boardEM.get(i).isOn())
			{
				this.boardEM.get(i).powerOff(now);
				this.contCores=this.nCores;	
				this.contBoards--;
				break;
			}
		}
	}
	
	/**
	 * Apaga un chassis que este prendido
	 * @param e
	 * @param now
	 */
	private void chassisPowerOff (CorePowerOffEvent e, Long now)
	{
		for (int i=0; i<chassisEM.size(); i++)
		{
			if(chassisEM.get(i).isOn())
			{
				this.chassisEM.get(i).powerOff(now);
				this.contCores=this.nCores;
				this.contBoards= this.nBoards;			
				this.contChassis--;
				
				break;
			}
		}
	}
	
	
	/**
	 * Prende un core del sitio.
	 * Si es el primer core del board que se prende, prendera el board.
	 * Si es el primer board del chassis que se prender, prendera el chassis.
	 * Si es el primer chassis del sitio que se prender, mandará el evento para prender el sitio.
	 * @param e
	 * 
	 */
	public void setWorkingCore(long time, ResourceBundle r)
	{
		for(CoreEnergyManager cem:coreEM) {
			if(r.contains(cem.getResource())) {
				cem.setWorking(time);

				//Galleta
				//break;
			}
		}
	}
	
	
	public void deliverCorePowerOnEvent (CorePowerOnEvent e) {
		Long now = new Long (e.getTimestamp().timestamp());
		//ResourceBundle op = this.coreOp();			//Lista de recursos que estan prendidos y ocupados
		if (!this.offCore.isEmpty()) {
			coreEM.get(offCore.get(0).getOrdinal()-1).powerOn(now);
			
			//Galleta
			this.setCoresOffTime(coreEM.get(offCore.get(0).getOrdinal()-1).getOffTime());
			//coreEM.get(i).setWorking(now);
			onCore.add(offCore.get(0));
			offCore.remove(0);
			this.contCores ++;
		}
		else {
			for (int i = 0 ; i<onCore.size();i++) {
				if (coreEM.get(onCore.get(i).getOrdinal()-1).isIdle()) {
					coreEM.get(onCore.get(i).getOrdinal()-1).setWorking(now);
					break;
				}
			}
		}
					
		if (contCores >= nCores)
		{
			//Prender un board
			this.boardPowerOn(e, now);		
		}
			
								
		if (contBoards >= nBoards)
		{
			//Prender un chassis
			this.chassisPowerOn(e, now);
			
		}
	}
	
	public void setWorkingCore(ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Prender un board que este apagado
	 * 
	 * @param e
	 * @param now
	 */
	private void boardPowerOn (CorePowerOnEvent e, long now)
	{
		for(int i=0; i<boardEM.size(); i++)
		{
			if (boardEM.get(i).isOff())
			{
//				System.out.println(this.getSite().getName()+" board" +i+" turns On at "+e.getTimestamp().toString());
				this.boardEM.get(i).powerOn(now);
				this.contBoards++;
				this.contCores = 1;
				break;
			}
		}
	}
	
	/**
	 * Prender un chassis que este apagado
	 * 
	 * @param e
	 * @param now
	 */
	private void chassisPowerOn (CorePowerOnEvent e, long now)
	{
		for (int i=0; i<chassisEM.size();i++)
		{
			//Prender un chassis
			if (chassisEM.get(i).isOff())
			{
//				System.out.println(this.getSite().getName()+" chassis" +i+" turns On at "+e.getTimestamp().toString());
				this.chassisEM.get(i).powerOn(now);
				this.contChassis++;
				this.contBoards =1;
				this.contCores = 1;
				break;
			}
		}
	}
	
	public void setAdmissible()
	{
		this.admissible=true;
	}
	
	public boolean isAdmissible()
	{
		return this.admissible;
	}

	public double getEnergyConsumption() {
		double energyConsumption = (this.getnTurnOn()*this.getTurnOnConsumption()) +
									(this.getPidle()*this.getOnTime()/1000);
		for	(CoreEnergyManager core:coreEM)
		{
			energyConsumption+=core.getEnergyConsumption();
		}
		for (BoardEnergyManager board:boardEM)
		{
			energyConsumption+=board.getEnergyConsumption();
		}
		for (ChassisEnergyManager chassis:chassisEM)
		{
			energyConsumption+=chassis.getEnergyConsumption();
		}
		return energyConsumption;
	}
	
	
	
	
}	