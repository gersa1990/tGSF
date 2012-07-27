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

import java.util.UUID;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.metabrokering.MetaBroker;
import de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker;
import de.irf.it.rmg.core.teikoku.scheduler.Scheduler;
import de.irf.it.rmg.core.teikoku.submission.Executor;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationBroker;
import mx.cicese.dcc.teikoku.broker.GridActivityBroker;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.uabc.lcc.teikoku.error.ErrorManager;

import mx.cicese.mcc.teikoku.energy.SiteEnergyManager;
import mx.cicese.mcc.teikoku.scheduler.SLA.*;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.AbstractDistribution;
import mx.cicese.mcc.teikoku.scheduler.util.ReleasedSiteQueue;
import mx.cicese.mcc.teikoku.scheduler.util.ReplicationControl;

/**
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>,
 *         href="mailto:ahirales@uabc.mx">Adan Carbajal</a>, 
 *         and <a href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public interface Site{

	/**
	 * TODO: not yet commented
	 * 
	 */
	final public static String CONFIGURATION_SECTION = "sites";

	/**
	 * Returns the UUID of this site.
	 * 
	 * @return The UUID of this site.
	 */
	UUID getUUID();

	/**
	 * Returns the name of this site.
	 * 
	 * @return The name of this site.
	 */
	String getName();

	/**
	 * Returns the site information on this site.
	 * 
	 * @return The site information on this site.
	 * 
	 * @see SiteInformation
	 */
	SiteInformation getSiteInformation();
	
	
	/**
	 * Returns the site information on this site.
	 * 
	 * @return The site information on this site.
	 * 
	 * @see SiteInformation
	 */
	void setSiteInformation(SiteInformation siteInformation);

	/**
	 * Returns the scheduler of this site.
	 * 
	 * @return The scheduler of this site.
	 * 
	 * @see Scheduler
	 */
	Scheduler getScheduler();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Executor getExecutor();

	/**
	 * Returns the ResourceBroker of this site.
	 * 
	 * @return The ResourceBroker of this site.
	 * 
	 * @see ResourceBroker
	 */
	ActivityBroker getActivityBroker();
	
	/**
	 * Returns the GridActivityBroker of this site.
	 * 
	 * @return The GridActivityBroker of this site.
	 * 
	 * @see GridActivityBroker
	 */
	GridActivityBroker getGridActivityBroker();
	
	/**
	 * Returns the site information broker associated to this site
	 * 
	 * @return The SiteInformationBroker
	 * 
	 * @see SiteInformationBroker
	 */
	SiteInformationBroker getSiteInformationBroker();
	
	/**
	 * Returns the grid information broker associated to this site
	 * 
	 * @return The GridInformationBroker
	 * 
	 * @see GridInformationBroker
	 */
	GridInformationBroker getGridInformationBroker();
	
	/**
	 * Updates this site information accounting data
	 * 
	 * @return The GridInformationBroker
	 * 
	 */
	void updateSiteInformation();
	
	/**
	 * Returns the local submission component
	 * 
	 * @return The submission component
	 * 
	 * @see LocalSubmissionComponent
	 */
	LocalSubmissionComponent getLocalSubmissionComponent();

	/**
	 * Returns this site local resource broker
	 * 
	 * @return The resource broker
	 * 
	 * @see ResourceBroker
	 */
	ResourceBroker getResourceBroker();

	/**
	 * Returns this site local meta broker
	 * 
	 * @return The meta broker
	 * 
	 * @see MetaBroker
	 */
	MetaBroker getMetaBroker();
		
	/**
	 * Determines if this site contains a active GridActivityBroke
	 * 
	 * @return True if the site has a Grid activity broker
	 * 
	 */
	public boolean hasGridActivityBroker();
	
	
	/**
	 * Sets the scheduler
	 * 
	 * @param the scheduler
	 */
	public void setScheduler(Scheduler s);
	
	
	public ErrorManager getErrorManager();
	
	/**
	 * Sets the SLA Manager
	 */
	
	public void setSLADistribution (AbstractDistribution abstractDistribution);
	
	public AbstractDistribution getSLADistribution();
	
	public SiteEnergyManager getSiteEnergyManager();

	public ReplicationControl getReplicationControl();
	
	public ReleasedSiteQueue getReleasedSiteQueue();
	
	//2522574337
	public long getLongestJob();
	public void setLongestJob(long v);
	public long getWork();
	public void setWork(long v);
	
}
