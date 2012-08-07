package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class SitePowerOffEvent extends Event {
	// Atributos
	   Site site;
	    //Constructores
	    public SitePowerOffEvent(final Instant time, Site s)
		{
	    	super(time,s.getUUID().toString(),s);
	    	this.site=s;
		}
	    
		//Metodos
	    
	    @Override
	    protected int getOrdinal()
	    {
	    	return EventType.POWEROFF.ordinal();
	    }
	    
	    public Site getSite()
	    {
	    	return site;
	    }

}
