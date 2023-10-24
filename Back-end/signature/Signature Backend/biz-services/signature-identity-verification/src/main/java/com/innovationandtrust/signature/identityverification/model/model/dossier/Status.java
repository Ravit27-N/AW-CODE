package com.innovationandtrust.signature.identityverification.model.model.dossier;

/** Enum class for status. */
public enum Status {
  ACTIVE,
  FAILED,
  BANNED,
  RETRY,
  EXCEEDED,
  VERIFIED, //Verified phone number
  DOCUMENT_VERIFIED
}
