package com.riskassessment.auth.dto.response;

import com.riskassessment.auth.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private Long tenantId;
    private String tenantName;
    private Boolean isActive;
}
