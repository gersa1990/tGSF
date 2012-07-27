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
/**
 * 
 */
package de.irf.it.rmg.core.teikoku.grid.metabrokering;

import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker;
import de.irf.it.rmg.core.teikoku.site.Site;

/**
 * @author Administrator
 *
 */
public interface MetaBroker {

	public void offer(ActivityDelegationSession session) throws OfferingVetoException;

	/**
	 * @return the activityBroker
	 */
	public ActivityBroker getActivityBroker();

	/**
	 * @param activityBroker the activityBroker to set
	 */
	public void setActivityBroker(ActivityBroker activityBroker);

	/**
	 * @return the resourceBroker
	 */
	public ResourceBroker getResourceBroker();

	/**
	 * @param resourceBroker the resourceBroker to set
	 */
	public void setResourceBroker(ResourceBroker resourceBroker);

	/**
	 * @return the site
	 */
	public Site getSite();

	/**
	 * @param site the site to set
	 */
	public void setSite(Site site);
}
