package com.innovationandtrust.profile.model.projection;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyListInfo {
    String name;
    @Getter String siret;
    @Getter String uuid;
    @Getter Date createdAt;
    @Getter Date modifiedAt;
    @Getter
    private Long id;
}
