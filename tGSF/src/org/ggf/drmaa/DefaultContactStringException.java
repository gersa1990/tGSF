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
package org.ggf.drmaa;

import java.io.Serializable;

/**
 * Indicates that the DRMAA implementation could not use the default contact
 * string to connect to DRM system.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class DefaultContactStringException extends
		InvalidContactStringException {

	/**
	 * The serial version unique identifier of this class.
	 * 
	 * @see Serializable
	 */
	final private static long serialVersionUID = -9092950703844124120L;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public DefaultContactStringException() {
		super();
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param message
	 *            A short description of the reason for this exception.
	 */
	public DefaultContactStringException(String message) {
		super(message);
	}

}
