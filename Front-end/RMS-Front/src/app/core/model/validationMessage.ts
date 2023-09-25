export interface IValidationMessage{
  requied?: string;
  email?: {
    required?: string;
    partern?: string;
    exist?: string;
  };
  telephone?: {
    minLength?: string;
    maxLength?: string;
    partern?: string;
  };
  gpa?: {
    max?: string;
    min?: string;
  };
}

export const VALIDATION_MESSAGE: IValidationMessage = {
    requied: 'This field has required !',
    email:{
      required: 'Email has required !',
      partern: 'Please input a valid email ! ',
      exist: 'Email has already exist.'
    },
    telephone: {
      minLength: 'Telephones should have 9 charactes up',
      maxLength: '',
      partern: 'Please enter a valid telephone !'
    },
    gpa: {
      max: 'Gpa must smaller or equal 4.0 !',
      min: 'Gpa must bigger of equal 0 !'
    }
  };

