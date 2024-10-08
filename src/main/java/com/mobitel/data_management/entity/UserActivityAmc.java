package com.mobitel.data_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mobitel.data_management.auth.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserActivityAmc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String version;
    private String action;
    private String description;
    private String beforeFile;
    private String afterFile;
    private String rowBefore;
    private String rowAfter;
    private String dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
