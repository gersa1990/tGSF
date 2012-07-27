/**
 * 
 */
package mx.cicese.dcc.teikoku.information.broker;

import java.util.UUID;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.util.time.Instant;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */

public abstract class Entity {
	/**
	 * This entity unique identifier of this entity
	 * 
	 */
	private UUID id;
	
	/**
	 * Name of the entity
	 * 
	 */
	private String name;
	
	/**
	 * Time when the entity was created
	 * 
	 */
	private Instant creationTime;
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * 
	 */
	public Entity() {
		this.creationTime = Clock.instance().now();
		this.id = UUID.randomUUID();
	}
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * 
	 */
	public Entity(String name) {
		this();
		this.name  = name;
	}
	
	
	/**
	 * Getter method for this entity UUID
	 * 
	 * @return Returns the current contents of "this.is".
	 */
	public UUID getUUID() {
		return this.id;
	}
	
	
	/**
	 * Getter method for this entity name
	 * 
	 * @return Returns the current contents of "this.name".
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Getter method for this entity creation time
	 * 
	 * @return Returns the current contents of "this.creationTime".
	 */
	public Instant getCreationTime() {
		return this.creationTime;
	}
	
}
