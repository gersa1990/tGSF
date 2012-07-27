/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import de.irf.it.rmg.core.util.time.Instant;
/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */

public interface SiteInformationData {
	
	/**
	 * TODO: not yet commented
	 *
	 */
	InformationType getType();
	
	/**
	 * TODO: not yet commented
	 *
	 */
	Instant getValidity();
	
}
