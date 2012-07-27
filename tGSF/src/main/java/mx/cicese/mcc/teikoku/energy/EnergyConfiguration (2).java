package mx.cicese.mcc.teikoku.energy;
//EnMod
import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.site.Site;

public class EnergyConfiguration {

	/*
	 * Site Energy Configuration
	 */
	
	public double getSitePowerIdle(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_IDLE_SITE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public double getSitePowerTurnOn(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_TURN_ON_SITE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public long getSiteTurnOnDelay(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_TURN_ON_DELAY_SITE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public long getSiteTurnOffDelay(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_TURN_OFF_DELAY_SITE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	/*
	 * Chassis Energy Configuration
	 */
	
	public int getNumberChassises(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_NUMBER_CHASSISES);
		}
		catch (Exception e)
		{
			return 1;
		}
	}

	public double getChassisPowerTurnOn(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_TURN_ON_CHASSIS);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public double getChassisPowerIdle(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_IDLE_CHASSIS);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	/*
	 * Boards Energy Configuration
	 */
	
	public int getNumberBoards(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_NUMBER_BOARDS);
		}
		catch (Exception e)
		{
			return 1;
		}
	}

	public double getBoardPowerTurnOn(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_TURN_ON_BOARD);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public double getBoardPowerIdle(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_IDLE_BOARD);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	/*
	 * Core Energy Configuration
	 */
	
	public double getCorePowerIdle(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_IDLE_CORE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
	public double getCorePowerWorking(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_WORKING_CORE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public double getCorePowerTurnOn(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_POWER_TURN_ON_CORE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public long getCoreTurnOnDelay(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_TURN_ON_DELAY_CORE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	public long getCoreTurnOffDelay(Site site)
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + site.getName());
		try
		{
			return c.getInt(Constants.CONFIGURATION_SITES_ENERGY_TURN_OFF_DELAY_CORE);
		}
		catch (Exception e)
		{
			return 0;
		}
	}
	
}
