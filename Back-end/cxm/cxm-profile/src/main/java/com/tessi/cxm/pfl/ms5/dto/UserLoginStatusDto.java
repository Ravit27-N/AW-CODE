package com.tessi.cxm.pfl.ms5.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserLoginStatusDto {
    
    private boolean isFirstLogin;
    private long daysSinceLastLogin;

}