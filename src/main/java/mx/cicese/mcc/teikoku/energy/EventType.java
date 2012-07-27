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
package mx.cicese.mcc.teikoku.energy;




/**
 * TODO: not yet commented
 * 
 * @author <a href="alexander.foelling@udo.edu">Alexander F�lling</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public enum EventType {

	/**
	 * TODO: not yet commented
	 * 
	 */
	POWERON(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	UNAVAILABILITY(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	AVAILABILITY(), 
	
	JOB_CANCELED(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	RETURN_OF_LENT_RESOURCES(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_ABORTED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	RESERVATION_ENDED(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	ALLOC_QUEUED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_COMPLETED(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_COMPLETED_ON_FOREIGN_SITE(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	WORKFLOW_COMPLETED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_STARTED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_QUEUED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_RELEASED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	JOB_QUEUED_ON_FOREIGN_SITE(),
	/**
	 * TODO: not yet commented
	 * 
	 */
	
	POWEROFF(),

	JOB_REPLICATED();
}
