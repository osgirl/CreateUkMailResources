
package uk.gov.dvla.osg.ukmail.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.gov.dvla.osg.common.classes.Customer;
import uk.gov.dvla.osg.common.classes.Product;
import uk.gov.dvla.osg.common.config.PostageConfiguration;
import uk.gov.dvla.osg.common.config.ProductionConfiguration;

public class CreateUkMailResources {
	private static final Logger LOGGER = LogManager.getLogger();

	private String mAccNo = "";
	private String fAccNo = "";
	private String mTrayLookup = "";
	private String fTrayLookup = "";
	private float weight;
	private int minimumTrayVolume = 20;
	private String itemIdLookup = "";
	private String morristonNextItemDate;
	private String fforestfachNextItemDate;
	private String ukMailManifestConsignorPath;
	private String ukMailManifestArchivePath;
	private String soapFilePath;
	private String soapFileArchivePath;
	private String runNo;
	private String runDate;
	private String manifestTimestamp;
	private Product actualProduct;
	private String resourcePath;
	private Integer morristonNextItemRef;
	private Integer fforestfachNextItemRef;
	private Integer nextItemId;
	private PostageConfiguration postConfig;
	private ProductionConfiguration prodConfig;
	private InputFileHandler fh = new InputFileHandler();
	private ArrayList<UkMailManifest> manifestList = new ArrayList<UkMailManifest>();
	private ArrayList<Customer> ukMailCustomers;
	private HashSet<String> ukMailManifestPaths;
	private HashMap<String, Integer> ukmMap = null;
	private ArrayList<Customer> input;

	private boolean processMailmark = false;
	private boolean processUkMail = false;

	public CreateUkMailResources(ArrayList<Customer> customers, PostageConfiguration postConfig, String runNo,
			String actualProduct) {

		this.input = customers;
		this.resourcePath = postConfig.getUkmResourcePath();
		mAccNo = postConfig.getUkmMAcc();
		fAccNo = postConfig.getUkmFAcc();
		this.mTrayLookup = resourcePath + postConfig.getUkmMTrayLookupFile();
		this.fTrayLookup = resourcePath + postConfig.getUkmFTrayLookupFile();
		minimumTrayVolume = postConfig.getUkmMinimumTrayVolume();
		this.itemIdLookup = postConfig.getUkmItemIdLookupFile();
		this.actualProduct = Product.valueOf(actualProduct);
		this.ukMailManifestArchivePath = postConfig.getUkmManifestArchive();
		this.runDate = new SimpleDateFormat("ddMMyy").format(new Date());
		this.manifestTimestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
		this.prodConfig = ProductionConfiguration.getInstance();
		this.postConfig = postConfig;
		this.runNo = runNo;
		this.soapFilePath = postConfig.getUkmSoapDestination() + "SOAP.DAT";
		this.soapFileArchivePath = postConfig.getUkmSoapArchive() + "SOAP_ARCH.DAT";
		//Lookup the next item reference and date for each account number format of these files is:
		//DDMMYY:NEXT_REF_NUMBER (190815:154)
		try {
			String[] morristonNextItemDetails = fh.getNextBagRef(mTrayLookup).split(":");
			String[] fforestfachNextItemDetails = fh.getNextBagRef(fTrayLookup).split(":");
			// Pull next date out of the array
			morristonNextItemDate = morristonNextItemDetails[0];
			fforestfachNextItemDate = fforestfachNextItemDetails[0];

			//If the date at runtime is different to the lookup date then reset the item reference
			if (!morristonNextItemDate.equals(runDate.toString())) {
				morristonNextItemRef = 1;
			}
			if (!fforestfachNextItemDate.equals(runDate.toString())) {
				fforestfachNextItemRef = 1;
			}

			// Pull reference number out of the array
			morristonNextItemRef = Integer.parseInt(morristonNextItemDetails[1]);
			fforestfachNextItemRef = Integer.parseInt(fforestfachNextItemDetails[1]);
		} catch (NumberFormatException | IOException ex) {
			LOGGER.fatal("Unable to read item details from Tray Lookup files", ex);
		}

		//Get the next item ID from the lookup file
		String resourceFileName = resourcePath + itemIdLookup;
		try {
			nextItemId = Integer.parseInt(fh.getNextBagRef(resourceFileName));
		} catch (NumberFormatException | IOException ex) {
			LOGGER.fatal("Unable to read next item ID from {}", resourceFileName, ex);
		}

		if (nextItemId == 100000000) {
			nextItemId = 1;
		}

		//Check to see if this application requires UKMAIL resources
		processUkMail = !actualProduct.equals("UNSORTED");
		processMailmark = actualProduct.equals("MM");
	}

