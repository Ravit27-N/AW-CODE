package com.tessi.cxm.pfl.ms5.entity.converter;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import javax.persistence.AttributeConverter;
import org.springframework.util.StringUtils;

public class AddressTypeAttributeConverter implements AttributeConverter<AddressType, String> {

  @Override
  public String convertToDatabaseColumn(AddressType attribute) {
    if (attribute != null) {
      return attribute.getValue();
    }
    return null;
  }

  @Override
  public AddressType convertToEntityAttribute(String dbData) {
    if (StringUtils.hasText(dbData)) {
      return Enum.valueOf(AddressType.class, dbData.toUpperCase());
    }
    return null;
  }
}
