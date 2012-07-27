package mx.uabc.lcc.teikoku.error;
/**
 * @author Aritz Barrondo
 */
public enum EventTypeCode
{
	AVAILABLE(true),
	UNAVAILABLE(false);
	
	private final boolean availability; // disponibilidad
	
	EventTypeCode(boolean avail)
	{
		this.availability=avail;
	}
	
	public boolean isAvailable()
	{
		return availability;
	}
}
