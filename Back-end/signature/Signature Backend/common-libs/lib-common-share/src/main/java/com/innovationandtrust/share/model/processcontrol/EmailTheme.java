package com.innovationandtrust.share.model.processcontrol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailTheme {
    private String companyName;
    private String mainColor;
    private Resource logoFile;
}
