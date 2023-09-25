package com.tessi.cxm.utils;

/**
 * Utility class of gateway.
 *
 * @author Sokhour LACH
 * @since 02/12/2021
 */
public final class GatewayUtils {
  private GatewayUtils() {
    // nothing to here
  }

  public static final String[] GATEWAY_CIRCUIT_BREAKER = {
      "cxm-acquisition",
      "cxm-analytics",
      "cxm-campaign",
      "cxm-composition",
      "cxm-directory",
      "cxm-file-control-management",
      "cxm-file-manager",
      "cxm-flow-traceability",
      "cxm-go2pdf",
      "cxm-hub-digitalflow",
      "cxm-process-control",
      "cxm-processing",
      "cxm-production",
      "cxm-profile",
      "cxm-setting",
      "cxm-switch",
      "cxm-template",
      "default"
  };
}
