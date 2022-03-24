package ru.jtc.moneytrans.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "role_signature")
    private String roleSignature;
    @Column(name = "role_name")
    private String roleName;

    @Override
    public String getAuthority() {
        return getRoleSignature();
    }
}
