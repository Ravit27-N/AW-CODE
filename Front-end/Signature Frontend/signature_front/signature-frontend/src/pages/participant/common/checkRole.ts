import {Participant} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {router} from '@/router';

// Validate file invitation
export const validateRoleView = (role: string) => {
  let goto: null | string = null;
  switch (role) {
    case Participant.Approval:
      goto = Route.participant.approveInvite;
      break;
    case Participant.Signatory:
      goto = Route.participant.root;
      break;
    case Participant.Receipt:
      goto = Route.participant.recipientInvite;
      break;
  }

  return goto;
};

export const validateRoleApprove = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewerInvite;
      break;
    case Participant.Signatory:
      goto = Route.participant.root;
      break;
    case Participant.Receipt:
      goto = Route.participant.recipientInvite;
      break;
  }

  return goto;
};

export const validateRoleSign = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewerInvite;
      break;
    case Participant.Approval:
      goto = Route.participant.approveInvite;
      break;
    case Participant.Receipt:
      goto = Route.participant.recipientInvite;
      break;
  }

  return goto;
};

export const validateRoleReceipt = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewerInvite;
      break;
    case Participant.Approval:
      goto = Route.participant.approveInvite;
      break;
    case Participant.Signatory:
      goto = Route.participant.root;
      break;
  }

  return goto;
};

// Validate file view
export const validateFileRoleView = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Receipt:
      goto = Route.participant.viewRecipientFile;
      break;
    case Participant.Signatory:
      goto = Route.participant.viewSignatoryFile;
      break;
    case Participant.Approval:
      goto = Route.participant.viewApproveFile;
      break;
  }

  return goto;
};

export const validateFileRoleApprove = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Receipt:
      goto = Route.participant.viewRecipientFile;
      break;
    case Participant.Signatory:
      goto = Route.participant.viewSignatoryFile;
      break;
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
  }

  return goto;
};

/** Validate signature access identity */
export const validateSignatureAccessIdentity = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Receipt:
      goto = Route.participant.recipientInvite;
      break;
    case Participant.Viewer:
      goto = Route.participant.viewerInvite;
      break;
    case Participant.Approval:
      goto = Route.participant.approveInvite;
      break;
  }

  return goto;
};

export const validateFileRoleSign = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Receipt:
      goto = Route.participant.viewRecipientFile;
      break;
    case Participant.Approval:
      goto = Route.participant.viewApproveFile;
      break;
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
  }

  return goto;
};

export const validateFileRoleReceipt = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Signatory:
      goto = Route.participant.viewSignatoryFile;
      break;
    case Participant.Approval:
      goto = Route.participant.viewApproveFile;
      break;
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
  }

  return goto;
};

// Validate file download
export const validateDownloadRoleApprove = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
    case Participant.Receipt:
      goto = Route.participant.receiptDocument;
      break;
    case Participant.Signatory:
      goto = Route.participant.signDocument;
      break;
  }

  return goto;
};

export const validateDownloadRoleSign = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
    case Participant.Receipt:
      goto = Route.participant.receiptDocument;
      break;
    case Participant.Approval:
      goto = Route.participant.approveDocument;
      break;
  }

  return goto;
};

export const validateDownloadRoleReceipt = (role: string) => {
  let goto: null | string = null;

  switch (role) {
    case Participant.Viewer:
      goto = Route.participant.viewDocument;
      break;
    case Participant.Signatory:
      goto = Route.participant.signDocument;
      break;
    case Participant.Approval:
      goto = Route.participant.approveDocument;
      break;
  }

  return goto;
};

// Sign document role check
export const signDocumentRoleCheck = (
  role: string,
  id: string,
  queryParameters: URLSearchParams,
) => {
  let goto = `${Route.participant.viewSignatoryFile}/${id}?${queryParameters}`;
  switch (role) {
    case Participant.Approval:
      goto = `${Route.participant.viewApproveFile}/${id}?${queryParameters}`;
      break;
    case Participant.Receipt:
      goto = `${Route.participant.viewRecipientFile}/${id}?${queryParameters}`;
      break;
    case Participant.Viewer:
      goto = `${Route.participant.viewDocument}/${id}?${queryParameters}`;
  }

  router.navigate(goto);
};

// Expired link role check
export const expiredLinkRoleCheck = (
  role: string,
  id: string,
  queryParameters: URLSearchParams,
) => {
  let goto = `${Route.participant.root}/${id}?${queryParameters}`;
  switch (role) {
    case Participant.Approval:
      goto = `${Route.participant.approveInvite}/${id}?${queryParameters}`;
      break;
    case Participant.Receipt:
      goto = `${Route.participant.recipientInvite}/${id}?${queryParameters}`;
      break;
    case Participant.Viewer:
      goto = `${Route.participant.viewerInvite}/${id}?${queryParameters}`;
  }

  router.navigate(goto);
};
