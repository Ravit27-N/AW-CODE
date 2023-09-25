package com.tessi.cxm.pfl.ms5.dto;

/**
 * This projection used to load id of organization.
 *
 * @author Sokhour LACH
 * @since 24/12/2021
 */
public interface LoadOrganization {
  long getServiceId();

  long getDivisionId();

  long getClientId();
}
