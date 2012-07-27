package mx.uabc.lcc.teikoku.error;
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;
/**
 * @author Aritz Barrondo
 */
public class ResourceAvailabilityEvent extends Event {
	// Atributos
   Error errorEvent;
   Site site;
    //Constructores
    public ResourceAvailabilityEvent(final Instant time, Error e, Site s)
	{
    	super(time,s.getUUID().toString(),s);
    	this.errorEvent=e;
    	this.site=s;
	}
    
	//Metodos
    
    final public Error getErrorEvent()
    {
    	return this.errorEvent;
    }
    final public void setErrorEvent(final Error e)
    {
    	this.errorEvent=e;
    }
    @Override
    protected int getOrdinal()
    {
    	return EventType.AVAILABILITY.ordinal();
    }
}
