import {ICountry} from '@/components/ng-phone/type';

export type Sort = 'asc' | 'desc';
export const Ascending: Sort = 'asc';
export const Descending: Sort = 'desc';
export const EntryPerPage = 10;
export const RowsPerPageOptions = [10, 20, 50, 100];
/** This enumeration is associate with key of i18n, if you want to modify these
 value, make sure to right with key on I18n. **/
export enum Participant {
  Approval = 'approval',
  Signatory = 'signatory',
  Receipt = 'receipt',
  Viewer = 'viewer',
}
export const defaultColor = '#D6056A';

// local storage key
export const i18nKey = 'i18nextLng';
export const refreshTokenKey = 'refresh_token';
export const currentProjectIdKey = 'current_project_id';
export const isLogoutKey = 'isLogout';

export const tokenRoleName = 'token&role&name';
export const LuApproval = 'Lu et approuvé';

/**
 * It's key for transactionId header in Gravitee for tracking usage of corporate or end user.
 * */

export const GraviteeTransactionIdKey = 'X-Gravitee-Transaction-Id';

/**
 * four steps of creation project
 * */
export enum STEP {
  STEP1,
  STEP2,
  STEP3,
  STEP4,
}

export type TypeSTep = STEP;
export enum FONT_TYPE {
  TAHOMA = 'Tahoma',
  POPPINS = 'poppins',
  FREDOKA = 'fredoka',
  VERDANA_BOLD_ITALIC = 'verdana-bold-italic',
  VERDANA_BOLD_BOLD = 'verdana-bold',
  VERDANA_BOLD = 'verdana',
}

export enum SIGNING_PROCESS {
  APPROVAL = 'approval',
  ORDER = 'order',
  LEGAL = 'legal',
  INDIVIDUAL_SIGN = 'individual-sign',
  COSIGN = 'cosign',
  ORDERED_COSIGN = 'ordered-cosign',
  COUNTER_SIGN = 'countersign',
}

/**
 * It is used for invitation status and documentation status
 * */
export enum InvitationStatus {
  SIGNED = 'SIGNED',
  APPROVED = 'APPROVED',
  RECEIVED = 'RECEIVED',
  REFUSED = 'REFUSED',
  SENT = 'SENT',
  ON_HOLD = 'ON_HOLD',
  IN_PROGRESS = 'IN_PROGRESS',
  READ = 'READ',
}

export type ChannelOptions = 1 | 2 | 3;
export type AutoReminder = 1 | 2 | 3 | 4;

export enum EnumChannelOptions {
  SMS = 1,
  EMAIL,
  SMS_EMAIL,
}

export enum EnumAutoReminder {
  'ONE_TIME_PER_DAY' = 1,
  'EVERY_TWO_DAYS',
  'ONE_TIME_PER_WEEK',
  'EVERY_TWO_WEEKS',
}

export enum SIZE_FILE_UPLOAD {
  MIN = 40000,
  MAX = 500000,
}

export enum DIMENSIONS_LOGO_UPLOAD {
  // in pixel
  MIN_WIDTH = 500,
  MIN_HEIGHT = 500,
  MAX_WIDTH = 2000,
  MAX_HEIGHT = 2000,
}

export enum HistoryStatus {
  CREATED = 'CREATED',
  APPROVED = 'APPROVED',
  SENT = 'SENT',
  SIGNED = 'SIGNED',
  COMPLETED = 'COMPLETED',
  REFUSED = 'REFUSED',
  EXPIRED = 'EXPIRED',
  READ = 'READ',
}

export enum ProjectStatus {
  COMPLETED = 'COMPLETED',
  DRAFT = 'DRAFT',
  EXPIRED = 'EXPIRED',
  REFUSED = 'REFUSED',
  IN_PROGRESS = 'IN_PROGRESS',
}

export enum DocumentStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  REFUSED = 'REFUSED',
  SIGNED = 'SIGNED',
  APPROVED = 'APPROVED',
  RECEIVED = 'RECEIVED',
  READ = 'READ',
}
export const OtpLength = 6;
export const trailPhoneLength = 4;
export const COLOR_THEME_ARRAY = [
  '#7065DF',
  '#AF6107',
  '#D83A30',
  '#D3355D',
  '#D6056A',
  '#486BD9',
  '#187AC1',
  '#147F98',
  '#2C8377',
  '#2A8833',
  '#894242',
  '#75737F',
  '#202020',
];
export const COLOR_THEME_FIGMA = {
  default: COLOR_THEME_ARRAY[4],
  purple: COLOR_THEME_ARRAY[0],
};

export const SnackBarTimeout = 5000;

type IDateInFrench = {
  [k: string]: string;
};

export const dateInFrench: IDateInFrench = {
  January: 'janvier',
  February: 'février',
  March: 'mars',
  April: 'avril',
  May: 'mai',
  June: 'juin',
  July: 'juillet',
  August: 'août',
  September: 'septembre',
  October: 'octobre',
  November: 'novembre',
  December: 'décembre',
};

export const dayInFrench: IDateInFrench = {
  Monday: 'lundi',
  Tuesday: 'mardi',
  Wednesday: 'mercredi',
  Thursday: 'jeudi',
  Friday: 'vendredi',
  Saturday: 'samedi',
  Sunday: 'dimanche',
};
export const ClosePage = false;

/**
 * It is used to search function.
 * Creates a debounced function that delays invoking func until after wait milliseconds
 * have elapsed since the last time the debounced function was invoked.
 */
export const STOP_TYPING_TIMEOUT = 500;
export enum FilterBy {
  COMPANY = 'company',
  DEPARTMENT = 'business-unit',
  ENDUSER = 'user',
}

export const UNKOWNERROR ="Nous sommes désolés, mais nous n'arrivons pas à traiter votre demande. Veuillez réessayer plus tard."
// "We're sorry, but we're having trouble processing your request. Please try again later.";

export enum CreateModelStep {
  STEP1 = 1,
  STEP2,
}
export const GroupAvtarVisible = 4;
export enum SignatureLevelType {
  simple = 'SIMPLE',
  advance = 'ADVANCE',
  qualify = 'QUALIFY',
}

export const DEFAULT_PHONE_COUNTRY: ICountry = {
  callingCode: '33',
  name: 'France',
  code: 'FR',
};
