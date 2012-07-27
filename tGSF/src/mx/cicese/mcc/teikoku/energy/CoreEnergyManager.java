package mx.cicese.mcc.teikoku.energy;

import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Kernel;

import java.util.ArrayList;
import java.util.List;

public class CoreEnergyManager extends AbstractEnergyManager{

	/**
	 * pcore a power constant for energy measurement
	 */
	
	private double pCore;
	
	private Resource resource;
	
	/*
	 *Constructor 
	 */
	
	public CoreEnergyManager (Site s, Resource r){
		super (s);
		this.pCore = eC.getCorePowerWorking(s);//Watts
		this.setPidle(eC.getCorePowerIdle(s));
		this.setTurnOnConsumption(eC.getCorePowerTurnOn(s));
		this.resource = r;
	}

	/**
	 * @return the pCore
	 */
	public double getpCore() {
		return pCore;
	}
	
	public Resource getResource (){
		return resource;
	}

	/**
	 * @param pCore the pCore to set
	 */
	public void setpCore(double pCore) {
		this.pCore = pCore;
	}
	
	
	/*
	 * Otros métodos
	 */
	
	public void powerOff (Long now)
	{

		this.setOnTime(this.getOnTime()+ (now-getLastEventTime()));
		this.setnTurnOff(this.getnTurnOff() + 1);
		this.turnOff();
		this.setLastEventTime(now);
		
	}
	
	public void powerOn (Long now)
	{
		this.setOffTime(this.getOffTime()+ (now-getLastEventTime()));
		this.setnTurnOn(this.getnTurnOn() + 1);
		this.turnOn();
		this.setLastEventTime(now);
	}
	
	public void setIdle (Long now)
	{
		this.setOnTime(this.getOnTime()+ (now-getLastEventTime()));
		this.setIsIdle(true);
		this.setWorkTime(this.getWorkTime()+ (now-getLastEventTime()));
		this.setLastEventTime(now);
	}

	public void setWorking (Long now)
	{
		this.setIsIdle(false);
		this.setLastEventTime(now);
	}
	
	public double getEnergyConsumption()
	{
		double cons = (this.getnTurnOn()*this.getTurnOnConsumption())+(this.getPidle()*this.getOnTime()/1000)+(this.getWorkTime()/1000*this.pCore);
		//Galleta
		//System.out.println("   -Core consumed "+cons);
		return cons;
	}

	
}
