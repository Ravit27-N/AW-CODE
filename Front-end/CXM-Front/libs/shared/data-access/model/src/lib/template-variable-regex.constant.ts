import { EMAILING, SMS } from './cxm-templates';

export const TEMPLATE_VARIABLE_REGEX = /([{])+([^}]+)+([}])/g;

export const SMS_KEY_REGEX = new RegExp('{' + SMS.key + '}', 'g'); // Result: /{SMS.key}/g
export const SMS_VALUE_REGEX = new RegExp('{' + SMS.value + '}', 'g'); // Result: /{SMS.value}/g
export const SMS_KEY = '{' + SMS.key + '}';
export const SMS_VALUE = '{' + SMS.value + '}';

export const EMAILING_KEY_REGEX = new RegExp('{' + EMAILING.key + '}', 'g'); // Result: /{EMAILING.key}/g
export const EMAILING_VALUE_REGEX = new RegExp('{' + EMAILING.value + '}', 'g'); // Result: /{EMAILING.value}/g
export const EMAILING_KEY = '{' + EMAILING.key + '}';
export const EMAILING_VALUE = '{' + EMAILING.value + '}';
