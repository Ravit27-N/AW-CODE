package com.innovationandtrust.profile.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TemplateFolder {
    private Long id;
    private String unitName;
    private Long businessUnitId;
    private Long createdBy;
    private Object templates;
    private int countTemplates;
}
