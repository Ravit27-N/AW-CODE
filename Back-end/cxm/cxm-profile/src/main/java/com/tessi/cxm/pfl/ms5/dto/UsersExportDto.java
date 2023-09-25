package com.tessi.cxm.pfl.ms5.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersExportDto implements Serializable {
    private String client;
    private String division;
    private String service;
    private String lastName;
    private String firstName;
    private String email;
    private List<UserAssignedProfileDTO> profiles;
    private LocalDateTime lastLoginDateTime;
    
}
