package com.innovationandtrust.profile.component;

import com.innovationandtrust.profile.model.dto.NormalUserDto;
import org.springframework.batch.item.ItemProcessor;

public class UserProcessor implements ItemProcessor<NormalUserDto, NormalUserDto> {
  @Override
  public NormalUserDto process(NormalUserDto item) {
    // validate email, phone, businessUnitId, userAccessId
    return item;
  }
}