	public void method() {
		try {
			if (processMailmark || processUkMail) {
				//Create a sub-set of customers that are to be sent via UKMAIL
				ukMailCustomers = getUkMailCustomers(input);
				//Cleanup from any previously run attempts
				cleanup();

				//Main methods
				createUkMailManifest(ukMailCustomers);

				//createKickfile(manifestList, ukMailCustomers);

				if (processMailmark) {
					//Also creates barcode lookup file
					createSOAPfile(manifestList, ukMailCustomers);

					//Update item numbers
					fh.writeReplace(resourcePath + itemIdLookup, "" + nextItemId++);

					//Update bag reference

					//Could be a different path for a different account number
					fh.writeReplace(mTrayLookup, runDate.toString() + ":" + morristonNextItemRef);
					fh.writeReplace(fTrayLookup, runDate.toString() + ":" + fforestfachNextItemRef);

				}
			} else {
				LOGGER.info("No UKMAIL customers to process");
			}

		} catch (Exception e1) {
			LOGGER.fatal("ERROR:'{}'", e1.getMessage());
			e1.printStackTrace();
			System.exit(3);
		}

		if (ukMailManifestPaths != null) {
			for (String s : ukMailManifestPaths) {
				LOGGER.info("MANIFEST, '{}' exists {}", s, fh.checkFileExists(s));
			}
		}
		LOGGER.info("UkMailResources finished");
	}

	private void cleanup() {
		fh.deleteFile(soapFilePath);
		fh.deleteFile(soapFileArchivePath);
	}

	private void createSOAPfile(ArrayList<UkMailManifest> ukmm, ArrayList<Customer> customers) {
		ArrayList<SoapFileEntry> sf = new ArrayList<SoapFileEntry>();
		int index = -1;
		boolean first = true;
		for (Customer customer : customers) {
			if (customer.isSot() || first) {
				index++;
				first = false;
			}
			if (customer.isEog()) {
				String batchRef = ukmm.get(index).getMailingId() + "_"
						+ customer.getTenDigitJid().toString().substring(0, 7) + "000_" + manifestTimestamp;

				SoapFileEntry soapFileEntry = new SoapFileEntry(runNo, customer.getTenDigitJid().toString(),
						customer.getSequenceInChild(), postConfig.getMmAppname(), batchRef, postConfig.getMmScid(),
						postConfig.getMmClass(), customer.getDps(), getItemId(), postConfig.getMmXmlFormat(),
						postConfig.getMmMachineable(), postConfig.getMmMailType(), getNumberOfAddressLines(customer),
						getPostCode(customer), postConfig.getMmXmlProduct(), customer.getWeight(),
						ukmm.get(index).getAltRef());

				//Setting MM barcode content
				customer.setMmBarcodeContent(getMmBarcodeContent(getItemId(), customer));
				sf.add(soapFileEntry);
			}
		}

		PrintWriter pw1 = fh.createOutputFileWriter(soapFilePath);
		PrintWriter pw2 = fh.createOutputFileWriter(soapFileArchivePath);

		for (SoapFileEntry sfee : sf) {
			fh.appendToFile(pw1, sfee.print());
			fh.appendToFile(pw2, sfee.print());
		}
		fh.closeFile(pw1);
		fh.closeFile(pw2);

	}

	private String getMmBarcodeContent(String itemId, Customer cus) {
		String customerContent = "";
		if (StringUtils.isBlank(cus.getMmCustomerContent())) {
			// TODO: REPLACE WITH DATE AND APP NAME
			customerContent = String.format("%-5.5s", runNo) + cus.getTenDigitJid() + cus.getSequenceInChild();
		} else {
			customerContent = cus.getMmCustomerContent();
		}

		String str = String.format("%-4.4s%-1.1s%-1.1s%-1.1s%-7.7s%-8.8s%-9.9s%-1.1s%-7.7s%-6.6s%-25.25s",
				postConfig.getMmUpuCountryId(), postConfig.getMmInfoType(), postConfig.getMmVersionId(),
				postConfig.getMmClass(), postConfig.getMmScid(), itemId,
				cus.getPostcode().replace(" ", "") + cus.getDps(), postConfig.getMmReturnMailFlag(),
				postConfig.getMmReturnMailPc(), postConfig.getMmReserved(), customerContent);
		return str;
	}

	private String getPostCode(Customer customer) {
		return String.format("%-7s", customer.getPostcode()).replace(" ", "").replace(" ", "0");
	}

