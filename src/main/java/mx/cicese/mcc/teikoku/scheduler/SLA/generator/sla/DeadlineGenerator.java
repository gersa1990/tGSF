package mx.cicese.mcc.teikoku.scheduler.SLA.generator.sla;





import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;



import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.AbstractDistribution;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.ConstantDistribution;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.MultipleConstantDistribution;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.NormalDistribution;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.InitializationException;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.TextfileWriterException;

public class DeadlineGenerator {

	private CommandLine cmd;
	
	private String args[];
	

	private URL swfPath;
	
	private URL slaPath;
	
	private String slaName;
	
	private AbstractDistribution abstractDistribution;
	

	
	
	final private static Log log = LogFactory.getLog(DeadlineGenerator.class);
	
	/**
	 * This method initialize the DeadlineGenerator to take the parameters then create the SLA file
	 */
	public DeadlineGenerator(String args[])
	{
		BasicConfigurator.configure();
		
		try {
			getArgs(args);
			createOptions();
			getParameters();
			validateInput();
			abstractDistribution.setSWFParser(swfPath);
			if(slaPath == null)
			{
				String path = swfPath.getFile();
				path = path.replaceFirst(".swf", ".sla");
				slaPath = new File(path).toURI().toURL();
			}
			TextfileWriter textfileWriter = new TextfileWriter(abstractDistribution, slaPath);
			textfileWriter.initialize();
		    }//try
			catch (MalformedURLException e) {
				String msg = "The input path parameter could not be converted to a valid path. " + e.getMessage();
				log.error(msg);		
		    }//catch 
			catch(InitializationException e) {
				String msg = "One distribution and one input file path should be specified. " + e.getMessage();
				log.error(msg);
			}//catch
			catch(IOException e)
			{
				String msg = "Problems opening the SWF file. " + e.getMessage();
				log.error(msg);
			}
			catch (TextfileWriterException e)
			{
				String msg = "Problems creating the SLA file." + e.getMessage();
				log.error(msg);
			}
	}
	
	
	private void getParameters()
	{
		//create new parser
		CommandLineParser parser = new GnuParser();
		Options op = createOptions();
		
		try {
			// parse the command line arguments
			 cmd = parser.parse(op, args);
		
		}
		catch (ParseException exp) {
			System.err.println("Parsing failed. Reason: " + exp.getMessage());
		}
		
		
		
	}
	
	
	private void validateInput() throws MalformedURLException, InitializationException
	{
		Option[] listOfOptions = cmd.getOptions();
		int incomingDist = 0;
		boolean swfpath = false;
		
		
		for ( int i=0; i<listOfOptions.length; i++)
		{
			Option currentOption = listOfOptions[i];
			
			
			switch((Parameters.Types)currentOption.getType())
			{
			
			case NORMAL_DIST:
				incomingDist++;
				//parameters extraction for the normal distribution
				double mean = Double.parseDouble(currentOption.getValue(0));
				double sd = Double.parseDouble(currentOption.getValue(1));
				abstractDistribution = new NormalDistribution(mean,sd);
				break;
				
			case CONSTANT_DIST:
				incomingDist++;
				//parameters extraction for the constant distribution
				 double cte = Double.parseDouble(currentOption.getValue());
				 abstractDistribution = new ConstantDistribution(cte);
				break;
				
			case SWF_PATH:
				swfpath=true;
				//parameters extraction for the SWF file path
				String swfStringPath = currentOption.getValue();
				swfPath = new File(swfStringPath).toURI().toURL();
				break;
				
			case SLA_PATH:
				String slaStringPath = currentOption.getValue();
				slaPath = new File(slaStringPath).toURI().toURL();
				break;
				
			case SLA_NAME:
				slaName = currentOption.getValue();
				break;
				
			case MULTIPLE_CONSTANT_DIST:
				incomingDist++;
				//parameters extraction for multiple constant distribution
				String parametersString = currentOption.getValue();
				abstractDistribution = new MultipleConstantDistribution();
				break;
			}
			
		}
		if(!swfpath||incomingDist!=1) {
		//the options specify a path and has at least one distribution
			throw new InitializationException();
		}
	}
	
	@SuppressWarnings("static-access")
	/*
	 * Create all the command line available options
	 */
			
	private Options createOptions()
	{
		Option help = new Option("help", "print this message");
		Option swffile = OptionBuilder.withArgName("inputfile")
						.hasArg()
						.withDescription(" use given swf file as source")
						.withType(Parameters.Types.SWF_PATH)
						.create("swffile");
		
		Option normalValues = OptionBuilder.withArgName(" normal distribution: <mean>,<sd>" )
						  .hasArgs(2)
						  .withValueSeparator(',')
						  .withDescription("use values for given parametes")
						  .withType(Parameters.Types.NORMAL_DIST)
						  .create("N");
		
		Option constantValue = OptionBuilder.withArgName(" constant distribution: <cte> ")
								.hasArg()
								.withDescription("use value for given parameter")
								.withType(Parameters.Types.CONSTANT_DIST)
								.create("C");
		
		Option multipleConstantValue = OptionBuilder.withArgName(" multiple constant distribution: {(<slack factor>,<price>)...(<slack factor>,<price>)}")
										.hasArg()
										.withDescription("use value for given parametres")
										.withType(Parameters.Types.MULTIPLE_CONSTANT_DIST)
										.create("MC");
		
		Option slapath = OptionBuilder.withArgName("outputfilepath")
						 .hasArg()
						 .withDescription(" use given sla file path, default swf file path")
						 .withType(Parameters.Types.SLA_PATH)
						 .create("slapath");
		
		Option slaname = OptionBuilder.withArgName("outputfilename")
						 .hasArg()
						 .withDescription(" use given sla file name, default swf file name sla extension")
						 .withType(Parameters.Types.SLA_NAME)
						 .create("slaname");
		
		
		
		
		Options options = new Options();
		
		//register the options
		options.addOption(help);
		options.addOption(swffile);
		options.addOption(constantValue);
		options.addOption(normalValues);
		options.addOption(slapath);
		options.addOption(slaname);
		options.addOption(multipleConstantValue);
		return options;
		
	}
	
	private void getArgs (String[] args)
	{
		this.args = args;
	}
}
