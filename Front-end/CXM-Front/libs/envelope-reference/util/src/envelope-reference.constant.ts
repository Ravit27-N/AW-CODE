import {Format} from "../../data-access/src/lib/models";

export enum EnvelopeReferenceFormProperties {
  REFERENCE = 'reference',
  ACTIVE = 'active',
  FORMAT = 'format',
  DESCRIPTION = 'description'
}


export declare type EnvelopeReferenceFormModel =
  EnvelopeReferenceFormProperties.ACTIVE |
  EnvelopeReferenceFormProperties.FORMAT |
  EnvelopeReferenceFormProperties.DESCRIPTION |
  EnvelopeReferenceFormProperties.REFERENCE

export enum EnvelopeReferenceFormUpdateMode {
  CREATE = 'c2RzZ53h2mQ',
  UPDATE_SINGLE = 'c2RzZmRhZmQ',
  UPDATE_MULTIPLE = 'd2V3ZWVmaG5le'
}

export const formats: any[] = Object.keys( Format).map(element => ({key:element,value:element, val:element}))

export declare type EnvelopeReferenceFormUpdateModel =
  EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE |
  EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE |
  EnvelopeReferenceFormUpdateMode.CREATE;


export enum EnvelopeReferenceFormErrorMessages {
  REFERENCE_REQUIRED = 'envelope_reference.form.errors.reference',
  REFERENCE_DUPLICATE = 'envelope_reference.form.errors.referenceDuplicate',
  REFERENCE_INVALID_LENGTH = 'envelope_reference.form.errors.maxLength30',
  FORMAT_REQUIRED = 'envelope_reference.form.errors.format',
  DESCRIPTION_REQUIRED = 'envelope_reference.form.errors.description',
  DESCRIPTION_INVALID_LENGTH = 'envelope_reference.form.errors.maxLength',

}




export declare type EnvelopeReferenceFormErrorMessagesModel =
  EnvelopeReferenceFormErrorMessages.REFERENCE_REQUIRED |
  EnvelopeReferenceFormErrorMessages.REFERENCE_INVALID_LENGTH |
  EnvelopeReferenceFormErrorMessages.FORMAT_REQUIRED |
  EnvelopeReferenceFormErrorMessages.DESCRIPTION_REQUIRED |
  EnvelopeReferenceFormErrorMessages.DESCRIPTION_INVALID_LENGTH |
  EnvelopeReferenceFormErrorMessages.REFERENCE_DUPLICATE



