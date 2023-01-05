package com.rio.security;

import com.rio.entity.Event;
import com.rio.entity.Permission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

public class EventPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return userDetails.getAuthorities().stream()
                    .map(Permission.class::cast)
                    .anyMatch(p -> p.getValue().equals(permission) && p.getEvent().getId().equals(((Event) targetDomainObject).getId()));
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getAuthorities().stream()
                .map(Permission.class::cast)
                .anyMatch(p -> p.getValue().equals(permission) && p.getEvent().getId().equals(Long.valueOf(targetId.toString())));
    }
}
