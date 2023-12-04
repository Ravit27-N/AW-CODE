package com.innovationandtrust.process.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstant {
  // Project invitation messages
  public static final String APPROVAL_INVITE_SUBJECT =
      "Invitation à approuver le(s) document(s) du projet";
  public static final String APPROVAL_INVITE_MESSAGE =
      "Vous êtes invité(e) à approuver le(s) document(s) du projet";
  public static final String APPROVAL_INVITE_THEME = "#D6056A";
  public static final String SIGNER_INVITE_SUBJECT =
      "Invitation à signer le(s) document(s) du projet";
  public static final String SIGNER_INVITE_MESSAGE =
      "Vous êtes invité(e) à signer le(s) document(s) du projet";
  public static final String SIGNER_INVITE_THEME = "#D6056A";
  public static final String VIEWER_INVITE_SUBJECT =
      "Invitation à consulter le(s) document(s) du projet";
  public static final String VIEWER_INVITE_MESSAGE =
      "Vous êtes invités à consulter le(s) document(s) de projet";
  public static final String RECIPIENT_INVITE_SUBJECT =
      "Invitation à télécharger le(s) document(s) du projet";
  public static final String RECIPIENT_INVITE_MESSAGE =
      "Vous êtes invité(e) à télécharger le(s) document(s) du projet";

  // Project reminder messages
  public static final String APPROVAL_INVITE_REMINDER_SUBJECT =
      "Relance : n’oubliez pas de approver votre document";
  public static final String SIGNER_INVITE_REMINDER_SUBJECT =
      "Relance : n’oubliez pas de signer votre document";

  // Project expiration date modification messages
  public static final String APPROVAL_INFORM_MODIFY_EXP =
      "Nouvelle date d'échéance de votre Approbation";
  public static final String SIGNER_INFORM_MODIFY_EXP =
      "Nouvelle date d'échéance de votre Signature";
  public static final String SIGN_COMPLETED_SUBJECT = "Téléchargement le(s) document(s) final(aux)";
  public static final String SIGN_COMPLETED_MESSAGE =
      "Tous les signataires ont signé le(s) document(s). Vous pouvez télécharger le(s) document(s) final(aux).";
  public static final String VIDEO_ACCEPTED_SUBJECT = "Identité vérifiée idée avec succès";
  public static final String VIDEO_ACCEPTED_MESSAGE =
      "Pour consulter le(s) document(s), cliquez sur le bouton ci-dessous:";
  public static final String VIDEO_REJECTED_SUBJECT = "Echec de vérification d’identité";
  public static final String VIDEO_REJECTED_MESSAGE =
      "Nous vous informons que la vérification de votre identité a échoué. Veuillez contacter votre opérateur.";
}
