/**
 * 
 */
package mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions;

import java.io.IOException;
import java.net.URL;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;

/**
 * @author Anuar
 *
 */
public interface Distribution {

	//Get a new SLA 
	public  SLA fetchSLA();
	
	//Give the header file that belongs to this kind of distribution
	public  String getHeader();
	
	//Establish the SWFParser that the class will use to create the SLAs
	public void setSWFParser (URL swfp) throws IOException;
	
	//Set the parameters string needed to initialize the distribution 
	public void setParametersString(String parametersString);
	
	//starts the distribution SLA generator
	public void initialize(boolean mayOverwrite);
	
	//get mayOverwrite parameter
	public boolean getMayOverwrite();
}
