package uk.gov.dvla.osg.ukmail.resources;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvla.osg.common.classes.Customer;
import uk.gov.dvla.osg.common.config.PostageConfiguration;
import uk.gov.dvla.osg.common.config.ProductionConfiguration;
import uk.gov.dvla.osg.common.enums.BatchType;
import uk.gov.dvla.osg.common.enums.Product;

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
	private int tenDigitJid;
	private String manifestFilename;
    private ProductionConfiguration prodConfig;
    private PostageConfiguration postConfig;
    private Product actualProduct;
    private BatchType batchType;
    public static int morristonNextItemRef;
    public static int fforestfachNextItemRef;
    private final String UNSORTEDPREFIX = "U";

	public UkMailManifest(Customer customer, Integer trayVol, Integer startPID, Integer endPID,
			Double weight, String runNo, String runDate, Product actualProduct) {
	   
	    this.prodConfig = ProductionConfiguration.getInstance();
	    this.postConfig = PostageConfiguration.getInstance();
	        
	    this.trayVol = trayVol;
	    this.firstPieceId = startPID;
	    this.lastPieceId = endPID;
	    this.trayWeight = weight.intValue();
	    this.runNo = runNo;
	    this.runDate = runDate;
	    
	    
  
	    this.tenDigitJid = customer.getTenDigitJid();
	    this.msc = customer.getMsc();
	    
	    this.batchType = customer.getBatchType();
       
	    if (BatchType.UNSORTED.equals(batchType)) {
            this.actualProduct = Product.UNSORTED;
            this.appName = UNSORTEDPREFIX + customer.getMailingId();
        } else {
            this.actualProduct = actualProduct;
            this.appName = customer.getMailingId();
        }
	    
	    this.itemId = getTrayId();
	    this.serviceCode = getServiceCode();
	    this.accountNo = getAccountNo();
	    this.format = getFormat();    
	    this.manifestFilename = setManifestFilename();	    
	    this.mailingId = setMailingId();
		this.altRef = setAltRef();
		this.customerRef = setCustomerRef();
		this.batchId = setBatchId();

		
	}
	
	public String print() {
		return String.format("%-10.10s%-5.5s%-6.6s%-1.1s%-1.1s%-3.3s%-10.10s%-20.20s%-20.20s%-20.20s%-8.8s%-20.20s",
					this.msc, this.trayVol, this.trayWeight, this.format, this.machinable, this.serviceCode,
					this.accountNo, this.mailingId, this.customerRef, this.altRef, this.collectionDate, this.batchId);
	}

	private String setMailingId() {
		return this.appName + "-" + this.runNo;
	}

	private String setAltRef() {
	    if (actualProduct.equals(Product.MM) && this.accountNo != null && this.runDate != null && this.itemId != null) {
	        return this.accountNo + "_" + this.runDate + "_" + String.format("%05d", this.itemId);
	    }
	    return "";
	}

	private String setCustomerRef() {
		String jid = Integer.toString(tenDigitJid);
		if (this.firstPieceId != null & this.lastPieceId != null & StringUtils.isNotBlank(jid)) {
			return String.format("%-6s / %-6s /%-3s", String.format("%06d", this.firstPieceId),
					String.format("%06d", this.lastPieceId), jid.substring(7));
		}
		return "";
	}

	private String setBatchId() {
	    String jid = Integer.toString(tenDigitJid);
	    if (this.appName != null && this.runNo != null && jid != null) {
	        return String.format("%-4.4s %-5.5s%-10.10s", this.appName, this.runNo, jid);
	    }
	    return "";
	}
	
   private Integer getTrayId() {
        if (getAccountNo().equals(postConfig.getUkmMAcc())) {
            morristonNextItemRef++;
            return morristonNextItemRef;
        }
        
        fforestfachNextItemRef++;
        return fforestfachNextItemRef;
    }

    private String getAccountNo() {
        if (BatchType.UNSORTED.equals(batchType))
            return postConfig.getUnsortedAccountNo();
        
        return "M".equalsIgnoreCase(prodConfig.getMailingSite()) ? postConfig.getUkmMAcc() : postConfig.getUkmFAcc();
    }
    
    private String getServiceCode() {
        if (BatchType.UNSORTED.equals(batchType))
            return postConfig.getUnsortedProduct();
        
        return actualProduct.equals(Product.MM) ? postConfig.getMmProduct() : postConfig.getOcrProduct();
    }
    
    private String setManifestFilename() {
        String productionArea = postConfig.getUkmConsignorDestinationDepartment();
        String mailingSite = prodConfig.getMailingSite().toUpperCase();
        String manifestTimestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        return StringUtils.joinWith(".", mailingSite, productionArea, appName, runNo, manifestTimestamp).concat(".DAT");
    }
    
    private String getFormat() {
        if (BatchType.UNSORTED.equals(batchType))
            return postConfig.getUnsortedFormat();
        
        return Product.MM.equals(actualProduct) ? postConfig.getMmFormat() : postConfig.getOcrFormat();
    }

    public String getMailingId() {
        return this.mailingId;
    }

    public String getAltRef() {
        return this.altRef;
    }

    public String getManifestFilename() {
        return this.manifestFilename;
    }
}
