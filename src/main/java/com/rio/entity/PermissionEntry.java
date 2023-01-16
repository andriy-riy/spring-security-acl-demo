package com.rio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class PermissionEntry implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String permission;
    @ManyToOne
    private Event event;
    @ManyToMany(mappedBy = "permissionEntries")
    private List<User> users = new ArrayList<>();

    @Override
    public String getAuthority() {
        return permission;
    }
}
