package com.innovationandtrust.utils.file.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileInfo {
    private String fileName;
    private String size;
    private String lastModified;
    private Boolean hidden;
}
