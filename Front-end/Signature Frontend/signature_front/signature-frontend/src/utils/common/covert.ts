/** convert form number of remainder to text as sms, email , ... **/
export const RemainOption = (key: number) => {
  switch (key) {
    case 1: {
      return 'sms';
    }
    case 2: {
      return 'email';
    }
    case 3: {
      return 'sms_email';
    }
  }
  return 'null';
};

export const RemainOptionToNumber = (key: string) => {
  switch (key) {
    case 'sms': {
      return 1;
    }
    case 'email': {
      return 2;
    }
    case 'sms_email': {
      return 3;
    }
  }
};
