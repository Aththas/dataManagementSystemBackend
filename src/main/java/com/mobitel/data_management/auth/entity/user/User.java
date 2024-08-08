package com.mobitel.data_management.auth.entity.user;

import com.mobitel.data_management.auth.entity.token.Token;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.entity.Po;
import com.mobitel.data_management.entity.UserActivityAmc;
import com.mobitel.data_management.entity.UserActivityPo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String grpName;
    private boolean viewPermission;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @OneToMany(mappedBy = "user")
    private List<Amc> amcs;

    @OneToMany(mappedBy = "user")
    private List<UserActivityAmc> userActivityAmcs;

    @OneToMany(mappedBy = "user")
    private List<Po> pos;

    @OneToMany(mappedBy = "user")
    private List<UserActivityPo> userActivityPos;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
