export enum UserConstantUrl {
  LIST = '/cxm-profile/users/list-user',
  CREATE = 'cxm-profile/users/create-user',
  EDIT = 'cxm-profile/users/update-user',
}

export enum UserLocalStorageConstant {
  UPDATE_BATCH_USER = 'modificationUser'
}

export enum EditUserMode {
  BATCH = 'batch',
  SINGLE = 'single'
}

export enum UserFormProperties {
  FIRST_NAME = 'firstName',
  LAST_NAME = 'lastName',
  EMAIL = 'email',
  PASSWORD = 'password',
  CONFIRMED_PASSWORD = 'confirmedPassword',
  CLIENT_ID = 'clientId',
  SERVICE_ID = 'serviceId',
  PROFILES = 'profiles'
}

export declare type UserFormModel =
  UserFormProperties.FIRST_NAME |
  UserFormProperties.LAST_NAME |
  UserFormProperties.EMAIL |
  UserFormProperties.PASSWORD |
  UserFormProperties.CONFIRMED_PASSWORD |
  UserFormProperties.CLIENT_ID |
  UserFormProperties.SERVICE_ID |
  UserFormProperties.PROFILES;

export enum UserFormErrorMessages {
  FIRST_NAME_REQUIRED = 'user.form.errors.firstName',
  FIRST_NAME_INVALID_LENGTH = 'user.form.errors.maxLength',
  FIRST_NAME_INVALID_PATTERN = 'user.form.errors.invalidFirstName',

  LAST_NAME_REQUIRED = 'user.form.errors.lastName',
  LAST_NAME_INVALID_LENGTH = 'user.form.errors.maxLength',
  LAST_NAME_INVALID_PATTERN = 'user.form.errors.invalidLastName',

  EMAIL_REQUIRED = 'user.form.errors.email',
  EMAIL_INVALID_LENGTH = 'user.form.errors.maxLength',
  EMAIL_INVALID_PATTERN = 'user.form.errors.invalidEmail',
  EMAIL_DUPLICATED = 'user.form.errors.duplicatedEmail',

  PASSWORD_REQUIRED = 'user.form.errors.password',
  PASSWORD_INVALID_LENGTH = 'user.form.errors.maxLength',

  CONFIRMED_PASSWORD_NOT_EQUAL_TO_PASSWORD = 'user.form.errors.confirmPassword',
  CONFIRMED_PASSWORD_INVALID_LENGTH = 'user.form.errors.maxLength',

  CLIENT_ID_REQUIRED = 'user.form.errors.clientId',
  SERVICE_ID_REQUIRED = 'user.form.errors.serviceId',

  PROFILE_IDS_REQUIRED = 'user.form.errors.profile'
}

export declare type UserFormErrorMessagesModel =
  UserFormErrorMessages.FIRST_NAME_REQUIRED |
  UserFormErrorMessages.FIRST_NAME_INVALID_LENGTH |
  UserFormErrorMessages.FIRST_NAME_INVALID_PATTERN |
  UserFormErrorMessages.LAST_NAME_REQUIRED |
  UserFormErrorMessages.LAST_NAME_INVALID_LENGTH |
  UserFormErrorMessages.LAST_NAME_INVALID_PATTERN |
  UserFormErrorMessages.EMAIL_REQUIRED |
  UserFormErrorMessages.EMAIL_INVALID_LENGTH |
  UserFormErrorMessages.EMAIL_INVALID_PATTERN |
  UserFormErrorMessages.EMAIL_DUPLICATED |
  UserFormErrorMessages.PASSWORD_REQUIRED |
  UserFormErrorMessages.PASSWORD_INVALID_LENGTH |
  UserFormErrorMessages.CONFIRMED_PASSWORD_NOT_EQUAL_TO_PASSWORD |
  UserFormErrorMessages.CONFIRMED_PASSWORD_INVALID_LENGTH |
  UserFormErrorMessages.CLIENT_ID_REQUIRED |
  UserFormErrorMessages.SERVICE_ID_REQUIRED |
  UserFormErrorMessages.PROFILE_IDS_REQUIRED;

export enum UserFormUpdateMode {
  CREATE = 'c2RzZ53h2mQ',
  UPDATE_SINGLE = 'c2RzZmRhZmQ',
  UPDATE_MULTIPLE = 'd2V3ZWVmaG5le'
}

export enum UserFormActionType {
  CREATE = "CREATE",
  UPDATE = "UPDATE",
  OTHER = "OTHER"
}

export declare type UserFormUpdateModel =
  UserFormUpdateMode.UPDATE_SINGLE |
  UserFormUpdateMode.UPDATE_MULTIPLE |
  UserFormUpdateMode.CREATE;
