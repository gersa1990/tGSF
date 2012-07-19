package mx.cicese.mcc.teikoku.scheduler.SLA.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.cicese.mcc.teikoku.scheduler.SLA.SLA;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SLAParserRestricted {

	
	final private static Log log = LogFactory.getLog(SLAParserRestricted.class);
	
	private long batchLength;
	
	/**
	 * Holds a list of up-to-now already parsed and validated jobs, as a 
	 * {@link Queue} view.
	 */
	private Queue<SLA> slas;
	
	/**
	 * Holds the reader to access the input file.
	 */
	private LineNumberReader trace;
	
	/**
	 * Mark if the header has been read
	 */
	
	private boolean hasReadHeader;
	
	private boolean hasParameters;
	
	private List<Double> SLAPrices = new ArrayList<Double>();
	
	private List<Double> SLARestrictions = new ArrayList<Double>();
	/**
	 * Holds the path of the source file this parser reads.
	 */
	private URL url;
	
	private SLAParserRestricted() {
		this.batchLength = 1000;
		this.hasReadHeader = false;
		this.hasParameters = false;
		this.slas = new LinkedList<SLA>();
	}
	
	public SLAParserRestricted (URL source)
		throws IOException {
		this();
		this.url = source;
		InputStreamReader isr = new InputStreamReader(this.url.openStream());
		log.debug("using " + this.url + " as the input source for this workload SLAs");
		BufferedReader br = new BufferedReader(isr);
		LineNumberReader lnr = new LineNumberReader(br);
		
		this.trace = lnr;
		
	}
	
	private void readHeader() throws SLAException {
		try{
			this.markCurrentPosition();
			do {
				String currentLine = this.trace.readLine();
				// In this part we read the file and extract the parameters from the header 
				// that show us both the SLA prices and the SLA restricted values 
				if(currentLine.startsWith(SLAConstants.COMMENT_DELIMITER)) {
					this.checkForHeaderLine(currentLine.substring(1).trim());
					this.markCurrentPosition();
				} else
				{
					this.hasReadHeader = true;
					this.trace.reset();
				}
				}while(!this.hasReadHeader);
		} catch (IOException e) {
			String msg = "problems during I/O of header: " + e.getMessage();
			log.error(msg,e);
		}// catch
		
	}
	
	
	private void readParameters() throws SLAException {
		try {
			this.markCurrentPosition();
			do {
				String currentLine = this.trace.readLine();
				if(currentLine.startsWith(SLAConstants.PARAMETER_DELIMITER)||
						currentLine.startsWith(SLAConstants.RESTRICTION_DELIMITER)) {
					this.checkForParameteLine(currentLine);
					this.markCurrentPosition();
				} else
				{
					this.hasParameters = true;
					this.trace.reset();
				}
			}while (!this.hasParameters);
			} catch (IOException e) {
				String msg = "problems during I/O of parameters: " +e.getMessage();
				log.error(msg,e);
			}
	}
		
	
	
	private void checkForParameteLine(String trim) {
		if(trim.startsWith(SLAConstants.PARAMETER_DELIMITER))
		{
			trim = trim.substring(1).trim(); //clear the number sign
			SLAPrices.add(Double.parseDouble(trim));
		}
		else {
			trim = trim.substring(1).trim(); // crear the & sign
			SLARestrictions.add(Double.parseDouble(trim));
		}
		
	}

	private void parse() throws IOException {
		String line = new String();
		this.trace.reset();
		do {
		line = this.trace.readLine();	
		SLA s = parseSLALine (line);
		
		this.markCurrentPosition();
		if ( s!= null)
		{
			// get the price from the parcel value
			s.setPrice(SLAPrices.get(s.getType()).doubleValue());
			s.setRestriction(SLARestrictions.get(s.getType()).doubleValue());
			slas.offer(s);
		}
		else
		{
			return;
		}
		} while(slas.size() < this.batchLength);
		
	}
	
	
	private void ensureBufferRefill() throws SLAException {
		if (this.slas.isEmpty()) {
			try {
				slas.clear();
				this.parse();
			} //try
			catch (IOException e) {
				String msg = "problems during I/O in parse:  " + e.getMessage();
				log.error(msg,e);
				throw new SLAException (msg,e);
			} // catch
		}// if
	}
	
	
	public SLA fetchNextSLA() throws SLAException {
		if (this.hasReadHeader == false) {
			this.readHeader();
		}
		if (this.hasParameters == false)
		{
			this.readParameters();
			normalizePrices();
		}
		this.ensureBufferRefill();
		return this.slas.poll();
	}
	
	

	private void normalizePrices() {
		//This normalizes the SLA prices
		if(!SLAPrices.isEmpty())
		{
			List<Double> TempSLAPrices = new ArrayList<Double>();
			double maxPrice = SLAPrices.get(0).doubleValue();
			if(maxPrice != 0.0)
			{
			for (int i =0; i<SLAPrices.size(); i++)
			{
				double tempPrice =  SLAPrices.get(i).doubleValue()/maxPrice;
				TempSLAPrices.add(tempPrice);
			}
			SLAPrices = TempSLAPrices;
			}
		}
		
	}

	private SLA parseSLALine (String currentLine)
	{
		SLA sla = null;
		if (currentLine != null) {
			sla = new SLA(null);
		StringTokenizer lineParser = new StringTokenizer(currentLine);
		
		
		if(lineParser.countTokens() != SLAConstants.NUMBER_OF_SLA_FIELDS) {
			String msg = "line has missing fields, skipping...";
			log.warn(msg);
		}
		else {
		sla.setJobNumber(new Long((String) lineParser.nextToken()).longValue());		//1
		sla.setGuaranteeTime(new Long((String) lineParser.nextToken()).longValue());	//2
		sla.setType(new Integer((String) lineParser.nextToken()).intValue());			//3
		sla.setSlackFactor(new Double((String) lineParser.nextToken()).doubleValue());  //4
		}
		}
		return sla;
	}
	
	
	private void markCurrentPosition() 
		throws IOException {
			this.trace.mark(Short.MAX_VALUE);
		}
		
	

	private void checkForHeaderLine (String lineToParse) {
		String keyword = null;
		
		keyword = SLAConstants.HeaderFields.VERSION.getIdentifier();
		if(lineToParse.startsWith(keyword)) {
			log.debug("found header keyword match: '" + lineToParse + "'");
			lineToParse.substring(keyword.length()).trim();
		}//if
	}
	
}
