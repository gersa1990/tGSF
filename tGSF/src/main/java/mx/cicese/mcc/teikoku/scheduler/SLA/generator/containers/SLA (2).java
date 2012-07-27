package mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers;

public class SLA {
	
	private long JobNumber;
	
	private long GuaranteeTime;
	
	private long SLAType;
	
	private double SlackFactor;

	/**
	 * @return the slackFactor
	 */
	public double getSlackFactor() {
		return SlackFactor;
	}

	/**
	 * @param sLASelection the slackFactor to set
	 */
	public void setSlackFactor(double sLASelection) {
		SlackFactor = sLASelection;
	}

	/**
	 * @return the sLAType
	 */
	public long getSLAType() {
		return SLAType;
	}

	/**
	 * @param sLAType the sLAType to set
	 */
	public void setSLAType(long sLAType) {
		SLAType = sLAType;
	}

	public long getJobNumber() {
		return JobNumber;
	}

	public void setJobNumber(long jobNumber) {
		JobNumber = jobNumber;
	}

	public long getGuaranteeTime() {
		return GuaranteeTime;
	}

	public void setGuaranteeTime(long guaranteeTime) {
		GuaranteeTime = guaranteeTime;
	}
	
	
	
}
