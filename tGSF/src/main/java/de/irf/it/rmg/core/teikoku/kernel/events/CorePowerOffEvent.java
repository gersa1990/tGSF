package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.sim.kuiga.Event;

public class CorePowerOffEvent extends Event {
	// Atributos
	   Site site;
	   Job job;
	   
	    //Constructores
	    public CorePowerOffEvent(final Instant time, Site s, Job j)
		{
	    	super(time,s.getUUID().toString(),s);
	    	this.site=s;
	    	this.job = j;
		}
	    
		//Metodos
	    
	    public Job getJob() {
	    	return this.job;
	    }
	    
	    @Override
	    protected int getOrdinal()
	    {
	    	return EventType.POWEROFF.ordinal();
	    }

}
