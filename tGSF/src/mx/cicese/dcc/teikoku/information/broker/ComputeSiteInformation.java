/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.MetricsException;
import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.metrics.MetricsVault;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.Instant;

import mx.cicese.mcc.teikoku.metrics.Energy_Grid;

/**
 * <p>
 * Represents a single site, bundling a set of resources and managing their
 * occupation with a scheduler.
 * </p>
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         href="mailto:ahirales@uabc.mx">Adan Hirales</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class ComputeSiteInformation implements SiteInformation {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(ComputeSiteInformation.class);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Set<Metrics> providedMetrics;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private ResourceBundle providedResources;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private int numberOfConferedResources = 0;
	
	//Macadamia
	private double coreEnergyConsumption;
	private double siteEnergyEfficiency;
	
    /**
     * TODO: not yet commented
     */
    private ResourceBundle lentResources;

    /**
     * TODO: not yet commented
     */
    private Map<Integer, Instant> mapLentResToTime = new HashMap<Integer, Instant>();

    /**
	 * Time when the compute site will become unavailable
	 */
	public Instant downtimeStart;

	/**
	 * Time when the compute site will become available
	 */
	public Instant downtimeEnd;

	/**
	 * Total number of jobs in the compute site
	 * 	The sum of runningJobs, waitingJobs, suspendedJobs, 
	 *	and preLRMSWaitingJobs
	 */
	public int totalJobs;

	/**
	 * Total of running jobs
	 * 	It account for jobs in jobStartedEvent state
	 */
	public int runningJobs;
		
	/**
	 * Total of running jobs
	 * 	It account for jobs in jobStartedEvent state
	 */
	public int localRunningJobs;
		
	/**
	 * Total of waiting jobs
	 * 	It account for jobs in jobQueuedEvent state
	 */
	public int waitingJobs;

	/**
	 * Total of localy submitted  waiting jobs
	 * 	It account for jobs in jobQueuedEvent state
	 */
	public int localWaitingJobs;
		
		
	/**
	 * Total of suspended jobs
	 * 	It account for jobs in jobSuspendedEvent state
	 */
	public int suspendedJobs;

	/**
	 * Total of released jobs that have not started execution
	 * 	It account for jobs in jobReleasedEvent state
	 */
	public int preLRMSWaitingJobs;

	/**
	 * Total of released jobs that have not started execution
	 * 	It account for jobs in jobReleasedEvent state
	 */
	public int localPreLRMSWaitingJobs;
		
	/**
	 * Accounts for the total number of jobs that not 
	 * offered by the local submission component 
	 */
	public int foreignJobs;
		
	/**
	 * The site this ComputeSiteInformation instance is associated with
	 */
	Site site;

	// EnMod
	//Macadamia
	private double speed;
	
	/**
	 * Used for creating the dummy site and schedule 
	 * 
	 * @param site
	 * @throws InstantiationException
	 */
	public ComputeSiteInformation(Site site, String dummy) throws InstantiationException {
		this.loadResources(site);
		this.site = site;
	}
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 */
	public ComputeSiteInformation(Site site) throws InstantiationException {
		this.loadResources(site);
		this.loadMetrics(site);
		this.downtimeStart = null;
		this.downtimeEnd = null;
		this.totalJobs = 0;
		this.runningJobs = 0;
		this.localRunningJobs = 0;
		this.waitingJobs = 0;
		this.localWaitingJobs = 0;
		this.suspendedJobs = 0;
		this.preLRMSWaitingJobs = 0;
		this.localPreLRMSWaitingJobs = 0;
		this.foreignJobs = 0;
		this.site = site;
		
		//Macadamia
		this.coreEnergyConsumption = 0;
		this.siteEnergyEfficiency = 0;
		
		// EnMod
		this.setSpeed();
	}
	
	
	/**
	 * Copy constructor
	 */
	public ComputeSiteInformation(ComputeSiteInformation another) {
	
		// Sets new values
		this.downtimeStart = another.downtimeStart;
		this.downtimeEnd = another.downtimeEnd;
		this.totalJobs = another.totalJobs;
		this.runningJobs = another.runningJobs;
		this.localRunningJobs = another.localRunningJobs;
		this.waitingJobs = another.waitingJobs;
		this.localWaitingJobs = another.localWaitingJobs;
		this.suspendedJobs = another.suspendedJobs;
		this.preLRMSWaitingJobs = another.preLRMSWaitingJobs;
		this.localPreLRMSWaitingJobs = another.localPreLRMSWaitingJobs;
		this.foreignJobs = another.foreignJobs;
		// EnMod
		this.speed=another.speed;
		// Clone the resources
		this.providedResources = new ResourceBundleSortedSetImpl();
		for(Iterator<Resource> it=another.providedResources.iterator(); it.hasNext();)
			this.providedResources.add(it.next().clone());
			

		// Clone the number of conferred resources
		this.numberOfConferedResources = another.getNumberOfConferedResources();
		
		// Clone the length of resources
		this.lentResources = new ResourceBundleSortedSetImpl();
		for(Iterator<Resource> it=another.lentResources.iterator(); it.hasNext();)
			this.lentResources.add(it.next().clone());
		
		for(Integer k : another.mapLentResToTime.keySet()) 
			this.mapLentResToTime.put(k, another.mapLentResToTime.get(k));

		/*
		 *  Perhaps it is necessary to clone this.providedMetrics
		 */
		
	}
	
	
	/**
	 * Creates a clone copy of this object
	 * 
	 * @return a copy of this object
	 */
	public ComputeSiteInformation clone() {
		return new ComputeSiteInformation(this);
	}
	
	
	
	public Map<Integer, Instant> getMapLentResToTime() {
		return this.mapLentResToTime;
	}

	
	
	 /**
     * TODO: not yet commented
     */
    private void loadMetrics(Site site) {
    	StringTokenizer p = new StringTokenizer(site.getName(),":");
		String siteName = p.nextToken();
    	
    	/*
            * determine configuration keys and load metrics identifiers
            */
        Configuration c = RuntimeEnvironment.getInstance()
                .getConfiguration().subset(
                        Site.CONFIGURATION_SECTION);
        String[] identifiers = c
                .getStringArray(siteName
                        + "."
                        + Constants.CONFIGURATION_SITES_REGISTEREDMETRIC_IDENTIFIER);

        this.providedMetrics = new HashSet<Metrics>();
        /*
            * load and register metrics at event manager
            */
        for (String identifier : identifiers) {
            try {
                //Get Configuration-part for Metric
                Configuration csub = RuntimeEnvironment.getInstance()
                        .getConfiguration().subset(
                                Metrics.CONFIGURATION_SECTION + "." + identifier);

                Metrics m = MetricsVault.getInstance().registerMetricAt(site,
                        identifier);
                this.providedMetrics.add(m);
            } // try
            catch (MetricsException e) {
                String msg = e.getMessage();
                log.warn(msg, e);
            } // catch
        } // for
    } // loadMetrics


	/**
	 * TODO: not yet commented
	 */
	private void loadResources(Site site) throws InstantiationException {
		StringTokenizer p = new StringTokenizer(site.getName(),":");
		String siteName = p.nextToken();
		
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + siteName);

		String key = Constants.CONFIGURATION_SITES_LISTOFPROVIDEDRESOURCES;
		if (c.containsKey(key)) {
			try {
				String[] listOfProvidedResources = c.getStringArray(key);
				this.providedResources = new ResourceBundleSortedSetImpl(Resource
						.newGroupOfResources(listOfProvidedResources));
			} // catch
			catch (ConversionException e) {
				String msg = "creation of named resources failed for site \""
					+ siteName + "\", trying anonymous creation";
				log.warn(msg, e);
			} // catch
		} // if

		/*
		 * load anonymous (numbered) resources
		 */
		int numberOfProvidedResources = 0;
		numberOfProvidedResources = c.getInt(Constants.CONFIGURATION_SITES_NUMBEROFPROVIDEDRESOURCES);
		if (numberOfProvidedResources < 1) {
			String msg = "could not instantiate site \""
				+ siteName
				+ "\": specified "
				+ Constants.CONFIGURATION_SITES_NUMBEROFPROVIDEDRESOURCES
				+ " must be larger than 0";
			log.error(msg);
			throw new InstantiationException(msg);
		} // if
		else {
			this.providedResources = new ResourceBundleSortedSetImpl(Resource
					.newGroupOfAnonymousResources(numberOfProvidedResources));
		} // End if
		Resource[] empty = new Resource[0];
        this.lentResources = new ResourceBundleSortedSetImpl(empty);

        log.debug("resource creation for site \"" + site.getName()
				+ "\" successful with " + this.providedResources.size()	+ " resources");
	} // End loadResources


	public Set<Metrics> getProvidedMetrics() {
		return this.providedMetrics;
	} // End getProvidedMetrics

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getProvidedResources()
	 */
	public ResourceBundle getProvidedResources() {
		return this.providedResources;
	}

	//		@Override
    public ResourceBundle getLentResources() {
        return this.lentResources;

    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getProvidedResources()
	 */
	public int getNumberOfAvailableResources() {
		//return site.getSiteInformation().getProvidedResources().size();
		return this.providedResources.size();
	}
	
    public void addConferedResources(int amount) {
        numberOfConferedResources += amount;
    }


    public void removeConferedResources(int amount) {
        numberOfConferedResources -= amount;
    }

    //		@Override
    public Instant getTimeOfLoan(Resource r) {
        return mapLentResToTime.get(r.getOrdinal());
    }

    //		@Override
    public void addLentResources(List<Resource> rl) {
        for (Resource r : rl) {
            this.lentResources.add(r);
        }
        this.mapLentResToTime.put(rl.get(0).getOrdinal(), Clock.instance().now());
    }

		
	public AverageHelper getAvgHelper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean getServingState() {
		boolean servingState = true;
		Instant currentTime = Clock.instance().now();
	
		// No down time has been scheduled.
		if( this.downtimeStart == null)
			return (servingState=true);
		// The site has been scheduled for downtime
		if (currentTime.after(this.downtimeStart) && currentTime.before(this.downtimeEnd))
			servingState = false;
		return servingState;
	} // End getServingState
	
	
	/**
	 * Gets the number of conferred resources 
	
	 * @return the number of conferred resources
	 */
	public int getNumberOfConferedResources() {
		return this.numberOfConferedResources;
	}

	//Macadamia
	public double getCoreEnergyConsumption() {
		this.setCoreEnergyConsumption();
		return this.coreEnergyConsumption;
	}
	
	//Macadamia
	public void setCoreEnergyConsumption()	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + this.site.getName());
		try
		{
			this.coreEnergyConsumption = c.getDouble(Constants.CONFIGURATION_SITES_ENERGY_POWER_SITE);
		}
		catch (Exception e)
		{
			this.coreEnergyConsumption=1;
		}
	}
	
	
	//Macadamia
	public double getSiteEnergyEfficiency() {
		
		//Macadamia
		this.setSiteEnergyEfficiency();
		return this.siteEnergyEfficiency;
	}
	
	//Macadamia
	public void setSiteEnergyEfficiency() {
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + this.site.getName());
		try
		{
			this.siteEnergyEfficiency = c.getDouble(Constants.CONFIGURATION_SITES_ENERGY_EFFICIENCY_SITE);
		}
		catch (Exception e)
		{
			this.coreEnergyConsumption=1;
		}
	}
	
	/*
	 * Sets the component site
	 * 
	 * @param site 	the site
	 */
	
	// Modificacion
	public void setSite(Site site){
		this.site = site;
	}

	//Macadamia
	// EnMod
	public void setSpeed()
	{
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + this.site.getName());
		try
		{
			this.speed = c.getDouble(Constants.CONFIGURATION_SITES_SPEED);
		}
		catch (Exception e)
		{
			this.speed=1;
		}
	}
	
	//Macadamia
	public double getSpeed()
	{
		return this.speed;
	}
}// End ComputeSiteInformation




