package com.innovationandtrust.share.model.corporateprofile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BusinessUnitRes implements Serializable {
        private Long id;
        @NotEmpty
        private String unitName;
        private Integer sortOrder;
        @Min(1)
        private Long companyDetailId;
}
