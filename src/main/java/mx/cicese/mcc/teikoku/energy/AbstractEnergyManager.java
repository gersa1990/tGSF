package mx.cicese.mcc.teikoku.energy;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.sim.kuiga.Event;

public class AbstractEnergyManager {

	/**
	 * lastEventTime saves the last event timeStamp for energy consumption calculation
	 */
	
	private long lastEventTime;
	
	 /**
	  *  onTime Tiempo que estuvo prendida 
	  */
	
	private double onTime;
	
	/**
	  *  offTime Tiempo que estuvo apagada 
	  */
	
	private double offTime;
	 
	/**
	  *  idleTime Tiempo que estuvo trabajando 
	  */
	
	private double workTime;
	
	 
	/**
	  *  ???? 
	  */
	
	private boolean powerState;
	
	private boolean isIdle;
	/**
	  *  nTurnOn Numero de veces que estuvo prendida 
	  */
	
	private int nTurnOn;
		

	 /**
	  *  nTurnOff Numero de veces que estuvo apagada 
	  */
	private int nTurnOff;
		

	/**
	 * pidle a power constant for energy measurement 
	 */
	private double pidle;
		
	private Site site;

	private double turnOnConsumption;

	protected EnergyConfiguration eC;
	/*
	 * Constructor
	 */
	
	
	public AbstractEnergyManager ()
	{
		this.powerState = true;
		
		this.offTime = 0;
		this.onTime = 0;
	}
	
	public AbstractEnergyManager (Site s)
	{
		this.site = s;
		this.powerState = false;
		this.turnOnConsumption=0;
		this.pidle=0;
		eC = new EnergyConfiguration();
		
		this.offTime = 0;
		this.onTime = 0;
		
	}
	
	/*
	 * Getters
	 */
	public double getTurnOnConsumption()
	{
		return turnOnConsumption;
	}
	 /**
	  *  @return Site 
	  */
	public Site getSite(){
		return this.site;
	}
	 /**
	  *  @return time of last event 
	  */
	public long getLastEventTime(){
		return this.lastEventTime;
	}


	 /**
	  *  @return times the site was turned on 
	  */
	public double getOnTime(){
		return onTime;
	}
	
	 /**
	  *  @return times the site was turned off 
	  */
	public double getOffTime(){
		return offTime;
	}
		
	/**
	  *  @return times the site was idle 
	  */
	public double getWorkTime(){
		return workTime;
	}
	
	/**
	  *  @return ???? 
	  */
	public boolean getPowerState(){
		return powerState;
	}	
	
	public double getPidle() {
		return pidle;
	}

	/**
	 * @return the nTurnOn
	 */
	public int getnTurnOn() {
		return nTurnOn;
	}

	/**
	 * @return the nTurnOff
	 */
	public int getnTurnOff() {
		return nTurnOff;
	}
	
	public boolean isIdle()
	{
		return this.isIdle;
	}
	/*
	 * Setters
	 */

	public void setIsIdle(boolean iI){
		this.isIdle=iI;
	}
	public void setTurnOnConsumption(double toc)
	{
		turnOnConsumption=toc;
	}
	 /**
	  *  @param Site 
	  */
	public void setSite(Site s){
		this.site=s;
	}
	
	/**
	 * @param nTurnOff the nTurnOff to set
	 */
	public void setnTurnOff(int nTurnOff) {
		this.nTurnOff = nTurnOff;
	}

	/**
	 * @param nTurnOn the nTurnOn to set
	 */
	public void setnTurnOn(int nTurnOn) {
		this.nTurnOn = nTurnOn;
	}

	public void setPidle(double pidle) {
		this.pidle = pidle;
	}

	public void setWorkTime(double d){
		workTime = d;
	}
	public void setLastEventTime(long le){
		this.lastEventTime=le;
	}
	
	
	/**
	 * @param offTime the offTime to set
	 */
	public void setOffTime(double offTime) {
		this.offTime = offTime;
	}

	
	
	/**
	 * @param onTime the onTime to set
	 */
	public void setOnTime(double onTime) {
		this.onTime = onTime;
	}
	
	
	
	/*
	 * Otros Metodos
	 */
	


	/**
	 * Turn On y Off cambian el estado del powerState
	 */
	public void turnOn ()
	{
		this.powerState = true;
	}
	
	public void turnOff ()
	{
		this.powerState = false;
	}
	
	/**
	 * is On, is Off retornan si est� prendido o apagado.
	 * @return
	 */
	
	public boolean isOn ()
	{
		return powerState;
	}
	
	public boolean isOff()
	{
		return !powerState;
	}
		
	public double getEnergyConsumption()
	{
		return nTurnOn*turnOnConsumption+pidle*onTime/1000;
	}
}
