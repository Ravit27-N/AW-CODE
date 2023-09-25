export interface ErrorValidationDirectiveModel {
  required?: boolean;
  maximum?: boolean;
  minimum?: boolean;
  mask?: boolean;
  dateType?: boolean;
  duplicate?: boolean;
  // TODO: can add other properties for your business logic.
}
