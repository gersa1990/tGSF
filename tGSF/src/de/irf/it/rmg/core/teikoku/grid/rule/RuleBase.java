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
package de.irf.it.rmg.core.teikoku.grid.rule;

import de.irf.it.rmg.core.teikoku.exceptions.InvalidRuleBaseException;
import de.irf.it.rmg.core.teikoku.job.Job;

/**
 * This is the standardinterface for a single ruleBase, which is used by transferPolicies to make a decision for accepting, decliening or distributing
 * jobs
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public interface RuleBase {

	/**
	 * Most important method, which decides whether a job has been accepted or not with regard to the
	 * inputValues present in the inputVector (e.g. values of single metrics)
	 *
	 * @param inputVector
	 * @param j
	 * @return
	 * @throws InvalidRuleBaseException
	 */
	public boolean decide(Job j)
			throws InvalidRuleBaseException;
}
