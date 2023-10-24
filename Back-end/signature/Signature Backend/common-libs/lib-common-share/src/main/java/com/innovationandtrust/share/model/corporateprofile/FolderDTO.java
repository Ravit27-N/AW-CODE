package com.innovationandtrust.share.model.corporateprofile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FolderDTO implements Serializable {
    private Long id;
    private String unitName;
    private Long businessUnitId;
    private Long createdBy;
}
