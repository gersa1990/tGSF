// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
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
package mx.cicese.dcc.teikoku.information.broker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.Instant;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface SiteInformation {

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	ResourceBundle getProvidedResources();
	
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	int getNumberOfAvailableResources();
	
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	Set<Metrics> getProvidedMetrics();
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	public boolean getServingState();
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	ResourceBundle getLentResources();
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	void addLentResources(List<Resource> rl);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	void addConferedResources(int amount);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	void removeConferedResources(int amount);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	Instant getTimeOfLoan(Resource r);
	
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	//public void copy(SiteInformation current);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	public Map<Integer, Instant> getMapLentResToTime();
}
