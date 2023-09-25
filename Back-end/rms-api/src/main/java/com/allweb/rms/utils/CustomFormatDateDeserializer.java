package com.allweb.rms.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomFormatDateDeserializer extends StdDeserializer<Date> {

  protected CustomFormatDateDeserializer() {
    super(Date.class);
  }

  @Override
  public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String formatPattern = "dd-MM-yyyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern);
    DateDeserializers.DateDeserializer deserializer =
        new DateDeserializer(DateDeserializer.instance, dateFormat, formatPattern);
    return deserializer.deserialize(p, ctxt);
  }
}
