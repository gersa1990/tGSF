package mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.parser.SWFParser;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.Job;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.WorkloadException;

public class ConstantDistribution extends AbstractDistribution {

	
	
	public ConstantDistribution(double[] parameters)
	{
		super(parameters);
		// TODO Auto-generated constructor stub
	}

	

	public ConstantDistribution(double cte) {
		super.parameters[0] = cte;
	}



	private Job job;
	final private static Log log = LogFactory.getLog(ConstantDistribution.class);
	
	

	@Override
	public SLA fetchSLA() {
		// TODO Auto-generated method stub
		SLA generatedSLA = null;
		try {
			job = super.swfParser.fetchNextJob();
			
			if (job != null) 
			{
				generatedSLA = new SLA();
				generatedSLA.setJobNumber(job.getJobNumber());
				generatedSLA.setGuaranteeTime((long)(job.getRunTime() * super.parameters[0] + job.getSubmitTime()));
				generatedSLA.setSLAType(1);
			} //if
		} catch (WorkloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedSLA;
	}



	@Override
	public String getHeader() {
		StringBuffer header = new StringBuffer();
		String line1 = ";SLA File: Multiple SLAs\n";
		header.append(line1);
		String line2 = "# " + 1 +"\n";
		header.append("# " + 1);
		
		return header.toString();
	}



	@Override
	public void setParametersString(String parametersString) {
		// TODO Auto-generated method stub
		
	}



	


	@Override
	public void initialize(boolean mayOverwrite) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean getMayOverwrite() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
