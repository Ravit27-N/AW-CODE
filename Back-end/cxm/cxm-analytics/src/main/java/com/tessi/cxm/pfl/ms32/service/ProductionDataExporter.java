package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.shared.core.Context;
import java.io.IOException;
import java.io.Writer;

public interface ProductionDataExporter {
  String getKey();

  void export(Context context, Writer writer) throws IOException;
}
