package mx.cicese.mcc.teikoku.scheduler.SLA.parser;

import java.io.IOException;

public class SLAException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4864511959387144538L;
	/**
	 * 
	 */
	
	public SLAException() {
		super();
	}
	
	
	public SLAException(String message) {
		super(message);
	}
	
	public SLAException (Throwable cause)
	{
		super(cause);
	}
	
	
	
	public SLAException(String msg, IOException e) {
		super(msg,e);	
	}

	/**
	 * 
	 */
	

}
