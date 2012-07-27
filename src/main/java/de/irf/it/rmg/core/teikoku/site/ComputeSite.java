/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2006 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
package de.irf.it.rmg.core.teikoku.site;

import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.broker.ActivityBrokerRole;
import mx.cicese.dcc.teikoku.broker.GridActivityBroker;
import mx.cicese.mcc.teikoku.scheduler.SLA.SLA;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.AbstractDistribution;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.Distribution;
import mx.cicese.mcc.teikoku.scheduler.util.ReleasedSiteQueue;
import mx.uabc.lcc.teikoku.error.ErrorManager;

import mx.cicese.mcc.teikoku.scheduler.util.ReleasedSiteQueue;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.metabrokering.MetaBroker;
import de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.scheduler.Scheduler;
import de.irf.it.rmg.core.teikoku.submission.Executor;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.teikoku.submission.SimulationAdapter;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;



/**
 * <p>
 * Represents a single site, bundling a set of resources and managing their
 * occupation with a scheduler.
 * </p>
 *
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */
public class ComputeSite
        implements Site {

    /**
     * The default log facility for this class, using the <a
     * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
     *
     * @see org.apache.commons.logging.Log
     */
    final private static Log log = LogFactory.getLog(ComputeSite.class);

    /**
     * TODO: not yet commented
     */
    private ActivityBroker activityBroker;
    private GridActivityBroker gridActivityBroker;

    /**
     * TODO: not yet commented
     */
    private Executor executor;

    /**
     * Holds the name (as an arbitrary string) of this site.
     */
    private String name;

    /**
     * Holds the SLA generator which uses some distribution
     */
    private AbstractDistribution slaGenerator;
    
    /**
     * Holds the scheduler for this site which manages the resource's
     * occupation.
     */
    private Scheduler scheduler;

    /**
     * TODO: not yet commented
     */
    private SiteInformation siteInformation;
    
	private ReleasedSiteQueue releasedSiteQueue;

    /**
     * Holds an unique identifier for a site.
     */
    private UUID uuid;

    private LocalSubmissionComponent localSubmissionComponent;

    private ResourceBroker resourceBroker;

    private MetaBroker metaBroker = null;
    
    /**
	 * Holds the information brokers
	 */
	private SiteInformationBroker siteInformationBroker;
	private GridInformationBroker gridInformationBroker;

	private boolean isClone;
	
	/**
	 * The error manager
	 */
	ErrorManager errorManager;
	
	/**
	 * Copy constructor
	 * 
	 * Note: Not all variables have been initiated, only those that are
	 * relevant to the scheduling process.
	 * 
	 * @param another
	 */
	public ComputeSite(ComputeSite another) {
		this.activityBroker = null;
		this.gridActivityBroker = null;
		this.executor = null;
		this.name = another.name;
		this.scheduler = another.scheduler.clone();
		this.siteInformation = ((ComputeSiteInformation)another.siteInformation).clone();
		this.uuid = another.uuid;
		this.localSubmissionComponent = null;
		this.resourceBroker = null;
		this.metaBroker = null;
		this.siteInformationBroker = null;
		this.gridInformationBroker = null;
		this.errorManager = null;
	}
	
	
	/**
	 * Creates a clone copy of this object
	 */
	public ComputeSite clone() {
		return new ComputeSite(this);
	}
	
	
	/**
     * Creates a new instance of this class, using the given parameters.
     *
     * @param name
     * @throws InstantiationException
     */
    public ComputeSite(String name)
            throws InstantiationException {
        /*
           * set UUID (generate random) and name of this site
           */
        this.uuid = UUID.randomUUID();
        this.name = name;

        this.localSubmissionComponent = new LocalSubmissionComponent(this);
        localSubmissionComponent.initialize();
       
        /*
         * initialize site information
         */
        this.siteInformation = new ComputeSiteInformation(this);

        /*
           * load decision maker, scheduler and strategy
           */
        this.loadActivityBroker();
        this.loadResourceBroker();
        this.loadMetaBroker();
        this.loadInformationBroker();

        this.loadSchedulerAndStrategy();
        
        /*
         * Load the SLA generator for this site
         */
        this.loadSLAGenerator();

        /*
           * TODO: just for testing, make this configurable
           */
        this.executor = new SimulationAdapter(this);
        try {
			new ComputeSiteEventHandler(this);
		} catch (InvalidAnnotationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.isClone = false;
    }

    /**
     * Create a new instance of the class for a dynamic site usig the parameters
     */
    public ComputeSite(String name, int counter) 
    		throws InstantiationException 
    {
    	/*
         * set UUID (generate random) and name of this site
         */
    	
      
      this.uuid = UUID.randomUUID();
      
     
      this.name = name;

      this.localSubmissionComponent = new LocalSubmissionComponent(this);
      localSubmissionComponent.initialize();
     
      /*
       * initialize site information
       */
      this.siteInformation = new ComputeSiteInformation(this);

      /*
         * load decision maker, scheduler and strategy
         */
      this.loadActivityBroker();
      this.loadResourceBroker();
      this.loadMetaBroker();
      this.loadInformationBroker();

      this.loadSchedulerAndStrategy();
      
      /*
       * Load the SLA generator for this site
       */
      this.loadSLAGenerator();
      
      
      /* 
       * Once we have loaded all the parameters on the configuration file 
       * under the name of siteX we change the name to be the next dinamic
       * site created.
       */
      
      StringBuilder strB = new StringBuilder(name);
      
      this.name = strB.append(counter).toString();
      
      /*
         * TODO: just for testing, make this configurable
         */
      this.executor = new SimulationAdapter(this);
      try {
			new ComputeSiteEventHandler(this);
		} catch (InvalidAnnotationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.isClone = false;
      
    }
    
    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getExecutor()
      */
    public Executor getExecutor() {
        return this.executor;
    }

    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getName()
      */
    public String getName() {
        return this.name;
    }

    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getScheduler()
      */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getSiteInformation()
      */
    public SiteInformation getSiteInformation() {
        return this.siteInformation;
    }
    
    /**
	 * Sets the site information broker associated to this site
	 * 
	 * @see SiteInformationBroker
	 */
    public void setSiteInformation(SiteInformation information) {
    	this.siteInformation = (ComputeSiteInformation) information;

	}

    // -------------------------------------------------------------------------
    // Implementation/Overrides for de.irf.it.rmg.sim.kuiga.EventIncidenceListener
    // -------------------------------------------------------------------------

    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getUUID()
      */

    public UUID getUUID() {
        return this.uuid;
    }

    // -------------------------------------------------------------------------
    // Implementation/Overrides for
    // de.irf.it.rmg.core.teikoku.kernel.events.TeikokuEventConsumer
    // -------------------------------------------------------------------------

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#toString()
      */

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.getName())
                .toString();
    }

   

    public static void configureMetric(Map<String, LinkedList> parameters, Metrics m) {
//		Check manualPermanent-parameter
        LinkedList<Boolean> tempManual = parameters.get("manual");
        if (tempManual != null) {
            ((AbstractMetric) m).setManualPermanent(tempManual.getFirst());
        }
    }

    // -------------------------------------------------------------------------
    // Implementation/Overrides for java.lang.Object
    // -------------------------------------------------------------------------

  
    public ActivityBroker getActivityBroker() {
        return this.activityBroker;
    }
    
    public GridActivityBroker getGridActivityBroker() {
		return this.gridActivityBroker;
	}

    public Map<String, Object> getPublicInformation() {
        return activityBroker.getPublicInformation();
    }
    
	public void updateSiteInformation() {
		/* TODO: 
		 * 	- 	Not all information is instrumented here.. 
		 * 		instrumentation can be performed in many places. 
		 * */
		((ComputeSiteInformation)siteInformation).runningJobs = 
		scheduler.getSchedule().getScheduledJobs().size();	
	}

    public LocalSubmissionComponent getLocalSubmissionComponent() {
        return this.localSubmissionComponent;
    }

    public ResourceBroker getResourceBroker() {
        return this.resourceBroker;
    }

    public MetaBroker getMetaBroker() {
        return this.metaBroker;
    }
    
    /**
	 * Returns the site information broker associated to this site
	 * 
	 * @return The SiteInformationBroker
	 * 
	 * @see SiteInformationBroker
	 */
    public SiteInformationBroker getSiteInformationBroker() {
		return this.siteInformationBroker;
	}
    
   
	public GridInformationBroker getGridInformationBroker() {
		return this.gridInformationBroker;
	}	
	
	public boolean hasGridActivityBroker() {
		return (this.gridActivityBroker != null)? true : false;
	}
	
	public void setScheduler(Scheduler s) {
		this.scheduler = s;
	}
	
	/**
	 * Returns the error managers
	 */
	public ErrorManager getErrorManager(){
		if(this.errorManager == null) {
			this.errorManager = new ErrorManager();
			this.errorManager.setSite(this);
		}
		return this.errorManager;
	}
	 /**
     * Loads the strategy this scheduler uses based on the configuration.
     *
     * @throws InstantiationException
     */
    private void loadActivityBroker()
            throws InstantiationException {
    	
    	// Loads the first activity broker (node level)
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Site.CONFIGURATION_SECTION);

		String local_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS);
		
		String grid_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_GRIDACTIVITYBROKER_CLASS);
		
		if (local_key == null && grid_key == null) {
			String msg = "ActivityBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + this.getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS 
					+ " and "
					+ Constants.CONFIGURATION_SITES_GRIDACTIVITYBROKER_CLASS
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if

		String local_className = c.getString(local_key);
		if( local_key != null ) {
			ActivityBroker ab = ClassLoaderHelper.loadInterface(local_className, ActivityBroker.class);
			ab.setSite(this);
			ab.setRole(ActivityBrokerRole.COMPUTE_SITE);
			ab.initialize();
			this.activityBroker = ab;
		}
		
		String grid_className = c.getString(grid_key);
		if( grid_key != null ){
			GridActivityBroker	gb = ClassLoaderHelper.loadInterface(grid_className, GridActivityBroker.class);
			gb.setSite(this);
			gb.setRole(ActivityBrokerRole.GRID);
			gb.initialize();
			this.gridActivityBroker = gb;
		}
		
		String msg = "successfully loaded \"" + local_className + " and / or " + grid_className
				+ "\" as activity brokers of site \"" + this.getName() + "\"";
		
		log.debug(msg);
    	
    	
    	/*
    	Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
                Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS);

        if (key == null) {
            String msg = "ResourceBroker entry ("
                    + Site.CONFIGURATION_SECTION + "[" + this.getName()
                    + "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS
                    + ") not found in configuration";
            throw new InstantiationException(msg);
        } // if

        String className = c.getString(key);
        ActivityBroker ab = ClassLoaderHelper.loadInterface(className,
                ActivityBroker.class);

        ab.setSite(this);
        ab.initialize();
        this.activityBroker = ab;
        String msg = "successfully loaded \"" + className
                + "\" as activity broker of site \"" + this.getName() + "\"";
        log.debug(msg); */
    }
    
    
	/**
	 * Loads the strategy this scheduler uses based on the configuration.
	 * 
	 * @throws InstantiationException
	 */
	private void loadInformationBroker()
			throws InstantiationException {
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration().subset(Site.CONFIGURATION_SECTION);

		/* 
		 * Only one information broker must be loaded per site 
		 */
		
		String local_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_INFORMATIONBROKER_CLASS);
		
		String grid_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_GRIDINFORMATIONBROKER_CLASS);
		
		if ((local_key == null && grid_key == null) ||
			(local_key != null && grid_key != null)) {
			String msg = "ActivityBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + this.getName()
					+ "]" + Constants.CONFIGURATION_SITES_INFORMATIONBROKER_CLASS 
					+ " or "
					+ Constants.CONFIGURATION_SITES_GRIDINFORMATIONBROKER_CLASS
					+ ") not found in configuration or two information brokers have been associated to the same site";
			throw new InstantiationException(msg);
		} // if

		String local_className = c.getString(local_key);
		if( local_key != null ) {
			String refreshKey = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
								Constants.CONFIGURATION_SITES_INFORMATIONBROKER_REFRESHRATE);
			long refreshRate = c.getLong(refreshKey);
			SiteInformationBroker sib = ClassLoaderHelper.loadInterface(local_className, SiteInformationBroker.class);
			sib.setSite(this);
			sib.setRefreshRate(refreshRate);
			this.siteInformationBroker = sib;
		}
		
		String grid_className = c.getString(grid_key);
		if( grid_key != null ){
			GridInformationBroker gib = ClassLoaderHelper.loadInterface(grid_className, GridInformationBroker.class);
			gib.setSite(this);
			//gib.bind();
			this.gridInformationBroker = gib;
		}
		
		String msg = "successfully loaded \"" + local_className + " and / or " + grid_className
				+ "\" as site or grid information broker of site \"" + this.getName() + "\"";
		
		log.debug(msg);
	}
	
	
	 /**
     * Loads the strategy this scheduler uses based on the configuration.
     *
     * @throws InstantiationException
     */
    private void loadSchedulerAndStrategy()
            throws InstantiationException {
    	
    	StringTokenizer p = new StringTokenizer(this.getName(),":");
		String siteName = p.nextToken();
		
        Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, siteName,
                Constants.CONFIGURATION_SITES_SCHEDULER_CLASS);

        if (key == null) {
            String msg = "scheduler entry ("
                    + Site.CONFIGURATION_SECTION + "[" + siteName
                    + "]" + Constants.CONFIGURATION_SITES_SCHEDULER_CLASS
                    + ") not found in configuration";
            throw new InstantiationException(msg);
        } // if

        String className = c.getString(key);
        Scheduler s = ClassLoaderHelper.loadInterface(className,
                Scheduler.class);
        s.setSite(this);
        try {
            s.initialize();
        }
        catch (InitializationException e) {
            throw new InstantiationException(e.getMessage());
        }
        this.scheduler = s;

        String msg = "successfully loaded \"" + className
                + "\" as scheduler of site \"" + siteName + "\"";
        log.debug(msg);
    }

    private void loadMetaBroker() {
        Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
                Constants.CONFIGURATION_SITES_METABROKER_CLASS);

        if (key != null) {
            String className = c.getString(key);
            MetaBroker mb;
            try {
                mb = ClassLoaderHelper.loadInterface(className,
                        MetaBroker.class);
                mb.setSite(this);
                mb.setActivityBroker(this.activityBroker);
                mb.setResourceBroker(this.resourceBroker);
                this.metaBroker = mb;
                String msg = "successfully loaded \"" + className
                        + "\" as meta broker of site \"" + this.getName() + "\"";
                log.debug(msg);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // if
    }

    // -------------------------------------------------------------------------
    // Implementation/Overrides for de.irf.it.rmg.core.teikoku.site.Site
    // -------------------------------------------------------------------------

    /**
     * Loads the strategy this scheduler uses based on the configuration.
     *
     * @throws InstantiationException
     */
    private void loadResourceBroker()
            throws InstantiationException {
        Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
                Constants.CONFIGURATION_SITES_RESOURCEBROKER_CLASS);

        if (key == null) {
            /*String msg = "ResourceBroker entry ("
                    + Site.CONFIGURATION_SECTION + "[" + this.getName()
                    + "]" + Constants.CONFIGURATION_SITES_RESOURCEBROKER_CLASS
                    + ") not found in configuration";
            throw new InstantiationException(msg);*/
        	System.out.println("WARNING: No Rersourcebroker set");
        } else{

            String className = c.getString(key);
            ResourceBroker rb = ClassLoaderHelper.loadInterface(className,
                    ResourceBroker.class);
            rb.setSite(this);
            rb.initialize();
            this.resourceBroker = rb;
            String msg = "successfully loaded \"" + className
                    + "\" as activity broker of site \"" + this.getName() + "\"";
            log.debug(msg);
        }
    }

    
    
    private void loadSLAGenerator()
    	throws InstantiationException {
    	Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
    			.subset(SLA.CONFIGURATION_SECTION);
    	
    	String key = ConfigurationHelper.retrieveRelevantKey(c, "generator",
                Constants.SLA_GENERATOR_DISTRIBUTION_CLASS);
    	 if (key == null) {
 
         	System.out.println("WARNING: No SLA generator set");
         }
    	 else
    	 {
    		 String className = c.getString(key);
             Distribution dis = ClassLoaderHelper.loadInterface(className,
                     Distribution.class);
             this.setSLADistribution((AbstractDistribution)dis);
             String msg = "successfully loaded \"" + className
                     + "\" as SLA generator \"" + this.getName() + "\"";
             log.debug(msg);
    	 }
    	 
    	 boolean mayOverwriteBool = false;
    	  String keyOverwrite = ConfigurationHelper.retrieveRelevantKey(c, "generator",
    			  Constants.SLA_GENERATOR_DISTRIBUTION_WRITER_MAYOVERWRITE);
    	 if (keyOverwrite == null) {
    		 System.out.println("WARNING: No SLA overwrite option set, using default: false");
    		 
    	 }
    	 else
    	 {
    		 
    		 String mayOverwrite = c.getString(keyOverwrite);
             mayOverwriteBool = Boolean.parseBoolean(mayOverwrite);
             String msg = "successfully loaded \"" + mayOverwriteBool
                     + "\" as SLA generator behavior \"";
             log.debug(msg);
             
   
    	 }
    	 String keyParameters = ConfigurationHelper.retrieveRelevantKey(c, "generator", Constants.SLA_GENERATOR_DISTRIBUTION_PARAMETERS);
    	 if (keyParameters == null) {
    		 System.out.println("WARNING: No SLA parametres set");
    	 }
    	 else
    	 {
    		 
    		 String parameters[] = c.getStringArray(keyParameters);
    		 StringBuffer result = new StringBuffer();
    		 if (parameters.length > 0) {
    		        result.append(parameters[0]);
    		        for (int i=1; i<parameters.length; i++) {
    		            result.append(parameters[i]);
    		        }
    		    }
    		 this.slaGenerator.setParametersString(result.toString());
    		 String msg = "successfully loaded \"" + result + "\" as parameters for the SLA generator.";
             log.debug(msg);
             
             this.slaGenerator.initialize(mayOverwriteBool);
    	 }
    	 
    	
    }
    
	public ReleasedSiteQueue getReleasedSiteQueue() {
		// TODO Auto-generated method stub
		if(this.releasedSiteQueue == null) {
			this.releasedSiteQueue = new ReleasedSiteQueue(this);
		}
		return releasedSiteQueue;
	}

	@Override
	public void setSLADistribution(AbstractDistribution abstractDistribution) {
		// TODO Auto-generated method stub
		this.slaGenerator = abstractDistribution;
	}


	@Override
	public AbstractDistribution getSLADistribution() {
		// TODO Auto-generated method stub
		return this.slaGenerator;
	}

	public void setLongestJob(long v){
		this.longestJob = v;
	}
	
	public long getLongestJob(){
		return longestJob;
	}


}
	