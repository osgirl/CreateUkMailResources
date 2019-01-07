package uk.gov.dvla.osg.royalmail.resources;

import org.apache.commons.lang3.StringUtils;

public class RM_SoapFileEntry {

    // ROYAL MAIL REQUIRED FIELDS
    private String itemId;
    private String batchRef;
    private String product;
    private String format;
    private String mailClass;
    private String weight;
    private String mailType;
    private String machinable;
    private String noOfAddressLines;
    private String postcode;
    private String dps;
    private String customerRef;
    // OSG FIELDS
    private String appName;
    private String runNo;
    private String jobId;
    private String pieceId;

    public String print() {
        return StringUtils.joinWith("|", this.appName, this.batchRef, this.mailClass, this.dps,this.itemId, this.format, this.machinable, this.mailType, 
                this.noOfAddressLines, this.postcode, this.product, this.customerRef, this.weight, this.runNo, this.jobId, this.pieceId);
    }

    private RM_SoapFileEntry(Builder builder) {
        this.itemId = builder.itemId;
        this.batchRef = builder.batchRef;
        this.product = builder.product;
        this.format = builder.format;
        this.mailClass = builder.mailClass;
        this.weight = builder.weight;
        this.mailType = builder.mailType;
        this.machinable = builder.machinable;
        this.noOfAddressLines = builder.noOfAddressLines;
        this.postcode = builder.postcode;
        this.dps = builder.dps;
        this.customerRef = builder.customerRef;
        this.runNo = builder.runNo;
        this.jobId = builder.jobId;
        this.pieceId = builder.pieceId;
    }

    /**
     * Creates builder to build {@link RM_SoapFileEntry}.
     * 
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link RM_SoapFileEntry}.
     */
    public static final class Builder {
        private String itemId;
        private String batchRef;
        private String product;
        private String format;
        private String mailClass;
        private String weight;
        private String mailType;
        private String machinable;
        private String noOfAddressLines;
        private String postcode;
        private String dps;
        private String customerRef;
        private String runNo;
        private String jobId;
        private String pieceId;

        private Builder() {
        }

        public Builder withItemID(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder withBatchRef(String batchRef) {
            this.batchRef = batchRef;
            return this;
        }
        
        public Builder withProduct(String product) {
            this.product = product;
            return this;
        }

        public Builder withFormat(String format) {
            this.format = format;
            return this;
        }

        public Builder withMailClass(String mailClass) {
            this.mailClass = mailClass;
            return this;
        }

        public Builder withWeight(String weight) {
            this.weight = weight;
            return this;
        }

        public Builder withMailType(String mailType) {
            this.mailType = mailType;
            return this;
        }

        public Builder withMachinable(String machinable) {
            this.machinable = machinable;
            return this;
        }

        public Builder withNoOfAddressLines(String noOfAddressLines) {
            this.noOfAddressLines = noOfAddressLines;
            return this;
        }

        public Builder withPostcode(String postcode) {
            this.postcode = postcode;
            return this;
        }

        public Builder withDps(String dps) {
            this.dps = dps;
            return this;
        }

        public Builder withCustomerRef(String customerRef) {
            this.customerRef = customerRef;
            return this;
        }

        public Builder withRunNo(String runNo) {
            this.runNo = runNo;
            return this;
        }

        public Builder withJobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withPieceId(String pieceId) {
            this.pieceId = pieceId;
            return this;
        }

        public RM_SoapFileEntry build() {
            return new RM_SoapFileEntry(this);
        }
    }

}
