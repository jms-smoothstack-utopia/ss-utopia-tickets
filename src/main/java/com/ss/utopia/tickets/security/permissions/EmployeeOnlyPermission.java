package com.ss.utopia.tickets.security.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
public @interface EmployeeOnlyPermission {

}
