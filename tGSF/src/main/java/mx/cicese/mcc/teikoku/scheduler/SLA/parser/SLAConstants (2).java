/**
 * 
 */
package mx.cicese.mcc.teikoku.scheduler.SLA.parser;

/**
 * @author Anuar
 *
 */
public class SLAConstants {
	//--- SLA header constants ----------------
	
	final public static String VERSION = "1.0";
	
	final public static String COMMENT_DELIMITER = ";";
	
	final public static byte NUMBER_OF_SLA_FIELDS = 4;
	
	final public static String PARAMETER_DELIMITER ="#";
	
	final public static String RESTRICTION_DELIMITER = "&";
	
	/**
	 * The same fields as the SWF Archive
	 * @author Anuar
	 *
	 */
	public static enum HeaderFields {

		COMPUTER("Computer:", false),
		VERSION("Version:", false),
		INSTALLATION("Installation:", false),
		ACKNOWLEDGE("Acknowledge:", true),
		INFORMATION("Information:", false),
		CONVERSION("Conversion:", false),
		MAX_JOBS("MaxJobs:", false),
		MAX_RECORDS("MaxRecords:", false),
		PREEMPTION("Preemption:", false),
		UNIX_START_TIME("UnixStartTime:", false),
		TIME_ZONE("TimeZone:", false),
		TIME_ZONE_STRING("TimeZoneString:", false),
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
		NOTE("Note:", true);

		final public static String PREEMPTION_NO = "No";

		final public static String PREEMPTION_YES = "Yes";

		final public static String PREEMPTION_DOUBLE = "Double";

		final public static String PREEMPTION_TS = "TS";

		private String identifier;

		private boolean multiValue;

		private HeaderFields(String name, boolean multiValue) {
			this.identifier = name;
			this.multiValue = multiValue;
		}

		public String getIdentifier() {
			return this.identifier;
		}

		public boolean isMultiValue() {
			return this.multiValue;
		}
	}
	
	// ----- SLA constants -----------------
	
	final public static class SLAFields {
		
		final public static String SLA_NUMBER = "slaNumber";
		
		final public static String SLA_GUARANTEE_TIME = "slaGuaranteeTime";
	}
}
