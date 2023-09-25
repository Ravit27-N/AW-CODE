package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.MailConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MailConfigurationAdvanceFilterRepository {
  int insertToMailConfigure(MailConfiguration config);

  void insertToMailConfigCC(MailConfiguration config);

  void insertToMailConfigTo(MailConfiguration config);

  Page<MailConfiguration> advanceFilters(Pageable page, String filter, String selectType);
}
