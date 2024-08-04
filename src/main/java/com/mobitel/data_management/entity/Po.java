package com.mobitel.data_management.entity;

import com.mobitel.data_management.auth.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Po {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long poNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date poCreationDate;

    private String poType;
    private String vendorName;
    private String vendorSiteCode;
    private String poDescription;
    private String approvalStatus;
    private String currency;
    private BigDecimal amount;
    private BigDecimal matchedAmount;
    private String buyerName;
    private String closureStatus;
    private String prNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date prCreationDate;

    private Long requisitionHeaderId;
    private String requesterName;
    private String requesterEmpNum;
    private Integer lineNum;
    private String itemCode;
    private String itemDescription;
    private String lineItemDescription;
    private String unit;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineAmount;
    private String budgetAccount;
    private String segment6Desc;
    private Long purchaseDeliverToPersonId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date purchasePoDate;

    private String department;

    private String poFile;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
