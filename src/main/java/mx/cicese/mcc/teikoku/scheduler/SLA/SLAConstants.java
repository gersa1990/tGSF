package mx.cicese.mcc.teikoku.scheduler.SLA;

public final class SLAConstants {
//--- SLA Header Constant ----
	
	final public static String VERSION = "1.0";
	
	final public static String COMMENT_DELIMITER = "%";
	
	final public static byte NUMBER_OF_JOB_FILEDS = 2;
	
	
	public static enum HeaderFields {
	
		VERSION("Version:",false),
		COMPUTER("Computer",false),
		INSTALLATION("Installation", false),
		ACKNOWLEDGE("Acknowledge",false),
		INFORMATION("Information",false),
		CONVERSION("Conversion",false),
		MAX_JOBS("MaxJobs",false),
		MAX_RECORDS("MaxRecords",false),
		PREEMPTION("Preemption",false),
		UNIX_START_TIME("UnixStartTime",false),
		TIME_ZONE("TimeZone", false),
		TIME_ZONE_STRINF("TimeZoneString",false),
		START_TIME("StartTime:", false),
		END_TIME("EndTime:", false),
		MAX_NODES("MaxNodes:", false),
		MAX_PROCS("MaxProcs:", false),
		MAX_RUNTIME("MaxRuntime:", false),
		MAX_MEMORY("MaxMemory:", false),
		ALLOW_OVERUSE("AllowOveruse:", false),
		MAX_QUEUES("MaxQueues:", false),
		QUEUES("Queues:", false),
		QUEUE("Queue:", true),
		MAX_PARTITIONS("MaxPartitions:", false),
		PARTITIONS("Partitions:", false),
		PARTITION("Partition:", true),
		SLATYPE("SLAType:", false),
		NOTE("Note:", true);
		
		final public static String PREEMPTION_NO = "No";
		
		final public static String PREEMPTION_YES = "Yes";
		
		final public static String PREEMPTION_DOUBLE  = "Double";
		
		final public static String PREEMPTION_TS = "TS";

		private String identifier;

		private boolean multiValue;
		
		private HeaderFields (String name, boolean multiValue) {
		
			this.identifier=name;
			this.multiValue = multiValue;
			
		}
		
		public String getIdentifier () {
			return this.identifier;
		}
		
		public boolean isMultiValue () {
			return this.multiValue;
		}
		
	}
	
	final public static class SLAFields {
		
		final public static String JOB_NUMBER = "jobNumber";
		
	 	final public static String SLA_GUARANTEE = "slaGuarantee";
	
	}
	
}