	private static Integer getNumberOfAddressLines(Customer customer) {
		Integer count = 0;
		if (!customer.getName1().equals("")) {
			count++;
			if (customer.getName1().contains("*")) {
				count++;
			}
		}
		if (!customer.getAdd1().equals("")) {
			count++;
		}
		if (!customer.getAdd2().equals("")) {
			count++;
		}
		if (!customer.getAdd3().equals("")) {
			count++;
		}
		if (!customer.getAdd4().equals("")) {
			count++;
		}
		if (!customer.getAdd5().equals("")) {
			count++;
		}
		if (!customer.getPostcode().equals("")) {
			count++;
		}
		return count;

	}

	private String getItemId() {
		if (nextItemId == 100000000) {
			nextItemId = 1;
		} else {
			nextItemId++;
		}
		return String.format("%08d", nextItemId);
	}

	private void createUkMailManifest(ArrayList<Customer> ukMailCustomer) {
		Integer itemCount = 0;
		int nextCustomerIdx = 0;
		Integer startPID = 1;
		Integer endPID = 1;
		Customer nextCustomer = null;
		int currentTrayItems = 0;
		double currentTrayWeight = 0;
		boolean firstCustomer = true;
		Customer previousCustomer = null;
		int lastCustomer = ukMailCustomers.get(ukMailCustomers.size() - 1).getOriginalIdx();

		for (Customer customer : ukMailCustomers) {
			if (firstCustomer) {
				startPID = customer.getSequenceInChild();
				firstCustomer = false;
			} else {
				if (customer.isSot()) {
					UkMailManifest manifest = new UkMailManifest(previousCustomer.getTenDigitJid(),
							previousCustomer.getMsc(), currentTrayItems, startPID,
							previousCustomer.getSequenceInChild(), previousCustomer.getAppName(), getTrayId(),
							getProductCode(), getManifestFilename(previousCustomer), currentTrayWeight, getAccountNo(),
							runNo, runDate, getFormat());
					manifestList.add(manifest);
					startPID = customer.getSequenceInChild();
					currentTrayItems = 0;
					currentTrayWeight = 0;
				} else if (customer.getOriginalIdx() == lastCustomer) {
					currentTrayWeight += customer.getWeight();
					//currentTrayItems++;
					UkMailManifest manifest = new UkMailManifest(customer.getTenDigitJid(), customer.getMsc(),
							currentTrayItems, startPID, customer.getSequenceInChild(), customer.getAppName(),
							getTrayId(), getProductCode(), getManifestFilename(customer), currentTrayWeight,
							getAccountNo(), runNo, runDate, getFormat());
					manifestList.add(manifest);
				}
			}
			currentTrayWeight += customer.getWeight();
			currentTrayItems++;
			previousCustomer = customer;
		}

		ukMailManifestPaths = new HashSet<String>();
		for (UkMailManifest ukmm : manifestList) {

			String output = ukmm.print(processMailmark);
			fh.write(ukMailManifestArchivePath + ukmm.getManifestFilename(), output);
			fh.write(ukMailManifestConsignorPath + ukmm.getManifestFilename(), output);
			ukMailManifestPaths.add(ukMailManifestArchivePath + ukmm.getManifestFilename());
		}

	}

	private String getManifestFilename(Customer customer) {
		String productionArea = postConfig.getUkmConsignorDestinationDepartment();
		String mailingSite = prodConfig.getMailingSite().toUpperCase();
		return mailingSite + "." + productionArea + "." + customer.getSelectorRef() + "." + runNo + "."
				+ manifestTimestamp + ".DAT";
	}

	private String getProductCode() {
		return actualProduct.name().equals("MM") ? postConfig.getMmProduct() : postConfig.getOcrProduct();
	}

	private String getFormat() {
		return actualProduct.name().equalsIgnoreCase("MM") ? postConfig.getMmFormat() : postConfig.getOcrFormat();
	}

	private Integer getTrayId() {
		String accountNo = getAccountNo();
		Integer itemId;

		if (accountNo.equals(mAccNo)) {
			itemId = morristonNextItemRef;
			morristonNextItemRef++;
		} else {
			itemId = fforestfachNextItemRef;
			fforestfachNextItemRef++;
		}
		return itemId;
	}

	private String getAccountNo() {
		return "M".equalsIgnoreCase(prodConfig.getMailingSite()) ? mAccNo : fAccNo;
	}

	public ArrayList<Customer> getUkMailCustomers(ArrayList<Customer> allCustomers) {

		/*
		 * return allCustomers.stream().filter( customer -> customer.getProduct().equals(Product.MM) || customer.getProduct().equals(Product.OCR)) .collect(Collectors.toCollection(ArrayList::new));
		 */

		ArrayList<Customer> filtered = new ArrayList<Customer>();
		for (Customer customer : allCustomers) {

			if (Product.MM.equals(customer.getProduct()) || Product.OCR.equals(customer.getProduct())) {
				filtered.add(customer);
			}
		}
		return filtered;

	}

}
