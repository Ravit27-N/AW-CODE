package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import java.io.IOException;

public interface BatchUserResource {

  UserRecord read() throws IOException;

  void close() throws IOException;
}
