package mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions;

import java.io.IOException;
import java.net.URL;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.parser.SWFParser;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;

public abstract class AbstractDistribution implements Distribution{

	public SWFParser swfParser;
	public double [] parameters = new double[10];

	
	public AbstractDistribution(double[] parameters)  {
		this.parameters = parameters;
	}
	
	public AbstractDistribution () 
	{
		
	}
	
	public abstract SLA fetchSLA();
	
	public abstract String getHeader();
	
	public void setSWFParser (URL swfp) throws IOException {
			this.swfParser = new SWFParser(swfp);
	}
	public abstract void setParametersString(String parametersString);
	
	public abstract void initialize(boolean mayOverwrite);
	
	public abstract boolean getMayOverwrite();
}
