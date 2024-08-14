package com.mobitel.data_management.auth.entity.token;

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
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String accessToken;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


}
