package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.dto.ReminderAdvanceFilterRequest;
import com.allweb.rms.entity.jpa.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ReminderAdvanceFilterRepository {

  Page<Reminder> findByAdvanceFilters(ReminderAdvanceFilterRequest request, Pageable pageable);
}
