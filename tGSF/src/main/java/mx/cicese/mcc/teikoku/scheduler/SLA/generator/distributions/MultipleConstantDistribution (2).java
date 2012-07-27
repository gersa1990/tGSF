package mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.Job;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.InitializationException;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.WorkloadException;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.MultipleConstantDistribution;

public class MultipleConstantDistribution extends AbstractDistribution {
	
	private Job job;
	final private static Log log = LogFactory.getLog(MultipleConstantDistribution.class);
	private Random randomGenerator = new Random();
	
	private List<ConstantDistribution> constantList = new ArrayList<ConstantDistribution>();
	
	private int numberOfSLAs;
	
	private String parametersString;
	
	private boolean mayOverwrite;
	
	public MultipleConstantDistribution() {
		
	}
	
	
	@Override
	public void initialize(boolean mayOverwrite) {
		numberOfSLAs = 0;
		try {
			parser(parametersString);
			this.mayOverwrite = mayOverwrite;
		} catch (InitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public SLA fetchSLA() {
		SLA generatedSLA = null;
		try {
			job = super.swfParser.fetchNextJob();
			
			if (job != null) 
			{
				generatedSLA = new SLA();
				generatedSLA.setJobNumber(job.getJobNumber());
				double [] SLASelection = calculateSLA(job.getRunTime(), job.getSubmitTime());
				generatedSLA.setGuaranteeTime((long)SLASelection[0]);
				generatedSLA.setSLAType((long)SLASelection[1]);
				generatedSLA.setSlackFactor(SLASelection[2]);
			} //if
		} catch (WorkloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedSLA;
	}
	
	//This function parcers the parameters into a list of ConstantDistribution objects
	@SuppressWarnings("unchecked")
	private void parser (String parameters) throws InitializationException 
	{
		List<String> matches = new ArrayList<String>();
		Pattern p = Pattern.compile("\\(([^\\(]*)\\)"); //regex to take what is inside the parentheses 
		Matcher matcher = p.matcher(parameters);
		boolean matchFound = matcher.find();
		String groupStr ="";
		while(matchFound) {
		 groupStr = matcher.group(matcher.groupCount());
		 matches.add(groupStr);
		 matchFound = matcher.find();
		}// while
		//in this point we have the input parameters in pairs divided by a comma 
		for(int i=matches.size()-1; i>=0; i--)
		{
			String[] numbers = matches.remove(i).split(";");
			if(numbers.length ==2)
			{
				try {
				ConstantDistribution constantDistribution = new ConstantDistribution();
				constantDistribution.setSlackFactor(Double.parseDouble(numbers[0]));
				constantDistribution.setPrice(Double.parseDouble(numbers[1]));
				constantList.add(constantDistribution);
				}
				catch (NumberFormatException e)
				{
					String msg = "There was a problem parsing the input parametrs CM pairs. " + e.toString();
					throw new InitializationException(msg);
				}
				numberOfSLAs++;
			}
			else
			{
				String msg = "There was a problem parsing the input parametrs CM pairs.";
				throw new InitializationException(msg);
			}
		}//for
		Collections.sort(constantList); // The SLAs are non decreasing sorted by them price
		log.debug("The number of SLA rules found are " + numberOfSLAs);
		
	}
	
	private double[] calculateSLA(long runTime, long submitTime)
	{
		//this are the parameters returned by this function the deadline and the number of SLA that
		// generate this slack factor
		double[] returnValues = new double[3];
		int randomSelection = randomGenerator.nextInt(numberOfSLAs);
		ConstantDistribution constantDistribution = constantList.get(randomSelection);
		returnValues[0] = (long) (runTime*constantDistribution.getSlackFactor() + submitTime);
		returnValues[1] = (long) randomSelection;
		returnValues[2] = (double) constantDistribution.getSlackFactor();
		String msg = "The SLA calculator has picked the random number " + randomSelection + " to generate the SLA.";
		log.debug(msg);
		return returnValues;
		
	}
		
	private class ConstantDistribution implements Comparable {
		private double slackFactor;
		/**
		 * @return the slackFactor
		 */
		double getSlackFactor() {
			return slackFactor;
		}
		/**
		 * @param slackFactor the slackFactor to set
		 */
		void setSlackFactor(double slackFactor) {
			this.slackFactor = slackFactor;
		}
		/**
		 * @return the price
		 */
		double getPrice() {
			return price;
		}
		/**
		 * @param price the price to set
		 */
		void setPrice(double price) {
			this.price = price;
		}
		private double price;
		
		@Override
		public int compareTo(Object arg0) {
		
		
		ConstantDistribution tempConstantDistr = (ConstantDistribution) arg0;
		
		if(this.getPrice() > tempConstantDistr.getPrice()){
			return -1;
					
		} else if(this.getPrice() < tempConstantDistr.getPrice() ){
			return 1;
		} else
		{
			return 0;
		}
		}
		
		
		
	}

	@Override
	public String getHeader() {
		StringBuffer header = new StringBuffer();
		String line1 = ";SLA File: Multiple SLAs";
		header.append(line1);
		Iterator<MultipleConstantDistribution.ConstantDistribution> it = constantList.iterator();
		while(it.hasNext())
		{
			header.append("\n"+"# " + it.next().getPrice());
		}
		return header.toString();
	}

	@Override
	public void setParametersString(String parametersString) {
		this.parametersString = parametersString;
		
	}


	@Override
	public boolean getMayOverwrite() {
		// TODO Auto-generated method stub
		return this.mayOverwrite;
	}






	
}
