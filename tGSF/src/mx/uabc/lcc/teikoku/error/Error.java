package mx.uabc.lcc.teikoku.error;

import java.util.UUID;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.Event;
public class Error{
    // Atributos
    UUID errorID;
    int siteID, eventReasonCode,type;
    double startTime, endTime;
    EventTypeCode eventType;
    EventReason eventReason; 
    
    TimeFactory tf;
	Event ee;
    //Constructores
    public Error(){
    
    	/* Vacio */
    	// System.out.println("Error Creado");
    	
    }
    public Error(int t, long time, String siteName)
    {
    	type=t;
    	tf=new TimeFactory();
    	ee=null;
		Instant tiempo = tf.newMoment(time);
		switch (type)
		{
			case 0:
				ResourceUnavailabilityEvent e0 =new ResourceUnavailabilityEvent(tiempo, this, RuntimeEnvironment.getInstance().getSiteContainer().getSite(siteName));
				ee=e0;
				break;
			case 1:
				ResourceAvailabilityEvent e1 =new ResourceAvailabilityEvent(tiempo, this, RuntimeEnvironment.getInstance().getSiteContainer().getSite(siteName));
				ee=e1;
				break;
			case 2:
				SiteUnavailabilityEvent e2 =new SiteUnavailabilityEvent(tiempo, this, RuntimeEnvironment.getInstance().getSiteContainer().getSite(siteName));
				ee=e2;
				break;
			case 3:
				SiteAvailabilityEvent e3 =new SiteAvailabilityEvent(tiempo, this, RuntimeEnvironment.getInstance().getSiteContainer().getSite(siteName));
				ee=e3;
				break;
		}
		ee.getTags().put(RuntimeEnvironment.getInstance().getSiteContainer().getSite(siteName).getUUID().toString(), RuntimeEnvironment.getInstance().getSiteContainer().getSite("site2"));
    }
    public Error(double st,double et, int id, 
                EventTypeCode etc, EventReason er)
   {
        eventReason = er;
        eventType = etc;
        siteID = id;
        endTime=et;
        startTime=st;
        errorID=Identificador.getInstance().getUUID();
        
    }
    //Metodos Getters: 
    public int getType(){
    	return type;
    }
    public Event getEvent()
    {
    	return ee;
    }
    public UUID getErrorID(){
            return errorID;
        }       
        public double getStartTime(){
            return startTime;
        }   
        public double getEndTime(){
            return endTime;
        }   
        public int getSiteID(){
            return siteID;
        }   
        public EventTypeCode getEventType(){
            return eventType;
        }       
        public EventReason getEventReason(){
            return eventReason;
        }   
    //Metodos Putters:
        public void putErrorID (){
            errorID=Identificador.getInstance().getRandomUUID();
        }       
        public void putStartTime(double st){
            startTime=st;
        }   
        public void putEndTime(double et){
            endTime=et;
        }   
        public void putSiteID(int id){
            siteID = id;
        }       
        public void putEventTypeCode(String et){
            eventType = EventTypeCode.valueOf(et);
        }   
        public void putEventType(int et){
            for (EventTypeCode event : EventTypeCode.values())
            {
                //
                if (et==0 && !event.isAvailable())
                {
                    eventType=event;
                    break;
                }
                if (et==1 && event.isAvailable())
                {
                    eventType=event;
                    break;
                }
            }
        }   
        public void putEventReason(String er){
            eventReason = EventReason.valueOf(er);
            eventReasonCode=eventReason.getCode();
        }
    }
