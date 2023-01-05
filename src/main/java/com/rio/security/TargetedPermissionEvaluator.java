package com.rio.security;

import com.rio.entity.Permission;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

public abstract class TargetedPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return userDetails.getAuthorities().stream()
                    .map(Permission.class::cast)
                    .filter(p -> targetDomainObject.getClass().getSimpleName().equals(p.getTargetDomainObject()))
                    .filter(p -> p.getTargetDomainObjectId() != null && Long.valueOf(p.getTargetDomainObjectId()).equals(getId(targetDomainObject)))
                    .anyMatch(p -> p.getPermission().equals(permission));
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getAuthorities().stream()
                .map(Permission.class::cast)
                .filter(p -> targetType.equals(p.getTargetDomainObject()))
                .filter(p -> p.getTargetDomainObjectId() != null && Long.valueOf(p.getTargetDomainObjectId()).equals(targetId))
                .anyMatch(p -> p.getPermission().equals(permission));
    }

    public abstract Object getId(Object targetDomainObject);
}
