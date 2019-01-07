package uk.gov.dvla.osg.ukmail.resources;

import org.apache.commons.lang3.StringUtils;

public class SoapFileEntry {
	private String runNo;
	private String jid;
	private Integer pid;
	private String appName;
	
	private String batchRef;
	private String scid;
	private String clasz;
	private String dps;
	private String itemId;
	private String format;
	private String machineable;
	private String mailType;
	private Integer noOfAddressLines;
	private String postcode;
	private String product;
	private Integer weight;
	private String spare8;
	
	public static class SoapFileEntryBuilder {
	    private String runNo;
	    private String jid;
	    private Integer pid;
	    private String appName;
	    private String batchRef;
	    private String scid;
	    private String clasz;
	    private String dps;
	    private String itemId;
	    private String format;
	    private String machineable;
	    private String mailType;
	    private Integer noOfAddressLines;
	    private String postcode;
	    private String product;
	    private Integer weight;
	    private String spare8;
	    
	    public static SoapFileEntryBuilder getInstance() {
            return new SoapFileEntryBuilder();
        }
	    
	    public SoapFileEntryBuilder runNo(String newRunNo) {
	        this.runNo = newRunNo;
            return this;
	    }
	    
	    public SoapFileEntryBuilder jid(String newJid) {
	        jid = newJid;
            return this;
	    }
	    
	    public SoapFileEntryBuilder pid(Integer newPid) {
	        pid = newPid;
            return this;
	    }
	    
	    public SoapFileEntryBuilder appName(String newAppName) {
	        this.appName = newAppName;
            return this;
	    }
	    
	    public SoapFileEntryBuilder batchRef(String newBatchRef) {
	        this.batchRef = newBatchRef;
            return this;
	    }
	    
	    public SoapFileEntryBuilder scid(String newScid) {
	        this.scid = newScid;
	        return this;
	    }
	    public SoapFileEntryBuilder clasz(String newClasz) {
	        clasz = newClasz;
            return this;
	    }
	    public SoapFileEntryBuilder dps(String newDps) {
	        dps = newDps;
            return this;
	    }
	    
	    public SoapFileEntryBuilder itemId(String newItemID) {
	        itemId = newItemID;
            return this;
	    }
	    
	    public SoapFileEntryBuilder format(String newFormat) {
	        format = newFormat;
            return this;
	    }
	    
	    public SoapFileEntryBuilder machineable(String newMachineable) {
	        machineable = newMachineable;
            return this;
	    }
	    public SoapFileEntryBuilder mailType(String newMailType) {
	        mailType = newMailType;
            return this;
	    }
	    
	    public SoapFileEntryBuilder noOfAddressLines(Integer newNoOfAddressLines) {
	        noOfAddressLines = newNoOfAddressLines;
            return this;
	    }
	    public SoapFileEntryBuilder postcode(String newPostcode) {
	        postcode = newPostcode;
            return this;
	    }
	    public SoapFileEntryBuilder product(String newProduct) {
	        product = newProduct;
            return this;
	    }
	    
	    public SoapFileEntryBuilder weight(Double newWeight) {
	        weight = newWeight.intValue();
	        return this;
	    }
	    
	    public SoapFileEntryBuilder spare8(String newSpare8) {
	        spare8 = newSpare8;
            return this;
	    }
	    
	    public SoapFileEntry build() {
	        return new SoapFileEntry(this); 
	    }
	    
	}
	
	private SoapFileEntry(SoapFileEntryBuilder builder) {
	    this.runNo = builder.runNo;
        this.jid = builder.jid;
        this.pid = builder.pid;
        this.appName = builder.appName;
        this.batchRef = builder.batchRef;
        this.scid = builder.scid;
        this.clasz = builder.clasz;
        this.dps = builder.dps;
        this.itemId = builder.itemId;
        this.format = builder.format;
        this.machineable = builder.machineable;
        this.mailType = builder.mailType;
        this.noOfAddressLines = builder.noOfAddressLines;
        this.postcode = builder.postcode;
        this.product = builder.product;
        this.weight = builder.weight.intValue();
        this.spare8 = builder.spare8;
	}
	
	public SoapFileEntry(String runNo, String jid, Integer pid, String appName, String batchRef, String scid,
			String clasz, String dps, String itemId, String format, String machineable, String mailType,
			Integer noOfAddressLines, String postcode, String product, Double weight, String spare8) {
		this.runNo = runNo;
		this.jid = jid;
		this.pid = pid;
		this.appName = appName;
		this.batchRef = batchRef;
		this.scid = scid;
		this.clasz = clasz;
		this.dps = dps;
		this.itemId = itemId;
		this.format = format;
		this.machineable = machineable;
		this.mailType = mailType;
		this.noOfAddressLines = noOfAddressLines;
		this.postcode = postcode;
		this.product = product;
		this.weight = weight.intValue();
		this.spare8 = spare8;
	}
	
	public String print() {
	    return StringUtils.joinWith("|", 
                this.appName, this.batchRef, this.scid, this.clasz, this.dps,
                this.itemId, this.format, this.machineable, this.mailType, this.noOfAddressLines, 
                this.postcode, this.product, this.spare8, this.weight, this.runNo, this.jid, this.pid);
	}

}
