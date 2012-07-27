package mx.cicese.mcc.teikoku.scheduler.SLA.generator.sla;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.SLA;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.AbstractDistribution;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.TextfileWriterException;



public class TextfileWriter {
	
	private URL slafilepath;
	
	private PrintWriter linePrinter;
	
	private AbstractDistribution distribution;

	final private static Log log = LogFactory.getLog(TextfileWriter.class);
	
	
	
	public TextfileWriter ( AbstractDistribution distribution, URL url) throws TextfileWriterException
	{
		setDistribution(distribution);
		setURL(url);
		createFile();
		
	}
	
	
	public void initialize () {
		this.writeHeader();
		this.writeSLA();
	}
	
	private void createFile () throws TextfileWriterException
	{
		try {
			
			
			FileWriter fstream = new FileWriter(slafilepath.getFile());
			linePrinter = new PrintWriter(fstream);
			
		} catch (IOException e) {
			
			String msg = "problems while creating the output file: " + e.getMessage();
			log.error(msg,e);
			throw new TextfileWriterException(msg,e);
		}
	}
	
	
	private void writeHeader()
	{
		String header = distribution.getHeader();
		linePrinter.println(header);
		log.debug("The header has been writen");
	}
	
	private void writeSLA () 
	{
		
		int count = 0;
		SLA currentSLA= null;
		do
		{
			currentSLA = distribution.fetchSLA();
			if (currentSLA != null)
			{
				count ++;
				String line = currentSLA.getJobNumber() +"\t" + currentSLA.getGuaranteeTime() +"\t" + currentSLA.getSLAType() +"\t" + currentSLA.getSlackFactor();
				linePrinter.println(line);
				log.debug("The SLA " + line + " has been writen");
			}
		}while (currentSLA != null);
		linePrinter.close();
		String msg = count + " SLA has been written";
		log.debug(msg);
	}
	
	
	private void setDistribution (AbstractDistribution  ad)
	{
		this.distribution = ad;
	}
	
	
	private void setURL (URL url)
	{
		this.slafilepath = url;
	}
}
