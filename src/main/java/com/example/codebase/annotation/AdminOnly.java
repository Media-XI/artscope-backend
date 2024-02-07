package com.example.codebase.annotation;

import jakarta.persistence.Table;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
public @interface AdminOnly {

}
