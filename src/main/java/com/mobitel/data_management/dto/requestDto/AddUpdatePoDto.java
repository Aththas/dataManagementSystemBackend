package com.mobitel.data_management.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AddUpdatePoDto {

    @NotNull(message = "PO Number cannot be null")
    @Positive(message = "PO Number must be a positive number")
    private long poNumber;

    @NotNull(message = "Creation Date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;

    @NotNull(message = "PO Creation Date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date poCreationDate;

    @NotEmpty(message = "PO Type cannot be empty")
    @NotNull(message = "PO Type cannot be null")
    private String poType;

    @NotEmpty(message = "Vendor Name cannot be empty")
    @NotNull(message = "Vendor Name cannot be null")
    private String vendorName;

    @NotEmpty(message = "Vendor Site Code cannot be empty")
    @NotNull(message = "Vendor Site Code cannot be null")
    private String vendorSiteCode;

    @NotEmpty(message = "PO Description cannot be empty")
    @NotNull(message = "PO Description cannot be null")
    private String poDescription;

    @NotEmpty(message = "Approval Status cannot be empty")
    @NotNull(message = "Approval Status cannot be null")
    private String approvalStatus;

    @NotEmpty(message = "Currency cannot be empty")
    @NotNull(message = "Currency cannot be null")
    private String currency;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be a positive number")
    private BigDecimal amount;

    @NotNull(message = "Matched Amount cannot be null")
    @Positive(message = "Matched Amount must be a positive number")
    private BigDecimal matchedAmount;

    @NotEmpty(message = "Buyer Name cannot be empty")
    @NotNull(message = "Buyer Name cannot be null")
    private String buyerName;

    @NotEmpty(message = "Closure Status cannot be empty")
    @NotNull(message = "Closure Status cannot be null")
    private String closureStatus;

    @NotEmpty(message = "PR Number cannot be empty")
    @NotNull(message = "PR Number cannot be null")
    private String prNumber;

    @NotNull(message = "PR Creation Date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date prCreationDate;

    @NotNull(message = "Requisition Header ID cannot be null")
    @Positive(message = "Requisition Header ID must be a positive number")
    private Long requisitionHeaderId;

    @NotEmpty(message = "Requester Name cannot be empty")
    @NotNull(message = "Requester Name cannot be null")
    private String requesterName;

    @NotEmpty(message = "Requester Employee Number cannot be empty")
    @NotNull(message = "Requester Employee Number cannot be null")
    private String requesterEmpNum;

    @NotNull(message = "Line Number cannot be null")
    @Positive(message = "Line Number must be a positive number")
    private Integer lineNum;

    @NotEmpty(message = "Item Code cannot be empty")
    @NotNull(message = "Item Code cannot be null")
    private String itemCode;

    @NotEmpty(message = "Item Description cannot be empty")
    @NotNull(message = "Item Description cannot be null")
    private String itemDescription;

    @NotEmpty(message = "Line Item Description cannot be empty")
    @NotNull(message = "Line Item Description cannot be null")
    private String lineItemDescription;

    @NotEmpty(message = "Unit cannot be empty")
    @NotNull(message = "Unit cannot be null")
    private String unit;

    @NotNull(message = "Unit Price cannot be null")
    @Positive(message = "Unit Price must be a positive number")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @NotNull(message = "Line Amount cannot be null")
    @Positive(message = "Line Amount must be a positive number")
    private BigDecimal lineAmount;

    @NotEmpty(message = "Budget Account cannot be empty")
    @NotNull(message = "Budget Account cannot be null")
    private String budgetAccount;

    @NotEmpty(message = "Segment 6 Description cannot be empty")
    @NotNull(message = "Segment 6 Description cannot be null")
    private String segment6Desc;

    @NotNull(message = "Deliver To Person ID cannot be null")
    @Positive(message = "Deliver To Person ID must be a positive number")
    private Long purchaseDeliverToPersonId;

    @NotNull(message = "PO Date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date purchasePoDate;

    @NotEmpty(message = "Department cannot be empty")
    @NotNull(message = "Department cannot be null")
    private String department;

    private MultipartFile poFile;

}
