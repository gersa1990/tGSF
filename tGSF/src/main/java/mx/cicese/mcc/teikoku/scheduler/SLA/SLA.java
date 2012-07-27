package mx.cicese.mcc.teikoku.scheduler.SLA;

import java.util.UUID;


public class SLA {
	
	private UUID uuid;
	
	private String name;
	
	private long guaranteeTime;
	
	private long jobNumber;
	
	private int type;

	private double price;
	
	private double slackFactor;

	private double restriction;
	
	private long restrictedTime;
	
	
	final public static String CONFIGURATION_SECTION = "sla";
	
	/**
	 * @return the slackFactor
	 */
	public double getSlackFactor() {
		return slackFactor;
	}

	/**
	 * @param slackFactor the slackFactor to set
	 */
	public void setSlackFactor(double slackFactor) {
		this.slackFactor = slackFactor;
	}

	/**
	 * Getter method for the "guaranteeTime" field of this type.
	 * @return The current contents of "this.guaranteeTime"
	 */
	public long getGuaranteeTime() {
		return guaranteeTime;
	}
	
	/**
	 * Setter method for the "guaranteeTime" field of this type.
	 * @param guaranteeTime of the type Instant
	 */
	
	public void setGuaranteeTime(long guaranteeTime) {
		this.guaranteeTime = guaranteeTime;
	}
	
	
	
	/**
	 * SLA default constructor
	 */
	public SLA() {
		this.uuid = UUID.randomUUID();
		this.setName(this.uuid.toString());
		this.guaranteeTime = 0;
		this.jobNumber = 0;
	}
	
	
	/**
	 * 
	 * @param name
	 */
	
	public void setName(String name) {
		this.name = name;
		
	}
	/**
	 * Create an instant of the class with the parameter name
	 * @param name
	 */
	
	public SLA(String name) {
		this();
		this.setName(name);
	}
	
	/**
	 * Getter method for the "uuid" field 
	 * @return the current contents of "this.uuid"
	 */
	final public UUID getUUID() {
		return this.uuid;
	}
	
	/**
	 * Getter method for the "name" field
	 * @return the current contents of "this.name"
	 */
	public String getName() {
		return name;
	}

	public void setJobNumber(long jobNumber) {
		this.jobNumber = jobNumber;
		
	}
	
	public long getJobNumber () {
		return this.jobNumber;
	}
	
	
	public int getType () {
		return this.type;
	}
	
	public void setType (int type) {
		this.type = type;
	}

	public void setPrice(double price) {
		// TODO Auto-generated method stub
		this.price = price;
	}
	
	public double getPrice () {
		return this.price;
	}

	public void setRestriction(double restrictionValue) {
		// TODO Auto-generated method stub
		this.restriction = restrictionValue;
	}
	
	public double getRestriction() {
		return this.restriction;
	}

	public long getRestrictedTime() {
		this.restrictedTime = (long) (this.guaranteeTime*this.restriction);
		return this.restrictedTime;
	}

}
