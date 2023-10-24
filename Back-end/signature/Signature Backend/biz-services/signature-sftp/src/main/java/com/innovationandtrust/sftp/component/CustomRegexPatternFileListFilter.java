package com.innovationandtrust.sftp.component;

import java.io.File;
import java.util.regex.Pattern;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;

public class CustomRegexPatternFileListFilter extends RegexPatternFileListFilter {

  public CustomRegexPatternFileListFilter(Pattern pattern) {
    super(pattern);
  }

  @Override
  protected String getFilename(File file) {
    return file.getAbsolutePath();
  }
}
