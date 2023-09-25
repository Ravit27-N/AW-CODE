package com.tessi.cxm.pfl.ms3.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public interface DocumentCsvProjection extends Serializable {
    Long getId_document();
    Date getDate_production();
    Date getDate_distribution();
    String getCanal();
    String getCategorie();
    Integer getRecto_verso();
    Integer getNb_pages();
    Integer getNb_feuilles();
    Integer getCouleur();
    String getCode_postal();
    String getEnveloppe_reelle();
    String getCompagnie();
    String getId_dest();
    String getFiller_1();
    String getFiller_2();
    String getFiller_3();
    String getFiller_4();
    String getFiller_5();
    String getStatut();
    String getDoc_name();
    String getDuree_archivage();
    Long getType_agrafe();
    String getUrgence_reelle();
    BigDecimal getPoids();
    String getTranche_reelle();
    Float getAffranchissement();
    String getService();
    String getZone_geo();
    String getCode_pays();
    String getNom_prestataire();
}
