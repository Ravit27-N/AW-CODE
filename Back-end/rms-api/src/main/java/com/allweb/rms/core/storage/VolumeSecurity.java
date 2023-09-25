package com.allweb.rms.core.storage;

import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

public interface VolumeSecurity {

  String getVolumePattern();

  SecurityConstraint getSecurityConstraint();
}
