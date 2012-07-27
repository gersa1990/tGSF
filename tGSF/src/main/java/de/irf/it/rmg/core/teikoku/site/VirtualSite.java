package de.irf.it.rmg.core.teikoku.site;

import java.util.UUID;

import mx.cicese.dcc.teikoku.broker.GridActivityBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationBroker;
import mx.cicese.dcc.teikoku.scheduler.Scheduler;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.AbstractDistribution;
import mx.uabc.lcc.teikoku.error.ErrorManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.metabrokering.MetaBroker;
import de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker;
import de.irf.it.rmg.core.teikoku.submission.Executor;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;

public class VirtualSite extends ComputeSite {
	
	final private static Log log = LogFactory.getLog(ComputeSite.class);
	
	private ActivityBroker activityBroker;
	private GridActivityBroker gridActivityBroker;
	
	
	private Executor executor;
	
	private String baseName; 
	
	
	private AbstractDistribution slaGenerator;
	
	
	private Scheduler scheduler;
	
	private SiteInformation siteInformation;
	
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
	

	
	public VirtualSite(String name, int Counter) throws InstantiationException {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
