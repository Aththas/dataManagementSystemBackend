package com.mobitel.data_management.entity;

import com.mobitel.data_management.auth.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Amc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contractName;
    private String existingPartner;
    private String userDivision;
    private double initialCostUSD;
    private double initialCostLKR;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private double amcValueUSD;
    private double amcValueLKR;
    private double amcPercentageUponPurchasePrice;
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
