package mx.cicese.mcc.teikoku.scheduler.SLA.generator.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers.Job;

import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.InitializationException;
import mx.cicese.mcc.teikoku.scheduler.SLA.generator.exceptions.WorkloadException;



public class SWFParser {
	final private static Log log = LogFactory.getLog(SWFParser.class);
	
	private URL url;
	
	
	private LineNumberReader trace;
	
	
	private long batchLenght;
	
	private Queue<Job> jobs;
	
	private SWFParser() {
		this.batchLenght = 1000;
		this.jobs = new LinkedList<Job>();
	}
	
	public SWFParser(URL source)
		throws IOException {
		this();
		this.url = source;
		InputStreamReader isr = new InputStreamReader(this.url.openStream());
		
		log.debug("using " + this.url + 
				" as the input source for this workload");
		BufferedReader br = new BufferedReader(isr);
		LineNumberReader lnr = new LineNumberReader(br);
		
		this.trace = lnr;
	}
	
	public void parse() 
		throws IOException {
		//this.trace.reset();
		for (int i = 0; i < this.batchLenght; i++) {
			String currentLine = this.trace.readLine();
			this.markCurrentPosition();
			if (currentLine != null) {
				log.debug("parsing line " + this.trace.getLineNumber());
				this.parseLine(currentLine.trim());
			}
			else {
				break;
			}
		}
	}

	
	private void markCurrentPosition() 
		throws IOException {
			this.trace.mark(Short.MAX_VALUE);
	}
		
	private void parseLine (String currentLine) {
		if (currentLine.startsWith(SWFConstants.COMMENT_DELIMITER)) {
			this.checkForHeaderLine(currentLine.substring(1).trim());
		}
		else {
			this.checkForJobLine(currentLine.trim());
		}
	}

	private void checkForJobLine(String lineToParse) {
		StringTokenizer tokenizer = new StringTokenizer(lineToParse);
		
		Job job = new Job();
		
		if (tokenizer.countTokens() != SWFConstants.NUMBER_OF_JOB_FIELDS) {
			String msg = "line has missing fields, skipping...";
			log.warn(msg);
		} // if

		job.setJobNumber(Long.parseLong(tokenizer.nextToken()));
		job.setSubmitTime(Long.parseLong(tokenizer.nextToken()));
		job.setWaitTime(Long.parseLong(tokenizer.nextToken()));
		job.setRunTime(Long.parseLong(tokenizer.nextToken()));
		job.setNumberOfAllocatedProcessors(Integer.parseInt(tokenizer
				.nextToken()));
		job.setAverageCPUTimeUsed(Double.parseDouble(tokenizer
				.nextToken()));
		job.setUsedMemory(Integer.parseInt(tokenizer.nextToken()));
		job.setRequestedNumberofProcessors(Integer.parseInt(tokenizer
				.nextToken()));
		job.setRequestedTime(Long.parseLong(tokenizer.nextToken()));
		job.setRequestedMemory(Integer.parseInt(tokenizer.nextToken()));
		job.setStatus(Byte.parseByte(tokenizer.nextToken()));
		job.setUserID(Short.parseShort(tokenizer.nextToken()));
		job.setGroupID(Short.parseShort(tokenizer.nextToken()));
		job.setExecutableApplicationNumber(Long.parseLong(tokenizer
				.nextToken()));
		job.setQueueNumber(Byte.parseByte(tokenizer.nextToken()));
		job.setPartitionNumber(Byte.parseByte(tokenizer.nextToken()));
		job.setPrecedingJobNumber(Long.parseLong(tokenizer.nextToken()));
		job.setThinkTimeFromPrecedingJob(Double.parseDouble(tokenizer
				.nextToken()));
		
		jobs.offer(job);
	}

	private void checkForHeaderLine(String trim) {
		String keyword = null;
		
		
	}
	
	
	public void initialize ()
	throws InitializationException{
		try {
			this.markCurrentPosition();
			String currentLine = this.trace.readLine().trim();
			while (currentLine != null) {
				if (currentLine.startsWith(";")) {
					this.checkForHeaderLine(currentLine);
				}//if
				else{
					this.trace.reset();
					break;			
				}//else
				this.markCurrentPosition();
				currentLine = this.trace.readLine();
			}//while
	}//try
	catch (IOException e) {
		String msg = "problem during I/O: " + e.getMessage();
		log.error(msg, e);
		throw new InitializationException(msg, e);
	}// catch
		
	}//initialize
	
	
	private void ensureBufferRefill() throws WorkloadException {
		if ( this.jobs.isEmpty()) {
			try {
				this.parse();
			}// try
			catch (IOException e) {
				String msg = "problems during I/O: " + e.getMessage();
				log.error(msg,e);
				throw new WorkloadException(msg,e);
			}// catch
		}//if
	}//ensureBufferRefill
	
	public Job inspectNextJob()
	throws WorkloadException {
		this.ensureBufferRefill();
		return this.jobs.peek();
	}//inspectNextJob
	
	public Job fetchNextJob()
	throws WorkloadException {
		this.ensureBufferRefill();
		return this.jobs.poll();
	}//fetchNextJob
}
