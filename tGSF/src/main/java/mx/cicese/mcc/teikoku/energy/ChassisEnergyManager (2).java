package mx.cicese.mcc.teikoku.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class ChassisEnergyManager extends AbstractEnergyManager{

	
	/**
	 * 
	 * @param s
	 */
	public ChassisEnergyManager (Site s)
	{
		super(s);
		this.setPidle(eC.getChassisPowerIdle(s));
		this.setTurnOnConsumption(eC.getChassisPowerTurnOn(s));
	}
	
	

	
	/*
	 * Otros Metodos
	 */
	
	/**
	 * 
	 * @param now
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
	
	
}
