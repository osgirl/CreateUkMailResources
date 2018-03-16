package uk.gov.dvla.osg.ukmail.resources;

import org.apache.commons.lang3.StringUtils;

public class UkMailManifest {
	private String msc;
	private Integer trayVol;
	private Integer trayWeight;
	private String format;
	private String machinable = "Y";
	private String serviceCode;
	private String accountNo;
	private String mailingId;
	private String appName;
	private String runNo;
	private String runDate;
	private Integer itemId;
	private String customerRef;
	private Integer firstPieceId;
	private Integer lastPieceId;
	private String altRef;
	private String collectionDate = "";
	private String batchId;
	private String rpdJid;
	private int tenDigitJid;
	private String manifestFilename;

	public UkMailManifest(int tenDigitJid, String msc, Integer itemCount
			, Integer startPID, Integer endPID, String appName
			, Integer trayID, String serviceCode, String manifestFilename, Double weight, 
			String accountNo, String runNo, String runDate, String format) {
		
		this.accountNo = accountNo;
		this.appName = appName;
		this.runNo = runNo;
		this.firstPieceId = startPID;
		this.lastPieceId = endPID;
		this.tenDigitJid = tenDigitJid;
		this.runDate = runDate;
		this.itemId = trayID;
		this.format = format;
		this.msc = msc;
		this.serviceCode = serviceCode;
		this.trayVol = itemCount;
		this.manifestFilename = manifestFilename;
		this.trayWeight = weight.intValue();
		setAltRef();
		setCustomerRef();
		setMailingId();
		setBatchId();
	}
	
	public String print(boolean mailmark) {
		String str = "";
		if (mailmark) {
			str = String.format("%-10.10s%-5.5s%-6.6s%-1.1s%-1.1s%-3.3s%-10.10s%-20.20s%-20.20s%-20.20s%-8.8s%-20.20s",
					this.msc, this.trayVol, this.trayWeight, this.format, this.machinable, this.serviceCode,
					this.accountNo, this.mailingId, this.customerRef, this.altRef, this.collectionDate, this.batchId);
		} else {
			str = String.format("%-10.10s%-5.5s%-6.6s%-1.1s%-1.1s%-3.3s%-10.10s%-20.20s%-20.20s%-20.20s%-8.8s%-20.20s",
					this.msc, this.trayVol, this.trayWeight, this.format, this.machinable, this.serviceCode,
					this.accountNo, this.mailingId, this.customerRef, "", this.collectionDate, this.batchId);
		}
		return str;
	}

	public String getMsc() {
		return msc;
	}

	public void setMsc(String msc) {
		this.msc = msc;
	}

	public Integer getTrayVol() {
		return trayVol;
	}

	public void setTrayVol(Integer trayVol) {
		this.trayVol = trayVol;
	}

	public Integer getTrayWeight() {
		return trayWeight;
	}

	public void setTrayWeight(Integer trayWeight) {
		this.trayWeight = trayWeight;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMachinable() {
		return machinable;
	}

	public void setMachinable(String machinable) {
		this.machinable = machinable;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
		setAltRef();
	}

	public String getMailingId() {
		return mailingId;
	}

	public void setMailingId() {
		this.mailingId = this.appName + "-"+this.runNo;
	}

	public String getCustomerRef() {
		return customerRef;
	}

	public void setCustomerRef() {
		String jid = Integer.toString(tenDigitJid);
		if (this.firstPieceId != null & this.lastPieceId != null & StringUtils.isNotBlank(jid)) {
			this.customerRef = String.format("%-6s / %-6s /%-3s", String.format("%06d", this.firstPieceId),
					String.format("%06d", this.lastPieceId), jid.substring(7));
		}
	}

	public String getAltRef() {
		return altRef;
	}

	public void setAltRef() {
		if (this.accountNo != null && this.runDate != null && this.itemId != null) {
			this.altRef = this.accountNo + "_" + this.runDate + "_" + String.format("%05d", this.itemId);
		}
	}

	public String getJid() {
		return Integer.toString(tenDigitJid);
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId() {
		String jid = Integer.toString(tenDigitJid);
		if (this.appName != null && this.runNo != null && jid != null) {
			this.batchId = String.format("%-4.4s %-5.5s%-10.10s", this.appName, this.runNo, jid);
		}
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
		setMailingId();
		setBatchId();
	}

	public String getRunNo() {
		return runNo;
	}

	public void setRunNo(String runNo) {
		this.runNo = runNo;
		setMailingId();
		setBatchId();
	}

	public Integer getFirstPieceId() {
		return firstPieceId;
	}

	public void setFirstPieceId(Integer firstPieceId) {
		this.firstPieceId = firstPieceId;
		setCustomerRef();
	}

	public Integer getLastPieceId() {
		return lastPieceId;
	}

	public void setLastPieceId(Integer lastPieceId) {
		this.lastPieceId = lastPieceId;
		setCustomerRef();
	}

	public void setTenDigitJid(int jid) {
		this.tenDigitJid = jid;
		setCustomerRef();
		setBatchId();
	}

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
		setAltRef();
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
		setAltRef();
	}

	public void setMailingId(String mailingId) {
		this.mailingId = mailingId;
	}

	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getManifestFilename() {
		return manifestFilename;
	}

	public void setManifestFilename(String manifestFilename) {
		this.manifestFilename = manifestFilename;
	}

	public String getRpdJid() {
		return rpdJid;
	}

	public void setRpdJid(String rpdJid) {
		this.rpdJid = rpdJid;
	}

	public int getTenDigitJid() {
		return tenDigitJid;
	}
}
