package mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.WorkloadException;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.Job;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;

public class ConstantDistributionRestricted extends AbstractDistribution {

	
	private Job job;
	final private static Log log = LogFactory.getLog(ConstantDistributionRestricted.class);
	
	public ConstantDistributionRestricted(double[] parameters)
	{
		super(parameters);
	}
	
	
	public ConstantDistributionRestricted (double cte)
	{
		super.parameters[0] = cte;
	}
	
	@Override
	public SLA fetchSLA() {
		// TODO Auto-generated method stub
		SLA generatedSLA = null;
		try {
			job = super.swfParser.fetchNextJob();
			
			if(job != null)
			{
				generatedSLA = new SLA();
				generatedSLA.setJobNumber(job.getJobNumber());
				generatedSLA.setGuaranteeTime((long)(job.getRunTime() * super.parameters[0] + job.getSubmitTime()));
				generatedSLA.setSLAType(1);
			}
		} 
		catch (mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.WorkloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedSLA;
	}

	@Override
	public String getHeader() {
		StringBuffer header = new StringBuffer();
		String line1 = ";SLA File: Multiple SLAs Restricted Distribution\n";
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
