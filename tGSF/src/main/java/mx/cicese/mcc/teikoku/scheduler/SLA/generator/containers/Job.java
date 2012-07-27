package mx.cicese.mcc.teikoku.scheduler.SLA.generator.containers;

public class Job {

	private long JobNumber;
	private long SubmitTime;
	private long WaitTime;
	private long RunTime;
	private int NumberOfAllocatedProcessors;
	private double AverageCPUTimeUsed;
	private int UsedMemory;
	private int RequestedNumberofProcessors;
	private long RequestedTime;
	private int RequestedMemory;
	private byte Status;
	private short UserID;
	private short GroupID;
	private long ExecutableApplicationNumber;
	private byte QueueNumber;
	private byte PartitionNumber;
	private long PrecedingJobNumber;
	private double ThinkTimeFromPrecedingJob;
	
	
	public long getJobNumber() {
		return JobNumber;
	}
	public void setJobNumber(long jobNumber) {
		JobNumber = jobNumber;
	}
	public long getSubmitTime() {
		return SubmitTime;
	}
	public void setSubmitTime(long submitTime) {
		SubmitTime = submitTime;
	}
	public long getWaitTime() {
		return WaitTime;
	}
	public void setWaitTime(long waitTime) {
		WaitTime = waitTime;
	}
	public long getRunTime() {
		return RunTime;
	}
	public void setRunTime(long runTime) {
		RunTime = runTime;
	}
	public int getNumberOfAllocatedProcessors() {
		return NumberOfAllocatedProcessors;
	}
	public void setNumberOfAllocatedProcessors(int numberOfAllocatedProcessors) {
		NumberOfAllocatedProcessors = numberOfAllocatedProcessors;
	}
	public double getAverageCPUTimeUsed() {
		return AverageCPUTimeUsed;
	}
	public void setAverageCPUTimeUsed(double averageCPUTimeUsed) {
		AverageCPUTimeUsed = averageCPUTimeUsed;
	}
	public int getUsedMemory() {
		return UsedMemory;
	}
	public void setUsedMemory(int usedMemory) {
		UsedMemory = usedMemory;
	}
	public int getRequestedNumberofProcessors() {
		return RequestedNumberofProcessors;
	}
	public void setRequestedNumberofProcessors(int requestedNumberofProcessors) {
		RequestedNumberofProcessors = requestedNumberofProcessors;
	}
	public long getRequestedTime() {
		return RequestedTime;
	}
	public void setRequestedTime(long requestedTime) {
		RequestedTime = requestedTime;
	}
	public int getRequestedMemory() {
		return RequestedMemory;
	}
	public void setRequestedMemory(int requestedMemory) {
		RequestedMemory = requestedMemory;
	}
	public byte getStatus() {
		return Status;
	}
	public void setStatus(byte status) {
		Status = status;
	}
	public short getUserID() {
		return UserID;
	}
	public void setUserID(short userID) {
		UserID = userID;
	}
	public short getGroupID() {
		return GroupID;
	}
	public void setGroupID(short groupID) {
		GroupID = groupID;
	}
	public long getExecutableApplicationNumber() {
		return ExecutableApplicationNumber;
	}
	public void setExecutableApplicationNumber(long executableApplicationNumber) {
		ExecutableApplicationNumber = executableApplicationNumber;
	}
	public byte getQueueNumber() {
		return QueueNumber;
	}
	public void setQueueNumber(byte queueNumber) {
		QueueNumber = queueNumber;
	}
	public byte getPartitionNumber() {
		return PartitionNumber;
	}
	public void setPartitionNumber(byte partitionNumber) {
		PartitionNumber = partitionNumber;
	}
	public long getPrecedingJobNumber() {
		return PrecedingJobNumber;
	}
	public void setPrecedingJobNumber(long precedingJobNumber) {
		PrecedingJobNumber = precedingJobNumber;
	}
	public double getThinkTimeFromPrecedingJob() {
		return ThinkTimeFromPrecedingJob;
	}
	public void setThinkTimeFromPrecedingJob(double thinkTimeFromPrecedingJob) {
		ThinkTimeFromPrecedingJob = thinkTimeFromPrecedingJob;
	}
	
	
	
}
