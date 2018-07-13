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
