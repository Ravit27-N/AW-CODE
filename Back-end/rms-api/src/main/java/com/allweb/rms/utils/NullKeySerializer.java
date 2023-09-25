package com.allweb.rms.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class NullKeySerializer extends StdSerializer<Object> {

  private static final long serialVersionUID = 1L;

  public NullKeySerializer() {
    this(null);
  }

  protected NullKeySerializer(Class<Object> t) {
    super(t);
  }

  @Override
  public void serialize(Object value, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeFieldName("");
  }
}
