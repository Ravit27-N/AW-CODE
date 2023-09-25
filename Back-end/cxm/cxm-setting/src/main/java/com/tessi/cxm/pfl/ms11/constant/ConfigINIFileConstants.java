package com.tessi.cxm.pfl.ms11.constant;

import java.util.List;

public final class ConfigINIFileConstants {

  private ConfigINIFileConstants() {
  }

  public static final String CONFIG_INI = "config.ini";
  public static final String CONFIG_PORTAL_INI = "config_portail.ini";
  public static final String PORENILIBRE = "PORENILIBRE";

  public static final String DEFAULT_CONFIG = "DEFAULT";
  public static final String PORTAIL_CONFIG = "PORTAIL";
  public static final String PORTAIL_ANALYSE_CONFIG = "PORTAIL_ANALYSE";
  public static final String PORTAIL_PREVIEW_CONFIG = "PORTAIL_PREVIEW";
  public static final String SEPARATOR_CHARS = "=";

  public static final String PATH_INI_KEY = "PathIni";
  public static final String VAR_CLIENT_CONFIG_PATH = "${clientConfigPath}";
  public static final String VAR_WORKING_PATH = "${workingPath}";
  public static final String VAR_ENRICHMENT_PATH = "${enrichmentPath}";
  public static final String MODELE_KEY = "Modele";

  public static final List<String> DEFAULT_INI_CONFIG_MODELS =
      List.of(DEFAULT_CONFIG, PORTAIL_CONFIG, PORTAIL_ANALYSE_CONFIG, PORTAIL_PREVIEW_CONFIG);
}
