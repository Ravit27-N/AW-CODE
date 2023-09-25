export enum TemplateType{
  EMAILING = 'EMAILING',
  SMS = 'SMS'
}

export const EMAILING = {
  key: 'email', // value that store in DB, Noted: this key cannot change.
  value: 'Email' // value that used in frontend.
}

export const SMS = {
  key: 'TelDestinataire', // value that store in DB, Noted: this key cannot change.
  value: 'Numéro destinataire' // value that used in frontend.
}

export const getVariableKey = (type: string): string[] => {
  return type.toUpperCase() === TemplateType.EMAILING ? [EMAILING.key] : [SMS.key];
}

export const getVariableValue = (type: string): string[] => {
  return type.toUpperCase() === TemplateType.EMAILING ? [EMAILING.value] : [SMS.value];
}

