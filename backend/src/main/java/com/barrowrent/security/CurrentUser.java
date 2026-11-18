package com.barrowrent.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static AppUserPrincipal get() {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                    instanceof AppUserPrincipal principal)) {

            throw new AccessDeniedException(
                "Authentication required"
            );
        }

        return principal;
    }

    public static Long getId() {
        return get().getUserId();
    }
}