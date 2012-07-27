package mx.cicese.mcc.teikoku.energy;

import java.util.Map;

import mx.cicese.mcc.teikoku.kernel.events.SitePowerOnEvent;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;


/*
 * Esta clase hara la sumatoria de los consumos de energia
 * de cada Site una ves terminada la simulacion, ademas de
 * enviar los eventos de prendido y apagado dadas las dis-
 * tintas condiciones
 */

public class GridEnergyManager extends AbstractEnergyManager{
	
	public GridEnergyManager() {
		this.turnOn();
	}

	public int getGridSize() {
		int gridSize = 0;
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0)
				gridSize += s.getSiteInformation().getProvidedResources().size();
		}
			return gridSize;
	}
	
	public void applyEnergyManagementStrategy() {
		// TODO Auto-generated method stub
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			s.getSiteEnergyManager().setAdmissible();
		}
		/*
		 * Mandar Apagar el resto
		 * 
		 * Kernel.getInstance().dispatch(new SitePowerOffEvent (eventTime, s));
		 */
	}

	public double getEnergyConsumption() {
		// TODO Auto-generated method stub
		double energyConsumption=0;
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0)
				energyConsumption+=s.getSiteEnergyManager().getEnergyConsumption();
		}
		//Galleta
		//System.out.println("Grid consumed "+energyConsumption+" Watts");
		
		return energyConsumption;
	}

	//Galleta
	public int[] getTurnOffTimes(){
		int[] sum = {0,0,0,0};
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0) {
				sum[0] += s.getSiteEnergyManager().getCoresTurnOffTimes();
				sum[1] += s.getSiteEnergyManager().getBoardsTurnOffTimes();
				sum[2] += s.getSiteEnergyManager().getChassisTurnOffTimes();
				sum[3] += s.getSiteEnergyManager().getnTurnOff();
			}
		}
		return sum;
	}
	public double[] getTimes(){
		double[] times = new double[4];
		for (int i = 0; i < times.length; i++)
			times[i] = 0;
		
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0) {
				times[0] = s.getSiteEnergyManager().getCoresOnTime();
				times[1] = s.getSiteEnergyManager().getCoresOffTime();
				times[2] = s.getSiteEnergyManager().getCoresIdleTime();
				times[3] = s.getSiteEnergyManager().getCoresWorkingTime();
				times[0] /= s.getSiteInformation().getProvidedResources().size();
				times[1] /= s.getSiteInformation().getProvidedResources().size();
				times[2] /= s.getSiteInformation().getProvidedResources().size();
				times[3] /= s.getSiteInformation().getProvidedResources().size();
			}
		}		
		return times;	
	}
	
	public long getWork(){
		long work = 0;
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0) {
				work += s.getWork();
			}
		}
		return work;
	}
	
	public long getLongest(){
		long longest = -1;
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for(Site s:sites.values()) {
			if (s.getName().compareTo("site0") != 0) {
				if (s.getLongestJob() > longest)
					longest = s.getLongestJob();
			}
		}
		return longest;
	}
	
}
